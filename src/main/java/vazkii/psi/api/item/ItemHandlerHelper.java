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

public final class ItemHandlerHelper {
	private ItemHandlerHelper() {}

	public static int calcRedstoneFromInventory(IItemHandler handler) {
		int slots = handler.getSlots();
		if(slots == 0) {
			return 0;
		}

		float fullness = 0.0F;
		int occupied = 0;
		for(int slot = 0; slot < slots; slot++) {
			ItemStack stack = handler.getStackInSlot(slot);
			if(!stack.isEmpty()) {
				fullness += (float) stack.getCount() / Math.min(handler.getSlotLimit(slot), stack.getMaxStackSize());
				occupied++;
			}
		}

		fullness /= slots;
		return (int) Math.floor(fullness * 14.0F) + (occupied > 0 ? 1 : 0);
	}
}
