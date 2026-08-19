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

public class ItemPsimetalExosuitChestplate extends ItemPsimetalArmor {

	public ItemPsimetalExosuitChestplate(ArmorType type, Item.Properties properties) {
		super(type, properties);
	}

	@Override
	public String getEvent(ItemStack stack) {
		return PsiArmorEvent.DAMAGE;
	}

}
