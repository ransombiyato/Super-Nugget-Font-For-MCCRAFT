/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.platform;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.UseRemainder;

public final class FabricHooks {
	private FabricHooks() {}

	public static ItemStack getCraftingRemainingItem(ItemStack stack) {
		UseRemainder remainder = stack.get(DataComponents.USE_REMAINDER);
		if(stack.isEmpty() || remainder == null) {
			return ItemStack.EMPTY;
		}
		return remainder.convertInto();
	}
}
