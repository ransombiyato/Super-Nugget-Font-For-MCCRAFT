/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.block.tile;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.psi.api.cad.*;
import vazkii.psi.api.event.PsiEventBus;
import vazkii.psi.api.item.IItemHandlerModifiable;
import vazkii.psi.api.item.ItemStackHandler;
import vazkii.psi.common.block.base.ModBlocks;
import vazkii.psi.common.block.tile.container.ContainerCADAssembler;
import vazkii.psi.common.core.handler.PsiSoundHandler;
import vazkii.psi.common.item.ItemCAD;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TileCADAssembler extends BlockEntity implements ITileCADAssembler, MenuProvider, ExtendedScreenHandlerFactory<BlockPos> {
	private final CADStackHandler inventory = new CADStackHandler();
	private ItemStack cachedCAD = null;

	public TileCADAssembler(BlockPos pos, BlockState state) {
		super(ModBlocks.cadAssemblerType.get(), pos, state);
	}

	public IItemHandlerModifiable getInventory() {
		return inventory;
	}

	@Override
	public void clearCachedCAD() {
		cachedCAD = null;
	}

	@Override
	public ItemStack getCachedCAD(Player player) {
		ItemStack cad = cachedCAD;
		if(cad == null) {
			ItemStack assembly = getStackForComponent(EnumCADComponent.ASSEMBLY);
			if(!assembly.isEmpty()) {
				List<ItemStack> components = IntStream.range(1, 6).mapToObj(inventory::getStackInSlot).collect(Collectors.toList());
				cad = ItemCAD.makeCADWithAssembly(assembly, components);
			} else {
				cad = ItemStack.EMPTY;
			}

			AssembleCADEvent assembling = new AssembleCADEvent(cad, this, player);

			PsiEventBus.post(assembling);

			if(assembling.isCanceled()) {
				cad = ItemStack.EMPTY;
			} else {
				cad = assembling.getCad();
			}

			cachedCAD = cad;
		}

		return cad;
	}

	@Override
	public ItemStack getStackForComponent(EnumCADComponent componentType) {
		return inventory.getStackInSlot(componentType.ordinal() + 1);
	}

	@Override
	public boolean setStackForComponent(EnumCADComponent componentType, ItemStack component) {
		int slot = componentType.ordinal() + 1;
		if(component.isEmpty()) {
			inventory.setStackInSlot(slot, component);
			return true;
		} else if(component.getItem() instanceof ICADComponent componentItem) {
			if(componentItem.getComponentType(component) == componentType) {
				inventory.setStackInSlot(slot, component);
				return true;
			}
		}

		return false;
	}

	@Override
	public ItemStack getSocketableStack() {
		return inventory.getStackInSlot(0);
	}

	@Override
	public ISocketable getSocketable() {
		return ISocketable.socketable(getSocketableStack());
	}

	@Override
	public boolean setSocketableStack(ItemStack stack) {
		if(stack.isEmpty() || ISocketable.isSocketable(stack)) {
			inventory.setStackInSlot(0, stack);
			return true;
		}

		return false;
	}

	@Override
	public void onCraftCAD(ItemStack cad) {
		PsiEventBus.post(new PostCADCraftEvent(cad, this));
		for(int i = 1; i < 6; i++) {
			inventory.setStackInSlot(i, ItemStack.EMPTY);
		}

		if(level == null) {
			return;
		}

		if(!level.isClientSide()) {
			level.playSound(null, getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, PsiSoundHandler.cadCreate, SoundSource.BLOCKS, 0.5F, 1F);
		}
	}

	@Override
	public boolean isBulletSlotEnabled(int slot) {
		if(getSocketableStack().isEmpty()) {
			return false;
		}
		ISocketable socketable = getSocketable();
		return socketable != null && socketable.isSocketSlotAvailable(slot);
	}

	@Override
	protected void saveAdditional(@NotNull ValueOutput output) {
		super.saveAdditional(output);
		ContainerHelper.saveAllItems(output, inventory.getItems());
	}

	@Override
	protected void loadAdditional(@NotNull ValueInput input) {
		super.loadAdditional(input);
		ContainerHelper.loadAllItems(input, inventory.getItems());
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this, (BlockEntity e, RegistryAccess provider) -> getUpdateTag(provider));
		//return new ClientboundBlockEntityDataPacket(getBlockPos(), -1, getUpdateTag());
	}//TODO Hopefully fixed?

	@NotNull
	@Override
	public CompoundTag getUpdateTag(HolderLookup.@NotNull Provider provider) {
		return saveWithoutMetadata(provider);
	}

	@NotNull
	@Override
	public Component getDisplayName() {
		return Component.translatable(ModBlocks.cadAssembler.get().getDescriptionId());
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int i, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
		return new ContainerCADAssembler(i, playerInventory, this);
	}

	@Override
	public BlockPos getScreenOpeningData(ServerPlayer player) {
		return worldPosition;
	}

	private class CADStackHandler extends ItemStackHandler {

		private CADStackHandler() {
			super(6);
		}

		private NonNullList<ItemStack> getItems() {
			return this.stacks;
		}

		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			if(0 < slot && slot < 6) {
				clearCachedCAD();
			}
			setChanged();
		}

		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			if(stack.isEmpty()) {
				return true;
			}

			if(slot == 0) {
				return ISocketable.isSocketable(stack);
			} else if(slot < 6) {
				return stack.getItem() instanceof ICADComponent &&
						((ICADComponent) stack.getItem()).getComponentType(stack).ordinal() == slot - 1;
			}

			return false;
		}
	}
}
