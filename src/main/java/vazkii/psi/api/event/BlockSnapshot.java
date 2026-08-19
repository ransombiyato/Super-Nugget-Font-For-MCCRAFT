/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record BlockSnapshot(ResourceKey<Level> dimension, Level level, BlockPos pos) {
	public static BlockSnapshot create(ResourceKey<Level> dimension, Level level, BlockPos pos) {
		return new BlockSnapshot(dimension, level, pos);
	}
}
