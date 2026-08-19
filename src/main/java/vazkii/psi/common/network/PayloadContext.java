/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.network;

import net.minecraft.world.entity.player.Player;

public interface PayloadContext {
	Player player();

	default void enqueueWork(Runnable runnable) {
		runnable.run();
	}
}
