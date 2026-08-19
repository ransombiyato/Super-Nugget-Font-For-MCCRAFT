/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.item;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class ComponentItemHandler extends ItemStackHandler {
	private final ItemStack stack;
	private final DataComponentType<ItemContainerContents> component;

	@SuppressWarnings("unchecked")
	public ComponentItemHandler(ItemStack stack, DataComponentType<?> component, int size) {
		super(size);
		this.stack = stack;
		this.component = (DataComponentType<ItemContainerContents>) component;
		ItemContainerContents contents = stack.getOrDefault(this.component, ItemContainerContents.EMPTY);
		contents.copyInto(stacks);
	}

	@Override
	protected void onContentsChanged(int slot) {
		stack.set(component, ItemContainerContents.fromItems(stacks));
	}
}
