/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.core.handler;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.*;
import vazkii.psi.api.event.PsiEventBus;
import vazkii.psi.api.exosuit.PsiArmorEvent;
import vazkii.psi.api.internal.IPlayerData;
import vazkii.psi.api.internal.PsiRenderHelper;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.common.Psi;
import vazkii.psi.common.attribute.base.ModAttributes;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.message.MessageDataSync;
import vazkii.psi.common.network.message.MessageDeductPsi;
import vazkii.psi.common.network.message.MessagePsiOverflow;

import java.lang.ref.WeakReference;
import java.util.*;

public class PlayerDataHandler {

	public static final Set<SpellContext> delayedContexts = new LinkedHashSet<>();
	private static final WeakHashMap<Player, PlayerData> remotePlayerData = new WeakHashMap<>();
	private static final WeakHashMap<Player, PlayerData> playerData = new WeakHashMap<>();
	private static final Map<UUID, CompoundTag> persistentData = new HashMap<>();
	private static final String DATA_TAG = "PsiData";
	private static boolean fabricCallbacksRegistered;

	@NotNull
	public static PlayerData get(Player player) {
		if(player == null) {
			return new PlayerData();
		}

		Map<Player, PlayerData> dataMap = player.level().isClientSide() ? remotePlayerData : playerData;

		PlayerData data = dataMap.computeIfAbsent(player, PlayerData::new);
		if(data.playerWR != null && data.playerWR.get() != player) {
			CompoundTag cmp = new CompoundTag();
			data.writeToNBT(cmp);
			dataMap.remove(player);
			data = get(player);
			data.readFromNBT(cmp);
		}

		return data;
	}

	public static CompoundTag getDataCompoundForPlayer(Player player) {
		CompoundTag playerPersistentData = persistentData.computeIfAbsent(player.getUUID(), ignored -> new CompoundTag());
		if(!playerPersistentData.contains(DATA_TAG)) {
			playerPersistentData.put(DATA_TAG, new CompoundTag());
		}

		return playerPersistentData.getCompoundOrEmpty(DATA_TAG);
	}

	public static void registerFabricCallbacks() {
		if(fabricCallbacksRegistered) {
			return;
		}

		fabricCallbacksRegistered = true;
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			tickDelayedContexts();
			for(ServerPlayer player : server.getPlayerList().getPlayers()) {
				tickPlayer(player);
			}
		});
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> syncPlayerData(handler.player));
		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
			if(entity instanceof Player player) {
				handleEntityDamage(player, source, damageTaken);
			}
		});
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if(world.isClientSide()) {
				return InteractionResult.PASS;
			}

			return handleArmorStandInteract(player, hand, entity);
		});
		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> clearDimensionData(player));
	}

	private static void tickDelayedContexts() {
		List<SpellContext> delayedContextsCopy = new ArrayList<>(delayedContexts);
		for(SpellContext context : delayedContextsCopy) {
			context.delay--;

			if(context.delay <= 0) {
				delayedContexts.remove(context);
				context.delay = 0; // Just in case it goes under 0
				context.cspell.safeExecute(context);
			}
		}
	}

	public static void tickPlayer(Player player) {
		if(player.isSpectator()) {
			return;
		}

		ItemStack cadStack = PsiAPI.getPlayerCAD(player);
		if(!cadStack.isEmpty() && cadStack.getItem() instanceof ICAD && PsiAPI.canCADBeUpdated(player)) {
			((ICAD) cadStack.getItem()).incrementTime(cadStack);
		}

		PsiArmorEvent.post(new PsiArmorEvent(player, PsiArmorEvent.TICK));
		PlayerDataHandler.get(player).tick();
	}

	private static void syncPlayerData(ServerPlayer player) {
		MessageDataSync message = new MessageDataSync(get(player));
		MessageRegister.sendToPlayer(player, message);
	}

	private static void handleEntityDamage(Player player, DamageSource source, float damage) {
		PlayerDataHandler.get(player).damage(damage);

		LivingEntity attacker = null;
		if(source.getEntity() instanceof LivingEntity living) {
			attacker = living;
		}

		PsiArmorEvent.post(new PsiArmorEvent(player, PsiArmorEvent.DAMAGE, damage, attacker));
		if(source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE)) {
			PsiArmorEvent.post(new PsiArmorEvent(player, PsiArmorEvent.ON_FIRE));
		}
	}

	private static InteractionResult handleArmorStandInteract(Player player, InteractionHand hand, Entity target) {
		if(!player.isSecondaryUseActive()) {
			return InteractionResult.PASS;
		}

		if(!(target instanceof ArmorStand)) {
			return InteractionResult.PASS;
		}

		ItemStack itemStackIn = player.getItemInHand(hand);
		ItemStack playerCad = PsiAPI.getPlayerCAD(player);

		if(playerCad != itemStackIn) {
			return InteractionResult.PASS;
		}

		return InteractionResult.FAIL;
	}

	private static void clearDimensionData(Player player) {
		get(player).eidosChangelog.clear();
	}

	@Environment(EnvType.CLIENT)
	public static void renderFabricWorld(WorldRenderContext context) {
		// Legacy immediate world rendering awaits a 1.21.11 render-state implementation.
	}

	public static class PlayerData implements IPlayerData {

		private static final String TAG_AVAILABLE_PSI = "availablePsi";
		private static final String TAG_REGEN_CD = "regenCd";
		private static final String TAG_OVERFLOWED = "overflowed";

		private static final String TAG_EIDOS_ANCHOR_X = "eidosAnchorX";
		private static final String TAG_EIDOS_ANCHOR_Y = "eidosAnchorY";
		private static final String TAG_EIDOS_ANCHOR_Z = "eidosAnchorZ";
		private static final String TAG_EIDOS_ANCHOR_PITCH = "eidosAnchorPitch";
		private static final String TAG_EIDOS_ANCHOR_YAW = "eidosAnchorYaw";
		private static final String TAG_EIDOS_ANCHOR_TIME = "eidosAnchorTime";

		private static final String TAG_CUSTOM_DATA = "customData";
		// Eidos stuff
		public final Stack<Vector3> eidosChangelog = new Stack<>();
		public final List<Deduction> deductions = new ArrayList<>();
		public final WeakReference<Player> playerWR;
		private final boolean client;
		public int availablePsi;
		public int lastAvailablePsi;
		public int regenCooldown;
		public boolean loopcasting = false;
		public InteractionHand loopcastHand = null;
		public ItemStack lastTickLoopcastStack;
		public int loopcastTime = 1;
		public int loopcastAmount = 0;
		public int loopcastFadeTime = 0;
		public boolean overflowed = false;
		public Vector3 eidosAnchor = new Vector3(0, 0, 0);
		public double eidosAnchorPitch, eidosAnchorYaw;
		public boolean isAnchored;
		public boolean isReverting;
		public int eidosAnchorTime;
		public int postAnchorRecallTime;
		public int eidosReversionTime;
		public DimensionType lastDimension;
		public boolean deductTick;
		// Exosuit Event Stuff
		private boolean lowLight, underwater, lowHp;
		// Custom Data
		private CompoundTag customData;

		private PlayerData() {
			playerWR = new WeakReference<>(null);
			client = true;
		}

		public PlayerData(Player player) {
			playerWR = new WeakReference<>(player);
			client = player.level().isClientSide();

			load();
		}

		public void tick() {
			Player player = playerWR.get();
			if(player == null) {
				return;
			}

			DimensionType dimension = player.level().dimensionType();

			if(deductTick) {
				deductTick = false;
			} else {
				lastAvailablePsi = availablePsi;
			}

			int max = getTotalPsi();
			if(availablePsi > max) {
				availablePsi = max;
			}

			ItemStack cadStack = getCAD();

			if(!cadStack.isEmpty()) {
				ICAD cad = (ICAD) cadStack.getItem();
				int overflow = cad.getStatValue(cadStack, EnumCADStat.OVERFLOW);
				if(overflow == -1) {
					availablePsi = max;
				} else {
					applyRegen(player, max, cadStack);
				}
			} else {
				applyRegen(player, max, cadStack);
			}

			int color = ICADColorizer.DEFAULT_SPELL_COLOR;

			if(!cadStack.isEmpty()) {
				color = Psi.proxy.getColorForCAD(cadStack);
			}

			float r = PsiRenderHelper.r(color) / 255F;
			float g = PsiRenderHelper.g(color) / 255F;
			float b = PsiRenderHelper.b(color) / 255F;

			loopcast: {
				if(player.isSpectator()) {
					stopLoopcast();
				}

				if(overflowed) {
					stopLoopcast();
				}

				if(loopcasting && loopcastHand != null) {
					ItemStack stackInHand = player.getItemInHand(loopcastHand);

					if(stackInHand.isEmpty() ||
							!ISocketable.isSocketable(stackInHand) ||
							!ISocketable.socketable(stackInHand).canLoopcast()) {
						stopLoopcast();
						break loopcast;
					}

					if(lastTickLoopcastStack != null) {
						if(!ItemStack.isSameItem(lastTickLoopcastStack, stackInHand) ||
								!ISocketable.isSocketable(lastTickLoopcastStack)) {
							stopLoopcast();
							break loopcast;
						} else {
							ISocketable lastTickItem = ISocketable.socketable(lastTickLoopcastStack);
							ISocketable thisTickItem = ISocketable.socketable(stackInHand);

							int lastSlot = lastTickItem.getSelectedSlot();
							int thisSlot = thisTickItem.getSelectedSlot();
							if(lastSlot != thisSlot) {
								stopLoopcast();
								break loopcast;
							}

							ItemStack lastTick = lastTickItem.getBulletInSocket(lastSlot);
							ItemStack thisTick = thisTickItem.getBulletInSocket(thisSlot);
							if(!ItemStack.matches(lastTick, thisTick)) {
								stopLoopcast();
								break loopcast;
							}
						}
					}

					lastTickLoopcastStack = stackInHand.copy();

					ISocketable socketable = ISocketable.socketable(stackInHand);

					for(int i = 0; i < 5; i++) {
						double x = player.getX() + (Math.random() - 0.5) * 2.1 * player.getBbWidth();
						double y = player.getY() + 0.35D;
						double z = player.getZ() + (Math.random() - 0.5) * 2.1 * player.getBbWidth();
						float grav = -0.15F - (float) Math.random() * 0.03F;
						Psi.proxy.sparkleFX(x, y, z, r, g, b, grav, 0.25F, 15);
					}

					if(loopcastTime > 0 && loopcastTime % 5 == 0) {
						ItemStack bullet = socketable.getSelectedBullet();
						if(bullet.isEmpty() || !ISpellAcceptor.hasSpell(bullet)) {
							stopLoopcast();
							break loopcast;
						}

						ISpellAcceptor spellContainer = ISpellAcceptor.acceptor(bullet);
						Spell spell = spellContainer.getSpell();
						SpellContext context = new SpellContext().setPlayer(player).setSpell(spell).setLoopcastIndex(loopcastAmount + 1);
						context.castFrom = loopcastHand;
						if(context.isValid()) {
							if(context.cspell.metadata.evaluateAgainst(cadStack)) {
								int cost = ItemCAD.getRealCost(cadStack, bullet, context.cspell.metadata.getStat(EnumSpellStat.COST));
								if(cost > 0 || cost == -1) {
									if(cost != -1) {
										deductPsi(cost, 0, true);
									}

									if(!player.level().isClientSide() && loopcastTime % 10 == 0) {
										player.level().playSound(null, player.getX(), player.getY(), player.getZ(), PsiSoundHandler.loopcast, SoundSource.PLAYERS, 0.1F, (float) (0.15 + Math.random() * 0.85));
									}
								}

								if(!player.level().isClientSide()) {
									if(!spellContainer.loopcastSpell(context)) {
										stopLoopcast();
										break loopcast;
									}
								}
								loopcastAmount++;
							}
						}
					}

					loopcastTime++;
				} else if(loopcastFadeTime > 0) {
					loopcastFadeTime--;
				}
			}

			if(!player.isAlive() || dimension != lastDimension) {
				eidosAnchorTime = 0;
				eidosReversionTime = 0;
				eidosChangelog.clear();
				isAnchored = false;
				isReverting = false;
			}

			if(eidosAnchorTime > 0) {
				if(eidosAnchorTime == 1) {
					if(player instanceof ServerPlayer pmp) {
						pmp.connection.teleport(eidosAnchor.x, eidosAnchor.y, eidosAnchor.z, (float) eidosAnchorYaw, (float) eidosAnchorPitch);

						Entity riding = player.getVehicle();
						while(riding != null) {
							riding.setPos(eidosAnchor.x, eidosAnchor.y, eidosAnchor.z);
							riding = riding.getVehicle();
						}
					}
					postAnchorRecallTime = 0;
				}
				eidosAnchorTime--;
			} else if(postAnchorRecallTime < 5) {
				postAnchorRecallTime--;
				isAnchored = false;
			}

			if(eidosReversionTime > 0) {
				if(eidosChangelog.isEmpty()) {
					eidosReversionTime = 0;
					isReverting = false;
				} else {
					eidosChangelog.pop();
					if(eidosChangelog.isEmpty()) {
						eidosReversionTime = 0;
						isReverting = false;
					} else {
						Vector3 vec = eidosChangelog.pop();
						if(player instanceof ServerPlayer pmp) {
							pmp.connection.teleport(new PositionMoveRotation(new Vec3(vec.x, vec.y, vec.z), pmp.getDeltaMovement(), 0, 0), Relative.ROTATION);
							pmp.connection.resetPosition();
						} else {
							player.setPos(vec.x, vec.y, vec.z);
						}

						Entity riding = player.getVehicle();
						while(riding != null) {
							riding.setPos(vec.x, vec.y, vec.z);

							riding = riding.getVehicle();
						}

						if(player.level().isClientSide()) {
							for(int i = 0; i < 5; i++) {
								double spread = 0.6;

								double x = player.getX() + (Math.random() - 0.5) * spread;
								double y = player.getY() + (Math.random() - 0.5) * spread;
								double z = player.getZ() + (Math.random() - 0.5) * spread;

								Psi.proxy.sparkleFX(x, y, z, r, g, b, 0, 0, 0, 1.2F, 12);
							}
						}

						player.setDeltaMovement(0, 0, 0);
						player.fallDistance = 0F;
					}
				}

				eidosReversionTime--;
				if(eidosReversionTime == 0 || player.isShiftKeyDown()) {
					eidosChangelog.clear();
					isReverting = false;
				}
			} else {
				if(eidosChangelog.size() > 600) {
					eidosChangelog.removeFirst();
				}
				eidosChangelog.push(Vector3.fromEntity(player));
			}

			BlockPos pos = player.blockPosition();
			int light = player.level().getLightEngine().getRawBrightness(pos, 0);

			boolean lowLight = light == 0;
			if(!this.lowLight && lowLight) {
				PsiArmorEvent.post(new PsiArmorEvent(player, PsiArmorEvent.LOW_LIGHT));
			}
			this.lowLight = lowLight;

			boolean underwater = player.isInWater();
			if(!this.underwater && underwater) {
				PsiArmorEvent.post(new PsiArmorEvent(player, PsiArmorEvent.UNDERWATER));
			}
			this.underwater = underwater;

			boolean lowHp = player.getHealth() <= 6;
			if(!this.lowHp && lowHp) {
				PsiArmorEvent.post(new PsiArmorEvent(player, PsiArmorEvent.LOW_HP));
			}
			this.lowHp = lowHp;

			List<Deduction> remove = new ArrayList<>();
			for(Deduction d : deductions) {
				if(d.invalid) {
					remove.add(d);
				} else {
					d.tick();
				}
			}
			deductions.removeAll(remove);

			lastDimension = dimension;
		}

		private void applyRegen(Player player, int max, ItemStack cadStack) {
			RegenPsiEvent event = new RegenPsiEvent(player, this, cadStack);

			if(!PsiEventBus.post(event).isCanceled()) {
				if(!cadStack.isEmpty()) {
					ICAD cad = (ICAD) cadStack.getItem();
					cad.regenPsi(cadStack, event.getCadRegen());
				}

				boolean anyChange = availablePsi != max && event.getPlayerRegen() > 0;

				int prevPsi = availablePsi;
				availablePsi = Math.min(max, availablePsi + event.getPlayerRegen());

				if(overflowed && event.willHealOverflow()) {
					anyChange = true;
					overflowed = false;
				}

				if(regenCooldown != event.getRegenCooldown()) {
					anyChange = true;
				}
				regenCooldown = event.getRegenCooldown();

				if(anyChange) {
					if(player instanceof ServerPlayer) {
						MessageDeductPsi message = new MessageDeductPsi(prevPsi, availablePsi, regenCooldown, false);
						MessageRegister.sendToPlayer((ServerPlayer) player, message);
					}

					save();
				}
			}
		}

		public void stopLoopcast() {
			Player player = playerWR.get();

			if(loopcasting) {
				loopcastFadeTime = 5;
				PsiEventBus.post(new LoopcastEndEvent(player, this, loopcastHand, loopcastAmount));
			}
			loopcasting = false;

			lastTickLoopcastStack = null;
			loopcastHand = null;

			loopcastTime = 1;
			loopcastAmount = 0;

			if(player instanceof ServerPlayer) {
				LoopcastTrackingHandler.syncForTrackersAndSelf((ServerPlayer) player);
			}
		}

		public int calculateDamageDeduction(float amount) {
			return (int) (getTotalPsi() * 0.02 * amount);
		}

		public void damage(float amount) {
			int psi = calculateDamageDeduction(amount);
			if(psi > 0 && availablePsi > 0) {
				psi = Math.min(psi, availablePsi);
				deductPsi(psi, 20, true, true);
			}
		}

		public ItemStack getCAD() {
			return PsiAPI.getPlayerCAD(playerWR.get());
		}

		public void deductPsi(int psi, int cd, boolean sync) {
			deductPsi(psi, cd, sync, false);
		}

		@Override
		public void deductPsi(int psi, int cd, boolean sync, boolean shatter) {
			int currentPsi = availablePsi;

			Player player = playerWR.get();
			if(player == null) {
				return;
			}

			ItemStack cadStack = getCAD();

			if(!cadStack.isEmpty()) {
				ICAD cad = (ICAD) cadStack.getItem();
				int storedPsi = cad.getStoredPsi(cadStack);
				if(storedPsi == -1) {
					return;
				}
			}

			availablePsi -= psi;
			if(regenCooldown < cd) {
				regenCooldown = cd;
			}

			if(availablePsi < 0) {
				int overflow = -availablePsi;
				availablePsi = 0;

				if(!cadStack.isEmpty()) {
					ICAD cad = (ICAD) cadStack.getItem();
					overflow = cad.consumePsi(cadStack, overflow);
				}

				if(!shatter && overflow > 0) {
					float dmg = (float) overflow / (loopcasting ? 50 : 125);
					if(!client) {
						DamageSource overloadSource = player.damageSources().magic();
						player.hurt(overloadSource, dmg);
					}
					overflowed = true;
					if(sync && player instanceof ServerPlayer) {
						MessagePsiOverflow message = new MessagePsiOverflow(true);
						MessageRegister.sendToPlayer((ServerPlayer) player, message);
					}
				}
			}

			if(sync && player instanceof ServerPlayer) {
				MessageDeductPsi message = new MessageDeductPsi(currentPsi, availablePsi, regenCooldown, shatter);
				MessageRegister.sendToPlayer((ServerPlayer) player, message);
			}

			save();
		}

		public void addDeduction(int current, int deduct, boolean shatter) {
			if(deduct > current) {
				deduct = current;
			}
			if(deduct < 0) {
				deduct = 0;
			}

			if(deduct == 0) {
				return;
			}

			deductions.add(new Deduction(current, deduct, 20, shatter));
		}

		@Override
		public int getAvailablePsi() {
			return availablePsi;
		}

		@Override
		public int getLastAvailablePsi() {
			return lastAvailablePsi;
		}

		@Override
		public int getTotalPsi() {
			Player player = playerWR.get();
			if(player != null) {
				return (int) player.getAttributeValue(ModAttributes.TOTAL_PSI);
			}
			return (int) ModAttributes.TOTAL_PSI.value().getDefaultValue();
		}

		@Override
		public int getRegenPerTick() {
			Player player = playerWR.get();
			if(player != null) {
				return (int) player.getAttributeValue(ModAttributes.REGEN);
			}
			return (int) ModAttributes.REGEN.value().getDefaultValue();
		}

		@Override
		public boolean isOverflowed() {
			return overflowed;
		}

		@Override
		public int getRegenCooldown() {
			return regenCooldown;
		}

		public boolean hasAdvancement(Identifier group) {
			Player player = playerWR.get();
			return Psi.proxy.hasAdvancement(group, player);
		}

		@Override
		public boolean isPieceGroupUnlocked(Identifier group, @Nullable Identifier name) {
			Player player = playerWR.get();
			if(player == null) {
				return false;
			}

			if(player.isCreative()) {
				return true;
			}

			boolean hasAdvancement = hasAdvancement(group);
			PieceKnowledgeEvent event = new PieceKnowledgeEvent(group, name, player, this, hasAdvancement);
			PsiEventBus.post(event);

			return !event.isCanceled();
		}

		@Override
		public void unlockPieceGroup(Identifier resourceLocation) {
			Player player = playerWR.get();
			if(player instanceof ServerPlayer serverPlayer) {
				AdvancementHolder advancement = ((net.minecraft.server.level.ServerLevel) serverPlayer.level()).getServer().getAdvancements().get(resourceLocation);
				if(advancement != null && !serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone()) {
					for(String s : serverPlayer.getAdvancements().getOrStartProgress(advancement).getRemainingCriteria()) {
						serverPlayer.getAdvancements().getOrStartProgress(advancement).grantProgress(s);
					}
				}
			}
		}

		@Override
		public void markPieceExecuted(SpellPiece piece) {
			Player player = playerWR.get();
			if(player == null) {
				return;
			}

			PieceExecutedEvent event = new PieceExecutedEvent(piece, player);
			PsiEventBus.post(event);
			Optional<Map.Entry<ResourceKey<Collection<Class<? extends SpellPiece>>>, Collection<Class<? extends SpellPiece>>>> advancementEntry = PsiAPI.ADVANCEMENT_GROUP_REGISTRY.entrySet().stream().filter((entry) -> entry.getValue().contains(piece.getClass())).findFirst();
			if(advancementEntry.isEmpty()) {
				return;
			}

			Identifier advancement = advancementEntry.get().getKey().identifier();
			Object advancementMainPieceClass = advancementEntry.get().getValue().toArray()[0];
			if(advancementMainPieceClass == piece.getClass() && !hasAdvancement(advancement)) {
				PsiEventBus.post(new PieceGroupAdvancementComplete(piece, player, advancement));
			}
		}

		@Override
		public CompoundTag getCustomData() {
			if(customData == null) {
				return customData = new CompoundTag();
			}
			return customData;
		}

		@Override
		public void save() {
			if(!client) {
				Player player = playerWR.get();

				if(player != null) {
					CompoundTag cmp = getDataCompoundForPlayer(player);
					writeToNBT(cmp);
				}
			}
		}

		public void writeToNBT(CompoundTag cmp) {
			cmp.putInt(TAG_AVAILABLE_PSI, availablePsi);
			cmp.putInt(TAG_REGEN_CD, regenCooldown);
			cmp.putBoolean(TAG_OVERFLOWED, overflowed);

			cmp.putDouble(TAG_EIDOS_ANCHOR_X, eidosAnchor.x);
			cmp.putDouble(TAG_EIDOS_ANCHOR_Y, eidosAnchor.y);
			cmp.putDouble(TAG_EIDOS_ANCHOR_Z, eidosAnchor.z);
			cmp.putDouble(TAG_EIDOS_ANCHOR_PITCH, eidosAnchorPitch);
			cmp.putDouble(TAG_EIDOS_ANCHOR_YAW, eidosAnchorYaw);
			cmp.putInt(TAG_EIDOS_ANCHOR_TIME, eidosAnchorTime);

			if(customData != null) {
				cmp.put(TAG_CUSTOM_DATA, customData);
			}
		}

		public void load() {
			if(!client) {
				Player player = playerWR.get();

				if(player != null) {
					CompoundTag cmp = getDataCompoundForPlayer(player);
					readFromNBT(cmp);
				}
			}
		}

		public void readFromNBT(CompoundTag cmp) {
			availablePsi = cmp.getIntOr(TAG_AVAILABLE_PSI, 0);
			regenCooldown = cmp.getIntOr(TAG_REGEN_CD, 0);
			overflowed = cmp.getBooleanOr(TAG_OVERFLOWED, false);

			double x = cmp.getDoubleOr(TAG_EIDOS_ANCHOR_X, 0.0);
			double y = cmp.getDoubleOr(TAG_EIDOS_ANCHOR_Y, 0.0);
			double z = cmp.getDoubleOr(TAG_EIDOS_ANCHOR_Z, 0.0);
			eidosAnchor.set(x, y, z);
			eidosAnchorPitch = cmp.getDoubleOr(TAG_EIDOS_ANCHOR_PITCH, 0.0);
			eidosAnchorYaw = cmp.getDoubleOr(TAG_EIDOS_ANCHOR_YAW, 0.0);
			eidosAnchorTime = cmp.getIntOr(TAG_EIDOS_ANCHOR_TIME, 0);

			customData = cmp.getCompoundOrEmpty(TAG_CUSTOM_DATA);
		}

		@Environment(EnvType.CLIENT)
		public void render(Player player, float partTicks, PoseStack ms) {
			// Legacy immediate spell-circle preview renderer awaits a submit-node renderer rewrite.
		}

		public static class Deduction {

			public final int current;
			public final int deduct;
			public final int cd;
			public final boolean shatter;

			public int elapsed;

			public boolean invalid;

			public Deduction(int current, int deduct, int cd, boolean shatter) {
				this.current = current;
				this.deduct = deduct;
				this.cd = cd;
				this.shatter = shatter;
			}

			public void tick() {
				elapsed++;

				if(elapsed >= cd) {
					invalid = true;
				}
			}

			public float getPercentile(float partTicks) {
				return 1F - Math.min(1F, (elapsed + partTicks) / cd);
			}
		}

	}
}
