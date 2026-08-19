/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.platform;

import com.mojang.authlib.GameProfile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public abstract class PsiFakePlayer extends Player {
	protected PsiFakePlayer(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
		super(level, gameProfile);
		setPos(pos.getX(), pos.getY(), pos.getZ());
		setYRot(yRot);
	}

	protected PsiFakePlayer(Level level) {
		this(level, BlockPos.ZERO, 0, new GameProfile(new UUID(0, 0), "PsiFakePlayer"));
	}
}
