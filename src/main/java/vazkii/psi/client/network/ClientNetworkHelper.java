/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import vazkii.psi.common.network.PayloadContext;
import vazkii.psi.common.network.message.*;

public final class ClientNetworkHelper {
	private static boolean registeredReceivers;

	private ClientNetworkHelper() {}

	public static void registerReceivers() {
		if(registeredReceivers) {
			return;
		}

		registeredReceivers = true;
		ClientPlayNetworking.registerGlobalReceiver(MessageAdditiveMotion.TYPE, (payload, context) -> payload.handle(new ClientPayloadContext(context.player())));
		ClientPlayNetworking.registerGlobalReceiver(MessageBlink.TYPE, (payload, context) -> payload.handle(new ClientPayloadContext(context.player())));
		ClientPlayNetworking.registerGlobalReceiver(MessageCADShotEffect.TYPE, (payload, context) -> payload.handle(new ClientPayloadContext(context.player())));
		ClientPlayNetworking.registerGlobalReceiver(MessageDataSync.TYPE, (payload, context) -> payload.handle(new ClientPayloadContext(context.player())));
		ClientPlayNetworking.registerGlobalReceiver(MessageDeductPsi.TYPE, (payload, context) -> payload.handle(new ClientPayloadContext(context.player())));
		ClientPlayNetworking.registerGlobalReceiver(MessageEidosSync.TYPE, (payload, context) -> payload.handle(new ClientPayloadContext(context.player())));
		ClientPlayNetworking.registerGlobalReceiver(MessageLoopcastSync.TYPE, (payload, context) -> payload.handle(new ClientPayloadContext(context.player())));
		ClientPlayNetworking.registerGlobalReceiver(MessageParticleTrail.TYPE, (payload, context) -> payload.handle(new ClientPayloadContext(context.player())));
		ClientPlayNetworking.registerGlobalReceiver(MessagePsiOverflow.TYPE, (payload, context) -> payload.handle(new ClientPayloadContext(context.player())));
		ClientPlayNetworking.registerGlobalReceiver(MessageSpamlessChat.TYPE, (payload, context) -> payload.handle(new ClientPayloadContext(context.player())));
		ClientPlayNetworking.registerGlobalReceiver(MessageSpellError.TYPE, (payload, context) -> payload.handle(new ClientPayloadContext(context.player())));
		ClientPlayNetworking.registerGlobalReceiver(MessageVisualEffect.TYPE, (payload, context) -> payload.handle(new ClientPayloadContext(context.player())));
	}

	public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {
		if(ClientPlayNetworking.canSend(message.type())) {
			ClientPlayNetworking.send(message);
		}
	}

	private record ClientPayloadContext(Player player) implements PayloadContext {
	}
}
