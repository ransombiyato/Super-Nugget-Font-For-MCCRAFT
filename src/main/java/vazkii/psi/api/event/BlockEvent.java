/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEvent extends PsiEvent implements PsiCancellableEvent {
	private final Level level;
	private final BlockPos pos;
	private final BlockState state;

	public BlockEvent(Level level, BlockPos pos, BlockState state) {
		this.level = level;
		this.pos = pos;
		this.state = state;
	}

	public Level getLevel() {
		return level;
	}

	public BlockPos getPos() {
		return pos;
	}

	public BlockState getState() {
		return state;
	}

	public static class BreakEvent extends BlockEvent {
		private final Player player;

		public BreakEvent(Level level, BlockPos pos, BlockState state, Player player) {
			super(level, pos, state);
			this.player = player;
		}

		public Player getPlayer() {
			return player;
		}
	}

	public static class EntityPlaceEvent extends BlockEvent {
		private final BlockSnapshot snapshot;
		private final BlockState placedAgainst;
		private final Entity entity;

		public EntityPlaceEvent(BlockSnapshot snapshot, BlockState placedAgainst, Entity entity) {
			super(snapshot.level(), snapshot.pos(), snapshot.level().getBlockState(snapshot.pos()));
			this.snapshot = snapshot;
			this.placedAgainst = placedAgainst;
			this.entity = entity;
		}

		public BlockSnapshot getBlockSnapshot() {
			return snapshot;
		}

		public BlockState getPlacedAgainst() {
			return placedAgainst;
		}

		public Entity getEntity() {
			return entity;
		}
	}
}
