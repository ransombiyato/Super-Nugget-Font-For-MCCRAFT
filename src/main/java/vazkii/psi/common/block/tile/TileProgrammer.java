/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.jetbrains.annotations.NotNull;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.block.BlockProgrammer;
import vazkii.psi.common.block.base.ModBlocks;
import vazkii.psi.common.spell.SpellCompiler;

public class TileProgrammer extends BlockEntity {
	private static final String TAG_SPELL = "spell";
	private static final String TAG_PLAYER_LOCK = "playerLock";
	public Spell spell;
	public boolean enabled;

	public String playerLock = "";

	public TileProgrammer(BlockPos pos, BlockState state) {
		super(ModBlocks.programmerType.get(), pos, state);
	}

	public boolean isEnabled() {
		return spell != null && !spell.grid.isEmpty();
	}

	public boolean canCompile() {
		return isEnabled() && new SpellCompiler().compile(spell).left().isPresent();
	}

	public void onSpellChanged() {
		boolean wasEnabled = enabled;
		enabled = isEnabled();
		if(getLevel() == null) {
			return;
		}

		if(wasEnabled != enabled) {
			getLevel().setBlockAndUpdate(worldPosition, getBlockState().setValue(BlockProgrammer.ENABLED, enabled));
		}
		setChanged();
	}

	@Override
	protected void loadAdditional(@NotNull ValueInput input) {
		super.loadAdditional(input);
		spell = input.read(TAG_SPELL, Spell.CODEC.codec()).orElse(null);
		playerLock = input.getStringOr(TAG_PLAYER_LOCK, "");
	}

	@Override
	protected void saveAdditional(@NotNull ValueOutput output) {
		super.saveAdditional(output);
		output.storeNullable(TAG_SPELL, Spell.CODEC.codec(), spell);
		output.putString(TAG_PLAYER_LOCK, playerLock);
	}

	public void readPacketNBT(CompoundTag cmp) {
		CompoundTag spellCmp = cmp.getCompoundOrEmpty(TAG_SPELL);
		if(spell == null) {
			spell = Spell.createFromNBT(spellCmp);
		} else {
			spell.readFromNBT(spellCmp);
		}
		playerLock = cmp.getStringOr(TAG_PLAYER_LOCK, "");
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
		return saveWithoutMetadata(pRegistries);
	}

	public boolean canPlayerInteract(Player player) {
		return player.isAlive() && player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
	}

}
