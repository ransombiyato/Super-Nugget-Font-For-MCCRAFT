/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.client.model;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import vazkii.psi.common.item.armor.ItemPsimetalArmor;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class ArmorModels {
	private static Map<EquipmentSlot, ModelArmor> exosuit = Collections.emptyMap();

	private static Map<EquipmentSlot, ModelArmor> make(EntityModelSet modelSet) {
		Map<EquipmentSlot, ModelArmor> ret = new EnumMap<>(EquipmentSlot.class);
		for(var slot : EquipmentSlot.values()) {
			var mesh = modelSet.bakeLayer(slot == EquipmentSlot.LEGS ? ModModelLayers.PSIMETAL_EXOSUIT_INNER_ARMOR : ModModelLayers.PSIMETAL_EXOSUIT_OUTER_ARMOR);
			ret.put(slot, new ModelArmor(mesh));
		}
		return ret;
	}

	public static void init(EntityModelSet modelSet) {
		exosuit = make(modelSet);
	}

	public static void init(EntityRendererProvider.Context ctx) {
		init(ctx.getModelSet());
	}

	@Nullable
	public static ModelArmor get(ItemStack stack) {
		Item item = stack.getItem();
		if(item instanceof ItemPsimetalArmor armor) {
			return exosuit.get(armor.getEquipmentSlot());
		}

		return null;
	}
}
