/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.api.internal.PsiRenderHelper;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.client.fx.SparkleParticleData;
import vazkii.psi.common.Psi;
import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.message.MessageParticleTrail;

import java.util.Optional;
import java.util.function.Consumer;

public class EntitySpellProjectile extends ThrowableProjectile {
	protected static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> ATTACKTARGET_UUID = SynchedEntityData.defineId(EntitySpellProjectile.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
	private static final String TAG_COLORIZER = "colorizer";
	private static final String TAG_BULLET = "bullet";
	private static final String TAG_TIME_ALIVE = "timeAlive";

	private static final String TAG_LAST_MOTION_X = "lastMotionX";
	private static final String TAG_LAST_MOTION_Y = "lastMotionY";
	private static final String TAG_LAST_MOTION_Z = "lastMotionZ";

	private static final EntityDataAccessor<ItemStack> COLORIZER_DATA = SynchedEntityData.defineId(EntitySpellProjectile.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<ItemStack> BULLET_DATA = SynchedEntityData.defineId(EntitySpellProjectile.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> CASTER_UUID = SynchedEntityData.defineId(EntitySpellProjectile.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
	private static final float DEFAULT_LAUNCH_VELOCITY = 1.25F;
	public SpellContext context;
	public int timeAlive;

	public EntitySpellProjectile(EntityType<? extends ThrowableProjectile> type, Level worldIn) {
		super(type, worldIn);
	}

	protected EntitySpellProjectile(EntityType<? extends ThrowableProjectile> type, Level world, LivingEntity thrower) {
		this(type, world, thrower, DEFAULT_LAUNCH_VELOCITY);
	}

	protected EntitySpellProjectile(EntityType<? extends ThrowableProjectile> type, Level world, LivingEntity thrower, float launchVelocity) {
		super(type, world);
		setOwner(thrower);
		setPos(thrower.getX(), thrower.getEyeY() - 0.1, thrower.getZ());
		shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0, launchVelocity, 0F);
	}

	public EntitySpellProjectile(Level world, LivingEntity thrower) {
		this(ModEntities.spellProjectile, world, thrower);
	}

	public EntitySpellProjectile setInfo(Player player, ItemStack colorizer, ItemStack bullet) {
		entityData.set(COLORIZER_DATA, colorizer);
		entityData.set(BULLET_DATA, bullet.copy());
		entityData.set(CASTER_UUID, Optional.of(EntityReference.of(player)));
		entityData.set(ATTACKTARGET_UUID, Optional.empty());
		return this;
	}

	public void spawnLaunchParticles() {
		Entity owner = getOwner();
		double x = owner == null ? getX() : owner.getX();
		double y = owner == null ? getY() : owner.getEyeY() - 0.1D;
		double z = owner == null ? getZ() : owner.getZ();
		sendSparkleBurst(x, y, z, 18, 0.45F, 5, 0.08D, 0.14D);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
		pBuilder.define(COLORIZER_DATA, ItemStack.EMPTY);
		pBuilder.define(BULLET_DATA, ItemStack.EMPTY);
		pBuilder.define(CASTER_UUID, Optional.empty());
		pBuilder.define(ATTACKTARGET_UUID, Optional.empty());
	}

	@Override
	protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.store(TAG_COLORIZER, ItemStack.CODEC, entityData.get(COLORIZER_DATA));
		output.store(TAG_BULLET, ItemStack.CODEC, entityData.get(BULLET_DATA));
		output.putInt(TAG_TIME_ALIVE, timeAlive);
		output.putDouble(TAG_LAST_MOTION_X, getDeltaMovement().x());
		output.putDouble(TAG_LAST_MOTION_Y, getDeltaMovement().y());
		output.putDouble(TAG_LAST_MOTION_Z, getDeltaMovement().z());
	}

	@Override
	protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
		super.readAdditionalSaveData(input);
		entityData.set(COLORIZER_DATA, input.read(TAG_COLORIZER, ItemStack.CODEC).orElse(ItemStack.EMPTY));
		entityData.set(BULLET_DATA, input.read(TAG_BULLET, ItemStack.CODEC).orElse(ItemStack.EMPTY));

		Entity thrower = getOwner();
		if(thrower instanceof Player) {
			entityData.set(CASTER_UUID, Optional.of(EntityReference.of((LivingEntity) thrower)));
		}

		timeAlive = input.getIntOr(TAG_TIME_ALIVE, 0);

		double lastMotionX = input.getDoubleOr(TAG_LAST_MOTION_X, 0.0);
		double lastMotionY = input.getDoubleOr(TAG_LAST_MOTION_Y, 0.0);
		double lastMotionZ = input.getDoubleOr(TAG_LAST_MOTION_Z, 0.0);
		setDeltaMovement(lastMotionX, lastMotionY, lastMotionZ);
	}

	@Override
	public void tick() {
		super.tick();

		int timeAlive = tickCount;
		if(timeAlive > getLiveTime()) {
			remove(RemovalReason.DISCARDED);
		}

		sendProjectileTrail();

		ItemStack colorizer = entityData.get(COLORIZER_DATA);
		int colorVal = Psi.proxy.getColorForColorizer(colorizer);

		float r = PsiRenderHelper.r(colorVal) / 255F;
		float g = PsiRenderHelper.g(colorVal) / 255F;
		float b = PsiRenderHelper.b(colorVal) / 255F;

		double x = getX();
		double y = getY();
		double z = getZ();

		Vector3 lookOrig = new Vector3(getDeltaMovement()).normalize();
		for(int i = 0; i < getParticleCount(); i++) {
			Vector3 look = lookOrig.copy();
			double spread = 0.6;
			double dist = 0.15;
			if(this instanceof EntitySpellGrenade) {
				look.y += 1;
				dist = 0.05;
			}

			look.x += (Math.random() - 0.5) * spread;
			look.y += (Math.random() - 0.5) * spread;
			look.z += (Math.random() - 0.5) * spread;

			look.normalize().multiply(dist);

			if(level().isClientSide()) {
				Psi.proxy.sparkleFX(x, y, z, r, g, b, (float) look.x, (float) look.y, (float) look.z, 1.2F, 12);
			}

		}
	}

	public int getLiveTime() {
		return 600;
	}

	public int getParticleCount() {
		return 5;
	}

	@Override
	protected void onHit(@NotNull HitResult pos) {
		if(pos instanceof BlockHitResult blockHit) {
			var targetPos = blockHit.getBlockPos().relative(blockHit.getDirection());
			teleportTo(targetPos.getX(), targetPos.getY(), targetPos.getZ());
		} else if(pos instanceof EntityHitResult entityHit) {
			teleportTo(entityHit.getLocation().x, entityHit.getLocation().y, entityHit.getLocation().z);
		}

		if(pos instanceof EntityHitResult && ((EntityHitResult) pos).getEntity() instanceof LivingEntity) {
			cast((SpellContext context) -> {
				if(context != null) {
					context.attackedEntity = (LivingEntity) ((EntityHitResult) pos).getEntity();
				}
			});
		} else {
			cast();
		}
	}

	private void spawnImpactParticles() {
		sendSparkleBurst(getX(), getY() + 0.1D, getZ(), 28, 0.55F, 6, 0.12D, 0.18D);
	}

	private void sendProjectileTrail() {
		if(!(level() instanceof ServerLevel serverLevel) || tickCount <= 1) {
			return;
		}

		Entity owner = getOwner();
		if(!(owner instanceof Player player)) {
			return;
		}

		Vec3 motion = getDeltaMovement();
		double length = motion.length();
		if(length <= 0.01D) {
			return;
		}

		Vec3 current = position();
		Vec3 previous = current.subtract(motion);
		MessageRegister.sendToPlayersInDimension(serverLevel, new MessageParticleTrail(previous, motion, length, 12, PsiAPI.getPlayerCAD(player)));
	}

	public void cast() {
		cast(null);
	}

	public void cast(Consumer<SpellContext> callback) {
		Entity thrower = getOwner();
		boolean canCast = false;
		ItemStack spellContainer = entityData.get(BULLET_DATA);
		Spell spell = null;

		if(thrower instanceof Player) {
			if(!spellContainer.isEmpty() && ISpellAcceptor.isContainer(spellContainer)) {
				spell = ISpellAcceptor.acceptor(spellContainer).getSpell();
				if(spell != null) {
					canCast = true;
					if(context == null) {
						context = new SpellContext().setPlayer((Player) thrower).setFocalPoint(this).setSpell(spell);
					}
					context.setFocalPoint(this);
				}
			}
		}

		if(callback != null) {
			callback.accept(context);
		}

		if(canCast && context != null) {
			spawnImpactParticles();
			context.cspell.safeExecute(context);
		}
		remove(RemovalReason.DISCARDED);
	}

	@Override
	public Entity getOwner() {
		Entity superThrower = super.getOwner();
		if(superThrower != null) {
			return superThrower;
		}

		return entityData.get(CASTER_UUID)
				.map(reference -> EntityReference.getLivingEntity(reference, level()))
				.orElse(null);
	}

	public LivingEntity getAttackTarget() {
		double radiusVal = SpellContext.MAX_DISTANCE;
		Vector3 positionVal = Vector3.fromVec3d(this.position());
		AABB axis = new AABB(positionVal.x - radiusVal, positionVal.y - radiusVal, positionVal.z - radiusVal, positionVal.x + radiusVal, positionVal.y + radiusVal, positionVal.z + radiusVal);
		return entityData.get(ATTACKTARGET_UUID)
				.map(reference -> EntityReference.getLivingEntity(reference, level()))
				.orElse(null);
	}

	protected void setAttackTarget(Entity target) {
		entityData.set(ATTACKTARGET_UUID, target instanceof LivingEntity living ? Optional.of(EntityReference.of(living)) : Optional.empty());
	}

	@Override
	protected double getDefaultGravity() {
		return 0D;
	}

	private void sendSparkleBurst(double x, double y, double z, int count, float size, int lifetime, double spread, double speed) {
		if(!(level() instanceof ServerLevel serverLevel)) {
			return;
		}

		int colorVal = getColorForColorizer(entityData.get(COLORIZER_DATA));
		float r = PsiRenderHelper.r(colorVal) / 255F;
		float g = PsiRenderHelper.g(colorVal) / 255F;
		float b = PsiRenderHelper.b(colorVal) / 255F;
		serverLevel.sendParticles(new SparkleParticleData(size, r, g, b, lifetime, 0, 0, 0), x, y, z, count, spread, spread, spread, speed);
	}

	private static int getColorForColorizer(ItemStack colorizer) {
		if(!colorizer.isEmpty() && colorizer.getItem() instanceof ICADColorizer colorizerItem) {
			return colorizerItem.getColor(colorizer);
		}
		return ICADColorizer.DEFAULT_SPELL_COLOR;
	}

	@Override
	public boolean isIgnoringBlockTriggers() {
		return true;
	}
}
