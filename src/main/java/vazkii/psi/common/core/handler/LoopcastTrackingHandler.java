/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.core.handler;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.message.MessageLoopcastSync;

public class LoopcastTrackingHandler {
	private static boolean fabricCallbacksRegistered;

	public static void registerFabricCallbacks() {
		if(fabricCallbacksRegistered) {
			return;
		}

		fabricCallbacksRegistered = true;
		ServerPlayerEvents.JOIN.register(player -> syncDataFor(player, player));
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> syncDataFor(newPlayer, newPlayer));
		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> syncDataFor(player, player));
		EntityTrackingEvents.START_TRACKING.register((tracked, player) -> {
			if(tracked instanceof Player trackedPlayer) {
				syncDataFor(trackedPlayer, player);
			}
		});
	}

	public static void syncDataFor(Player player, ServerPlayer receiver) {
		PlayerDataHandler.PlayerData data = PlayerDataHandler.get(player);

		MessageRegister.sendToPlayer(receiver, new MessageLoopcastSync(player.getId(), data.loopcasting, data.loopcastHand));
	}

	public static void syncForTrackersAndSelf(ServerPlayer playerEntity) {
		PlayerDataHandler.PlayerData data = PlayerDataHandler.get(playerEntity);
		MessageLoopcastSync messageLoopcastSync = new MessageLoopcastSync(playerEntity.getId(), data.loopcasting, data.loopcastHand);
		MessageRegister.sendToPlayersTrackingEntity(playerEntity, messageLoopcastSync);
		MessageRegister.sendToPlayer(playerEntity, messageLoopcastSync);
	}
}
