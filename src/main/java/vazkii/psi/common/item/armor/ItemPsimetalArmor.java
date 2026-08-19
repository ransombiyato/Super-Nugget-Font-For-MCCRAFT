package vazkii.psi.common.item.armor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.capability.ICapabilityProvider;
import vazkii.psi.api.exosuit.IPsiEventArmor;
import vazkii.psi.api.exosuit.PsiArmorEvent;
import vazkii.psi.api.internal.TooltipHelper;
import vazkii.psi.api.material.PsimetalArmorMaterial;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.core.handler.PlayerDataHandler.PlayerData;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.item.base.ModDataComponents;
import vazkii.psi.common.item.tool.IPsimetalTool;
import vazkii.psi.common.item.tool.ToolSocketable;

import java.util.List;

public class ItemPsimetalArmor extends Item implements IPsimetalTool, IPsiEventArmor {
	protected final ArmorType armorType;

	public ItemPsimetalArmor(ArmorType type, Properties props) {
		super(props.humanoidArmor(PsimetalArmorMaterial.PSIMETAL_ARMOR_MATERIAL, type)
				.component(ModDataComponents.BULLETS.get(), ItemContainerContents.EMPTY));
		this.armorType = type;
	}

	public void setDamage(ItemStack stack, int damage) {
		stack.setDamageValue(Math.min(damage, stack.getMaxDamage()));
	}

	@NotNull
	public String getDescriptionId(@NotNull ItemStack stack) {
		String name = super.getDescriptionId();
		return IPsimetalTool.isEnabled(stack) ? name : name + ".broken";
	}

	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
		IPsimetalTool.regen(stack, entity);
	}

	@Nullable
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new ArmorSocketable(stack, 3);
	}

	public void cast(ItemStack stack, PsiArmorEvent event) {
		PlayerData data = PlayerDataHandler.get(event.getEntity());
		ItemStack playerCad = PsiAPI.getPlayerCAD(event.getEntity());
		if(IPsimetalTool.isEnabled(stack) && !playerCad.isEmpty()) {
			int timesCast = stack.getOrDefault(ModDataComponents.TIMES_CAST.get(), 0);
			ItemStack bullet = ISocketable.socketable(stack).getSelectedBullet();
			ItemCAD.cast(event.getEntity().level(), event.getEntity(), data, bullet, playerCad, getCastCooldown(stack), 0, getCastVolume(), context -> {
				context.tool = stack;
				context.attackingEntity = event.attacker;
				context.damageTaken = event.damage;
				context.loopcastIndex = timesCast;
			}, (int) (data.calculateDamageDeduction((float) event.damage) * 0.75));
			stack.set(ModDataComponents.TIMES_CAST.get(), timesCast + 1);
		}
	}

	@Override
	public void onEvent(ItemStack stack, PsiArmorEvent event) {
		if(event.type.equals(getEvent(stack)))
			cast(stack, event);
	}

	public String getEvent(ItemStack stack) {
		return PsiArmorEvent.NONE;
	}

	public int getCastCooldown(ItemStack stack) {
		return 5;
	}

	public float getCastVolume() {
		return 0.025F;
	}

	@Environment(EnvType.CLIENT)
	public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag advanced) {
		TooltipHelper.tooltipIfShift(tooltip, () -> {
			tooltip.add(Component.translatable("psimisc.spell_selected", ISocketable.getSocketedItemName(stack, "psimisc.none")));
			tooltip.add(Component.translatable(getEvent(stack)));
		});
	}

	public boolean isRepairable(@NotNull ItemStack stack) {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public int getColor(@NotNull ItemStack stack) {
		return ICADColorizer.DEFAULT_SPELL_COLOR;
	}

	public static class ArmorSocketable extends ToolSocketable {
		public ArmorSocketable(ItemStack tool, int slots) {
			super(tool, slots);
		}

		@Override
		public void setSelectedSlot(int slot) {
			super.setSelectedSlot(slot);
			tool.set(ModDataComponents.TIMES_CAST.get(), 0);
		}

		@Override
		public void setBulletInSocket(int slot, ItemStack bullet) {
			super.setBulletInSocket(slot, bullet);
			tool.set(ModDataComponents.TIMES_CAST.get(), 0);
		}
	}
}
