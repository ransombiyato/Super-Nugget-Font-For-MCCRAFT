/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.item;

import net.minecraft.world.item.ItemStack;

public interface IItemHandlerModifiable extends IItemHandler {
	void setStackInSlot(int slot, ItemStack stack);
}
