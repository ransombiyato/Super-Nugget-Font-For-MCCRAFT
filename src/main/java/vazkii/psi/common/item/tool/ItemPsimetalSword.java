/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.item.tool;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.capability.ICapabilityProvider;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.core.handler.PlayerDataHandler.PlayerData;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.item.base.ModDataComponents;

import java.util.List;

public class ItemPsimetalSword extends Item implements IPsimetalTool {

	public ItemPsimetalSword(Item.Properties properties) {
		super(properties.sword(PsiAPI.PSIMETAL_TOOL_MATERIAL, 3, -2.4F).component(ModDataComponents.BULLETS.get(), ItemContainerContents.EMPTY));
	}

	@Override
	public void hurtEnemy(@NotNull ItemStack itemstack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
		super.hurtEnemy(itemstack, target, attacker);

		if(IPsimetalTool.isEnabled(itemstack) && attacker instanceof Player player) {

			PlayerData data = PlayerDataHandler.get(player);
			ItemStack playerCad = PsiAPI.getPlayerCAD(player);

			if(!playerCad.isEmpty()) {
				ItemStack bullet = ISocketable.socketable(itemstack).getSelectedBullet();
				ItemCAD.cast(player.level(), player, data, bullet, playerCad, 5, 10, 0.05F,
						(SpellContext context) -> {
							context.attackedEntity = target;
							context.tool = itemstack;
						});
			}
		}

	}

	public void setDamage(ItemStack stack, int damage) {
		if(damage > stack.getMaxDamage()) {
			damage = stack.getDamageValue();
		}
		stack.setDamageValue(damage);
	}

	@Override
	public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
		if(!IPsimetalTool.isEnabled(stack)) {
			return 1;
		}
		return super.getDestroySpeed(stack, state);
	}

	@NotNull
	public String getDescriptionId(@NotNull ItemStack stack) {
		String name = super.getDescriptionId();
		if(!IPsimetalTool.isEnabled(stack)) {
			name += ".broken";
		}
		return name;
	}

	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull Entity entityIn, int itemSlot, boolean isSelected) {
		IPsimetalTool.regen(stack, entityIn);
	}

	@Environment(EnvType.CLIENT)
	public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, @NotNull TooltipFlag advanced) {
		Component componentName = ISocketable.getSocketedItemName(stack, "psimisc.none");
		tooltip.add(Component.translatable("psimisc.spell_selected", componentName));
	}

	@Nullable
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return IPsimetalTool.super.initCapabilities(stack, nbt);
	}
}
