/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.exosuit;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import vazkii.psi.api.event.PsiEventBus;
import vazkii.psi.api.event.PsiPlayerEvent;

public class PsiArmorEvent extends PsiPlayerEvent {

	// DO NOT FIRE AN EVENT WITH THIS
	public static final String NONE = "psi.event.none";

	public static final String DAMAGE = "psi.event.damage";
	public static final String TICK = "psi.event.tick";
	public static final String JUMP = "psi.event.jump";

	public static final String LOW_LIGHT = "psi.event.low_light";
	public static final String UNDERWATER = "psi.event.underwater";
	public static final String ON_FIRE = "psi.event.on_fire";
	public static final String LOW_HP = "psi.event.low_hp";
	public static final String DETONATE = "psi.event.spell_detonate";

	private static boolean posting = false;

	public final String type;
	public final double damage;
	public final LivingEntity attacker;

	public PsiArmorEvent(Player player, String type) {
		this(player, type, 0, null);
	}

	public PsiArmorEvent(Player player, String type, double damage, LivingEntity attacker) {
		super(player);
		this.type = type;
		this.damage = damage;
		this.attacker = attacker;

		if(type.equals(NONE)) {
			throw new IllegalArgumentException("Can't you read?");
		}
	}

	public static void post(PsiArmorEvent event) {
		if(!posting) {
			posting = true;
			try {
				PsiEventBus.post(event);
				dispatchArmorEvent(event);
			} finally {
				posting = false;
			}
		}
	}

	private static void dispatchArmorEvent(PsiArmorEvent event) {
		if(event.getEntity().isSpectator()) {
			return;
		}

		for(EquipmentSlot slot : new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET }) {
			ItemStack armor = event.getEntity().getItemBySlot(slot);
			if(!armor.isEmpty() && armor.getItem() instanceof IPsiEventArmor handler) {
				handler.onEvent(armor, event);
			}
		}
	}

}
