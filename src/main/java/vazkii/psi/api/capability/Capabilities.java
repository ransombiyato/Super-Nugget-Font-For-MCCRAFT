/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.capability;

import net.minecraft.resources.Identifier;

import vazkii.psi.api.item.IItemHandler;

public final class Capabilities {
	private Capabilities() {}

	public static final class ItemHandler {
		public static final ItemCapability<IItemHandler, Void> ITEM =
				ItemCapability.createVoid(Identifier.fromNamespaceAndPath("psi", "item_handler"), IItemHandler.class);
		public static final ItemCapability<IItemHandler, Void> BLOCK = ITEM;

		private ItemHandler() {}
	}
}
