/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.event;

import net.minecraft.world.entity.player.Player;

public class PsiPlayerEvent extends PsiEvent {
	private final Player entity;

	public PsiPlayerEvent(Player entity) {
		this.entity = entity;
	}

	public Player getEntity() {
		return entity;
	}
}
