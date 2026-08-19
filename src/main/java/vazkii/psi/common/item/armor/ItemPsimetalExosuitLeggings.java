/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.item.armor;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;

import vazkii.psi.api.exosuit.PsiArmorEvent;

public class ItemPsimetalExosuitLeggings extends ItemPsimetalArmor {

	public ItemPsimetalExosuitLeggings(ArmorType type, Item.Properties properties) {
		super(type, properties);
	}

	@Override
	public String getEvent(ItemStack stack) {
		return PsiArmorEvent.TICK;
	}

	@Override
	public int getCastCooldown(ItemStack stack) {
		return 0;
	}

	@Override
	public float getCastVolume() {
		return 0F;
	}

}
