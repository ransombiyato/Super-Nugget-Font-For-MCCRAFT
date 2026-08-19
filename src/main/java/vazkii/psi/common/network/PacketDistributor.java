/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.network;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class PacketDistributor {
	private PacketDistributor() {}

	public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {}

	public static <MSG extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, MSG message) {
		if(ServerPlayNetworking.canSend(player, message.type())) {
			ServerPlayNetworking.send(player, message);
		}
	}

	public static <MSG extends CustomPacketPayload> void sendToPlayersTrackingEntity(Entity entity, MSG message) {
		for(ServerPlayer player : PlayerLookup.tracking(entity)) {
			sendToPlayer(player, message);
		}
	}

	public static <MSG extends CustomPacketPayload> void sendToPlayersTrackingEntityAndSelf(Entity entity, MSG message) {
		sendToPlayersTrackingEntity(entity, message);
		if(entity instanceof ServerPlayer player) {
			sendToPlayer(player, message);
		}
	}

	public static <MSG extends CustomPacketPayload> void sendToPlayersInDimension(ServerLevel level, MSG message) {
		for(ServerPlayer player : PlayerLookup.world(level)) {
			sendToPlayer(player, message);
		}
	}
}
