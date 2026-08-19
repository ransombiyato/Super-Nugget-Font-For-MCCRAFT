/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.util;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

public final class SimpleTier {
	private SimpleTier() {}

	public static ToolMaterial create(TagKey<Block> incorrectBlocksForDrops, int durability, float speed,
			float attackDamageBonus, int enchantmentValue, TagKey<Item> repairItems) {
		return new ToolMaterial(incorrectBlocksForDrops, durability, speed, attackDamageBonus, enchantmentValue, repairItems);
	}
}
