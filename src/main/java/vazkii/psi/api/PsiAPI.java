/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;

import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ICADData;
import vazkii.psi.api.cad.IPsiBarDisplay;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.capability.Capabilities;
import vazkii.psi.api.capability.EntityCapability;
import vazkii.psi.api.capability.ItemCapability;
import vazkii.psi.api.internal.DummyMethodHandler;
import vazkii.psi.api.internal.IInternalMethodHandler;
import vazkii.psi.api.item.ComponentItemHandler;
import vazkii.psi.api.registry.PsiRegistryBuilder;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.ISpellImmune;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.detonator.IDetonationHandler;
import vazkii.psi.api.util.SimpleTier;
import vazkii.psi.common.core.capability.CapabilityTriggerSensor;
import vazkii.psi.common.core.handler.capability.CADData;
import vazkii.psi.common.entity.EntitySpellCharge;
import vazkii.psi.common.entity.EntitySpellCircle;
import vazkii.psi.common.item.ItemFlashRing;
import vazkii.psi.common.item.ItemSpellBullet;
import vazkii.psi.common.item.armor.ItemPsimetalArmor;
import vazkii.psi.common.item.base.ModItems;
import vazkii.psi.common.item.tool.ToolSocketable;

import java.util.Collection;

public final class PsiAPI {
	public static final String MOD_ID = "psi";
	public static final EntityCapability<ISpellImmune, Void> SPELL_IMMUNE_CAPABILITY = EntityCapability.createVoid(PsiAPI.location("spell_immune"), ISpellImmune.class);
	public static final EntityCapability<IDetonationHandler, Void> DETONATION_HANDLER_CAPABILITY = EntityCapability.createVoid(PsiAPI.location("detonation_handler"), IDetonationHandler.class);
	public static final ItemCapability<IPsiBarDisplay, Void> PSI_BAR_DISPLAY_CAPABILITY = ItemCapability.createVoid(PsiAPI.location("psi_bar_display"), IPsiBarDisplay.class);
	public static final ItemCapability<ISpellAcceptor, Void> SPELL_ACCEPTOR_CAPABILITY = ItemCapability.createVoid(PsiAPI.location("spell_acceptor"), ISpellAcceptor.class);
	public static final ItemCapability<ICADData, Void> CAD_DATA_CAPABILITY = ItemCapability.createVoid(PsiAPI.location("cad_data"), ICADData.class);
	public static final ItemCapability<ISocketable, Void> SOCKETABLE_CAPABILITY = ItemCapability.createVoid(PsiAPI.location("socketable"), ISocketable.class);
	public static final ResourceKey<Registry<Class<? extends SpellPiece>>> SPELL_PIECE_REGISTRY_TYPE_KEY = ResourceKey.createRegistryKey(PsiAPI.location("spell_piece_registry_type_key"));
	public static final Registry<Class<? extends SpellPiece>> SPELL_PIECE_REGISTRY = (new PsiRegistryBuilder<>(SPELL_PIECE_REGISTRY_TYPE_KEY)).create();

	// The main piece for the group is the one that comes first.
	public static final ResourceKey<Registry<Collection<Class<? extends SpellPiece>>>> ADVANCEMENT_GROUP_REGISTRY_KEY = ResourceKey.createRegistryKey(PsiAPI.location("advancement_group_registry_key"));
	public static final Registry<Collection<Class<? extends SpellPiece>>> ADVANCEMENT_GROUP_REGISTRY = (new PsiRegistryBuilder<>(ADVANCEMENT_GROUP_REGISTRY_KEY)).create();

	public static final TagKey<Item> PSIMETAL_REPAIR_ITEMS = TagKey.create(Registries.ITEM, PsiAPI.location("psimetal_repair_items"));
	public static final ToolMaterial PSIMETAL_TOOL_MATERIAL = SimpleTier.create(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 900, 7.8F, 2F, 12, PSIMETAL_REPAIR_ITEMS);
	/**
	 * The internal method handler in use. This object allows the API to interact with the mod.
	 * By default, this is a dummy. In the mod itself, this is replaced with an implementation that
	 * can handle all of its queries.<br>
	 * <br>
	 *
	 * <b>DO NOT EVER, EVER, OVERWRITE THIS VALUE</b>
	 */
	public static IInternalMethodHandler internalHandler = new DummyMethodHandler();

	/**
	 * Gets the CAD the passed PlayerEntity is using. As a player can only have one CAD, if there's
	 * more than one, this will return null.
	 */
	public static ItemStack getPlayerCAD(Player player) {
		if(player == null) {
			return ItemStack.EMPTY;
		}

		ItemStack cad = ItemStack.EMPTY;
		for(int i = 0; i < player.getInventory().getContainerSize(); i++) {
			ItemStack stackAt = player.getInventory().getItem(i);
			if(!stackAt.isEmpty() && stackAt.getItem() instanceof ICAD) {
				if(!cad.isEmpty()) {
					return ItemStack.EMPTY; // Player can only have one CAD
				}

				cad = stackAt;
			}
		}

		return cad;
	}

	public static int getPlayerCADSlot(Player player) {
		if(player == null) {
			return -1;
		}

		int slot = -1;
		for(int i = 0; i < player.getInventory().getContainerSize(); i++) {
			ItemStack stackAt = player.getInventory().getItem(i);
			if(!stackAt.isEmpty() && stackAt.getItem() instanceof ICAD) {
				if(slot != -1) {
					return -1; // Player can only have one CAD
				}

				slot = i;
			}
		}

		return slot;
	}

	public static boolean canCADBeUpdated(Player player) {
		if(player == null) {
			return false;
		}

		if(player.containerMenu == null) {
			return true;
		}

		int cadSlot = getPlayerCADSlot(player);
		return cadSlot < 9 || cadSlot == 40;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getItemCapability(ItemStack stack, ItemCapability<T, Void> capability) {
		if(stack == null || stack.isEmpty()) {
			return null;
		}

		if(capability == Capabilities.ItemHandler.ITEM) {
			if(stack.is(ModItems.cad.get())) {
				return (T) new ComponentItemHandler(stack, vazkii.psi.common.item.base.ModDataComponents.BULLETS.get(), ISocketable.MAX_ASSEMBLER_SLOTS);
			}
			if(isPsimetalSocketable(stack)) {
				return (T) new ComponentItemHandler(stack, vazkii.psi.common.item.base.ModDataComponents.BULLETS.get(), 3);
			}
			return null;
		}

		if(stack.is(ModItems.cad.get())) {
			CADData cadData = new CADData(stack);
			return (T) cadData.getCapability(capability, null);
		}

		if(isPsimetalTool(stack)) {
			ToolSocketable socketable = new ToolSocketable(stack, 3);
			return (T) socketable.getCapability(capability, null);
		}

		if(isPsimetalArmor(stack)) {
			ItemPsimetalArmor.ArmorSocketable socketable = new ItemPsimetalArmor.ArmorSocketable(stack, 3);
			return (T) socketable.getCapability(capability, null);
		}

		if(capability == SPELL_ACCEPTOR_CAPABILITY) {
			if(stack.getItem() instanceof ItemSpellBullet) {
				return (T) new ItemSpellBullet.SpellAcceptor(stack);
			}
			if(stack.getItem() instanceof ItemFlashRing) {
				return (T) new ItemFlashRing.SpellAcceptor(stack);
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getEntityCapability(Entity entity, EntityCapability<T, Void> capability) {
		if(entity == null) {
			return null;
		}
		if(capability == SPELL_IMMUNE_CAPABILITY && entity instanceof EntitySpellCircle spellCircle) {
			return (T) spellCircle;
		}
		if(capability == DETONATION_HANDLER_CAPABILITY) {
			if(entity instanceof Player player) {
				return (T) new CapabilityTriggerSensor(player);
			}
			if(entity instanceof EntitySpellCharge charge) {
				return (T) charge;
			}
		}
		return null;
	}

	private static boolean isPsimetalTool(ItemStack stack) {
		return stack.is(ModItems.psimetalShovel.get())
				|| stack.is(ModItems.psimetalPickaxe.get())
				|| stack.is(ModItems.psimetalAxe.get())
				|| stack.is(ModItems.psimetalSword.get());
	}

	private static boolean isPsimetalArmor(ItemStack stack) {
		return stack.is(ModItems.psimetalExosuitHelmet.get())
				|| stack.is(ModItems.psimetalExosuitChestplate.get())
				|| stack.is(ModItems.psimetalExosuitLeggings.get())
				|| stack.is(ModItems.psimetalExosuitBoots.get());
	}

	private static boolean isPsimetalSocketable(ItemStack stack) {
		return isPsimetalTool(stack) || isPsimetalArmor(stack);
	}

	public static Identifier location(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
