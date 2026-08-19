package vazkii.psi.api.material;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;

import vazkii.psi.api.PsiAPI;

import java.util.EnumMap;

public final class PsimetalArmorMaterial {
	private PsimetalArmorMaterial() {}

	public static final ArmorMaterial PSIMETAL_ARMOR_MATERIAL = new ArmorMaterial(
			18,
			Util.make(new EnumMap<>(ArmorType.class), map -> {
				map.put(ArmorType.BOOTS, 2);
				map.put(ArmorType.LEGGINGS, 5);
				map.put(ArmorType.CHESTPLATE, 6);
				map.put(ArmorType.HELMET, 2);
				map.put(ArmorType.BODY, 5);
			}),
			12,
			SoundEvents.ARMOR_EQUIP_IRON,
			0.0F,
			0.0F,
			PsiAPI.PSIMETAL_REPAIR_ITEMS,
			EquipmentAssets.IRON
	);
}
