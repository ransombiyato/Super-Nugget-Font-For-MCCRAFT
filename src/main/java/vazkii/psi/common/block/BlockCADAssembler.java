/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.psi.api.item.IItemHandler;
import vazkii.psi.api.item.ItemHandlerHelper;
import vazkii.psi.common.block.tile.TileCADAssembler;

public class BlockCADAssembler extends HorizontalDirectionalBlock implements EntityBlock {
	public static final MapCodec<BlockCADAssembler> CODEC = simpleCodec(BlockCADAssembler::new);

	public BlockCADAssembler(Properties props) {
		super(props);
	}

	@Override
	public @NotNull MapCodec<BlockCADAssembler> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
	}

	@Override
	public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(@NotNull BlockState blockState, Level worldIn, @NotNull BlockPos pos, @NotNull Direction direction) {
		if(worldIn.getBlockEntity(pos) instanceof TileCADAssembler assembler) {
			IItemHandler handler = assembler.getInventory();
			return ItemHandlerHelper.calcRedstoneFromInventory(handler);
		}
		return 0;
	}

	@Override
	public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Player playerIn, @NotNull BlockHitResult rayTraceResult) {
		if(world.isClientSide()) {
			return InteractionResult.SUCCESS;
		} else {
			MenuProvider container = state.getMenuProvider(world, pos);
			if(container != null) {
				playerIn.openMenu(container);
			}
		}
		return InteractionResult.CONSUME;
	}

	@Nullable
	@Override
	public MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof TileCADAssembler) {
			return (MenuProvider) te;
		}
		return null;
	}

	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new TileCADAssembler(pos, state);
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, boolean movedByPiston) {
		if(!movedByPiston) {
			TileCADAssembler te = world.getBlockEntity(pos) instanceof TileCADAssembler assembler ? assembler : null;
			if(te != null) {
				for(int i = 0; i < te.getInventory().getSlots(); i++) {
					ItemStack stack = te.getInventory().getStackInSlot(i);
					if(!stack.isEmpty()) {
						Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
					}
				}
			}
		}

		super.affectNeighborsAfterRemoval(state, world, pos, movedByPiston);
	}

}
