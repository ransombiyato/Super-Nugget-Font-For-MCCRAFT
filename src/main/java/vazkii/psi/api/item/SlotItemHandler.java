/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.item;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotItemHandler extends Slot {
	private final IItemHandlerModifiable itemHandler;
	private final int index;

	public SlotItemHandler(IItemHandlerModifiable itemHandler, int index, int xPosition, int yPosition) {
		super(new HandlerContainer(itemHandler), index, xPosition, yPosition);
		this.itemHandler = itemHandler;
		this.index = index;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return itemHandler.isItemValid(index, stack);
	}

	@Override
	public ItemStack getItem() {
		return itemHandler.getStackInSlot(index);
	}

	@Override
	public void set(ItemStack stack) {
		itemHandler.setStackInSlot(index, stack);
		setChanged();
	}

	@Override
	public void setChanged() {}

	@Override
	public int getMaxStackSize() {
		return itemHandler.getSlotLimit(index);
	}

	@Override
	public ItemStack remove(int amount) {
		return itemHandler.extractItem(index, amount, false);
	}

	@Override
	public boolean mayPickup(Player player) {
		return true;
	}

	private static final class HandlerContainer extends SimpleContainer {
		private final IItemHandlerModifiable itemHandler;

		private HandlerContainer(IItemHandlerModifiable itemHandler) {
			super(itemHandler.getSlots());
			this.itemHandler = itemHandler;
		}

		@Override
		public int getContainerSize() {
			return itemHandler.getSlots();
		}

		@Override
		public ItemStack getItem(int slot) {
			return itemHandler.getStackInSlot(slot);
		}

		@Override
		public void setItem(int slot, ItemStack stack) {
			itemHandler.setStackInSlot(slot, stack);
		}

		@Override
		public ItemStack removeItem(int slot, int amount) {
			return itemHandler.extractItem(slot, amount, false);
		}

		@Override
		public boolean stillValid(Player player) {
			return true;
		}
	}
}
