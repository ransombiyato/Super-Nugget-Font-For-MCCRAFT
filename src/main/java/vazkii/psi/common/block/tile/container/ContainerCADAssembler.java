/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.block.tile.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICADComponent;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.inventory.InventorySocketable;
import vazkii.psi.api.item.IItemHandlerModifiable;
import vazkii.psi.api.item.SlotItemHandler;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.common.block.base.ModBlocks;
import vazkii.psi.common.block.tile.TileCADAssembler;
import vazkii.psi.common.block.tile.container.slot.InventoryAssemblerOutput;
import vazkii.psi.common.block.tile.container.slot.SlotCADOutput;
import vazkii.psi.common.block.tile.container.slot.SlotSocketable;
import vazkii.psi.common.block.tile.container.slot.ValidatorSlot;

public class ContainerCADAssembler extends AbstractContainerMenu {
	private static final EquipmentSlot[] equipmentSlots = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
	public final TileCADAssembler assembler;

	private final int cadComponentStart;
	private final int socketableStart;
	private final int socketableEnd;
	private final int bulletStart;
	private final int bulletEnd;
	private final int playerStart;
	private final int playerEnd;
	private final int hotbarStart;
	private final int hotbarEnd;
	private final int armorStart;

	public ContainerCADAssembler(int windowId, Inventory playerInventory, TileCADAssembler assembler) {
		super(ModBlocks.containerCADAssembler.get(), windowId);
		Player player = playerInventory.player;
		int playerSize = playerInventory.getContainerSize();

		this.assembler = assembler;
		IItemHandlerModifiable assemblerInv = assembler.getInventory();
		assembler.clearCachedCAD();

		InventoryAssemblerOutput output = new InventoryAssemblerOutput(player, assembler);
		InventorySocketable bullets = new InventorySocketable(assembler, assembler.getSocketableStack());

		addSlot(new SlotCADOutput(output, assembler, 120, 35));

		cadComponentStart = slots.size();
		addSlot(new SlotItemHandler(assemblerInv, EnumCADComponent.ASSEMBLY.ordinal() + 1, 120, 91));
		addSlot(new SlotItemHandler(assemblerInv, EnumCADComponent.CORE.ordinal() + 1, 100, 91));
		addSlot(new SlotItemHandler(assemblerInv, EnumCADComponent.SOCKET.ordinal() + 1, 140, 91));
		addSlot(new SlotItemHandler(assemblerInv, EnumCADComponent.BATTERY.ordinal() + 1, 110, 111));
		addSlot(new SlotItemHandler(assemblerInv, EnumCADComponent.DYE.ordinal() + 1, 130, 111));

		socketableStart = slots.size();
		addSlot(new SlotSocketable(assemblerInv, bullets, 0, 35, 21));
		socketableEnd = slots.size();

		bulletStart = slots.size();
		for(int row = 0; row < 4; row++) {
			for(int col = 0; col < 3; col++) {
				addSlot(new ValidatorSlot(bullets, col + row * 3, 17 + col * 18, 57 + row * 18));
			}
		}
		bulletEnd = slots.size();

		int xs = 48;
		int ys = 143;

		playerStart = slots.size();
		for(int row = 0; row < 3; row++) {
			for(int col = 0; col < 9; col++) {
				addSlot(new Slot(playerInventory, col + row * 9 + 9, xs + col * 18, ys + row * 18));
			}
		}
		playerEnd = slots.size();

		hotbarStart = slots.size();
		for(int col = 0; col < 9; col++) {
			addSlot(new Slot(playerInventory, col, xs + col * 18, ys + 58));
		}
		hotbarEnd = slots.size();

		armorStart = slots.size();
		for(int armorSlot = 0; armorSlot < 4; armorSlot++) {
			final EquipmentSlot slot = equipmentSlots[armorSlot];

			addSlot(new Slot(playerInventory, playerSize - 2 - armorSlot,
					xs - 27, ys + 18 * armorSlot) {
				@Override
				public int getMaxStackSize() {
					return 1;
				}

				@Override
				public boolean mayPlace(@NotNull ItemStack stack) {
					return !stack.isEmpty() && player.getEquipmentSlotForItem(stack) == slot;
				}

				@Environment(EnvType.CLIENT)
				@Override
				public Identifier getNoItemIcon() {
					Identifier texture = switch(slot) {
					case HEAD -> InventoryMenu.EMPTY_ARMOR_SLOT_HELMET;
					case CHEST -> InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE;
					case LEGS -> InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS;
					case FEET -> InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS;
					default -> InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD;
					};
					return texture;
				}
			});
		}

		addSlot(new Slot(playerInventory, playerSize - 1, 219, 143) {
			@Environment(EnvType.CLIENT)
			@Override
			public Identifier getNoItemIcon() {
				return InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD;
			}
		});
	}

	public static ContainerCADAssembler fromNetwork(int windowId, Inventory playerInventory, BlockPos pos) {
		return new ContainerCADAssembler(windowId, playerInventory, (TileCADAssembler) playerInventory.player.level().getBlockEntity(pos));
	}

	@Override
	public boolean stillValid(@NotNull Player playerIn) {
		return !playerIn.isRemoved() && assembler.getBlockPos().distToCenterSqr(playerIn.position()) <= 64;
	}

	@NotNull
	@Override
	public ItemStack quickMoveStack(@NotNull Player playerIn, int from) {
		ItemStack mergeStack = ItemStack.EMPTY;
		if(from > slots.size() - 1 || from < 0) {
			return mergeStack;
		}

		Slot slot = slots.get(from);

		if(slot.hasItem()) {
			ItemStack stackInSlot = slot.getItem();
			mergeStack = stackInSlot.copy();

			if(from >= playerStart) {
				if(stackInSlot.getItem() instanceof ICADComponent) {
					EnumCADComponent componentType = ((ICADComponent) stackInSlot.getItem()).getComponentType(stackInSlot);
					int componentSlot = cadComponentStart + componentType.ordinal();
					if(!moveItemStackTo(stackInSlot, componentSlot, componentSlot + 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if(ISocketable.isSocketable(stackInSlot)) {
					if(!moveItemStackTo(stackInSlot, socketableStart, socketableEnd, false)) {
						return ItemStack.EMPTY;
					}
				} else if(ISpellAcceptor.isContainer(stackInSlot)) {
					if(!moveItemStackTo(stackInSlot, bulletStart, bulletEnd, false)) {
						return ItemStack.EMPTY;
					}
				} else if(from < hotbarStart) {
					if(!moveItemStackTo(stackInSlot, hotbarStart, hotbarEnd, true)) {
						return ItemStack.EMPTY;
					}
				} else if(!moveItemStackTo(stackInSlot, playerStart, playerEnd, false)) {
					return ItemStack.EMPTY;
				}
			} else if(!moveItemStackTo(stackInSlot, playerStart, hotbarEnd, true)) {
				return ItemStack.EMPTY;
			}

			slot.setChanged();

			if(stackInSlot.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else if(stackInSlot.getCount() == mergeStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, stackInSlot);
		}

		return mergeStack;
	}

	@Override
	public void removed(@NotNull Player playerIn) {
		super.removed(playerIn);
		assembler.clearCachedCAD();
	}
}
