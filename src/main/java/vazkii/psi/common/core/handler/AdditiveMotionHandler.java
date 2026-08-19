/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.core.handler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.message.MessageAdditiveMotion;

import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;

public class AdditiveMotionHandler {
	private static final Map<Entity, Vec3> toUpdate = new WeakHashMap<>();
	private static boolean fabricCallbacksRegistered;

	public static void registerFabricCallbacks() {
		if(fabricCallbacksRegistered) {
			return;
		}

		fabricCallbacksRegistered = true;
		ServerTickEvents.END_SERVER_TICK.register(server -> flushMotionUpdates());
	}

	public static void addMotion(Entity entity, double x, double y, double z) {
		if(x == 0 && y == 0 && z == 0) {
			return;
		}
		if(!entity.level().isClientSide()) {
			Vec3 base = toUpdate.getOrDefault(entity, Vec3.ZERO);
			toUpdate.put(entity, base.add(x, y, z));
		}
	}

	private static void flushMotionUpdates() {
		if(toUpdate.isEmpty()) {
			return;
		}

		for(Map.Entry<Entity, Vec3> entry : new ArrayList<>(toUpdate.entrySet())) {
			Entity entity = entry.getKey();
			Vec3 vec = entry.getValue();
			if(entity == null || vec == null || entity.level().isClientSide() || entity.hurtMarked) {
				continue;
			}

			MessageAdditiveMotion motion = new MessageAdditiveMotion(entity.getId(), vec.x, vec.y, vec.z);
			// We want a player's motion to be handled client-side to ensure movement consistency.
			if(entity instanceof ServerPlayer player) {
				MessageRegister.sendToPlayer(player, motion);
			} else {
				entity.push(vec.x, vec.y, vec.z);
			}
			if(entity.level() instanceof ServerLevel) {
				MessageRegister.sendToPlayersTrackingEntity(entity, motion);
			}
		}

		toUpdate.clear();
	}

}
