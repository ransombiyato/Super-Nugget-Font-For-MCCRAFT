/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class ItemStackHandler implements IItemHandlerModifiable {
	protected NonNullList<ItemStack> stacks;

	public ItemStackHandler(int size) {
		stacks = NonNullList.withSize(size, ItemStack.EMPTY);
	}

	@Override
	public int getSlots() {
		return stacks.size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		return stacks.get(slot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		validateSlotIndex(slot);
		stacks.set(slot, stack);
		onContentsChanged(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		validateSlotIndex(slot);
		if(stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		if(!isItemValid(slot, stack)) {
			return stack;
		}

		ItemStack existing = stacks.get(slot);
		int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
		if(!existing.isEmpty()) {
			if(!ItemStack.isSameItemSameComponents(existing, stack)) {
				return stack;
			}
			limit -= existing.getCount();
		}
		if(limit <= 0) {
			return stack;
		}

		int toMove = Math.min(limit, stack.getCount());
		if(!simulate) {
			if(existing.isEmpty()) {
				stacks.set(slot, stack.copyWithCount(toMove));
			} else {
				existing.grow(toMove);
			}
			onContentsChanged(slot);
		}

		return stack.copyWithCount(stack.getCount() - toMove);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		validateSlotIndex(slot);
		if(amount <= 0) {
			return ItemStack.EMPTY;
		}

		ItemStack existing = stacks.get(slot);
		if(existing.isEmpty()) {
			return ItemStack.EMPTY;
		}

		int toExtract = Math.min(amount, existing.getCount());
		ItemStack extracted = existing.copyWithCount(toExtract);
		if(!simulate) {
			existing.shrink(toExtract);
			if(existing.isEmpty()) {
				stacks.set(slot, ItemStack.EMPTY);
			}
			onContentsChanged(slot);
		}
		return extracted;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return true;
	}

	protected void onContentsChanged(int slot) {}

	protected void validateSlotIndex(int slot) {
		if(slot < 0 || slot >= stacks.size()) {
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
		}
	}
}
