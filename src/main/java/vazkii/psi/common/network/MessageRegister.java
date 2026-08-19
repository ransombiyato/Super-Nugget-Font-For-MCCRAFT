/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

import vazkii.psi.common.network.message.*;

public class MessageRegister {
	public static final StreamCodec<RegistryFriendlyByteBuf, Vec3> VEC3 = new StreamCodec<>() {
		public @NotNull Vec3 decode(RegistryFriendlyByteBuf pBuffer) {
			return pBuffer.readVec3();
		}

		public void encode(RegistryFriendlyByteBuf pBuffer, @NotNull Vec3 pVec3) {
			pBuffer.writeVec3(pVec3);
		}
	};
	private static boolean registered;

	public static void register() {
		if(registered) {
			return;
		}

		registered = true;
		registerC2S(MessageChangeControllerSlot.TYPE, MessageChangeControllerSlot.CODEC);
		registerC2S(MessageChangeSocketableSlot.TYPE, MessageChangeSocketableSlot.CODEC);
		registerC2S(MessageFlashRingSync.TYPE, MessageFlashRingSync.CODEC);
		registerC2S(MessageSpellModified.TYPE, MessageSpellModified.CODEC);
		registerC2S(MessageTriggerJumpSpell.TYPE, MessageTriggerJumpSpell.CODEC);

		registerS2C(MessageAdditiveMotion.TYPE, MessageAdditiveMotion.CODEC);
		registerS2C(MessageBlink.TYPE, MessageBlink.CODEC);
		registerS2C(MessageCADShotEffect.TYPE, MessageCADShotEffect.CODEC);
		registerS2C(MessageDataSync.TYPE, MessageDataSync.CODEC);
		registerS2C(MessageDeductPsi.TYPE, MessageDeductPsi.CODEC);
		registerS2C(MessageEidosSync.TYPE, MessageEidosSync.CODEC);
		registerS2C(MessageLoopcastSync.TYPE, MessageLoopcastSync.CODEC);
		registerS2C(MessageParticleTrail.TYPE, MessageParticleTrail.CODEC);
		registerS2C(MessagePsiOverflow.TYPE, MessagePsiOverflow.CODEC);
		registerS2C(MessageSpamlessChat.TYPE, MessageSpamlessChat.CODEC);
		registerS2C(MessageSpellError.TYPE, MessageSpellError.CODEC);
		registerS2C(MessageVisualEffect.TYPE, MessageVisualEffect.CODEC);
	}

	private static <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
		PayloadTypeRegistry.playC2S().register(type, codec);
		ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
			if(payload instanceof MessageChangeControllerSlot message) {
				message.handle(new FabricPayloadContext(context.player()));
			} else if(payload instanceof MessageChangeSocketableSlot message) {
				message.handle(new FabricPayloadContext(context.player()));
			} else if(payload instanceof MessageFlashRingSync message) {
				message.handle(new FabricPayloadContext(context.player()));
			} else if(payload instanceof MessageSpellModified message) {
				message.handle(new FabricPayloadContext(context.player()));
			} else if(payload instanceof MessageTriggerJumpSpell message) {
				message.handle(new FabricPayloadContext(context.player()));
			}
		});
	}

	private static <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
		PayloadTypeRegistry.playS2C().register(type, codec);
	}

	public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			vazkii.psi.client.network.ClientNetworkHelper.sendToServer(message);
			return;
		}

		PacketDistributor.sendToServer(message);
	}

	public static <MSG extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, MSG message) {
		PacketDistributor.sendToPlayer(player, message);
	}

	public static <MSG extends CustomPacketPayload> void sendToPlayersTrackingEntity(Entity entity, MSG message) {
		PacketDistributor.sendToPlayersTrackingEntity(entity, message);
	}

	public static <MSG extends CustomPacketPayload> void sendToPlayersTrackingEntityAndSelf(Entity entity, MSG message) {
		PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, message);
	}

	public static <MSG extends CustomPacketPayload> void sendToPlayersInDimension(ServerLevel level, MSG message) {
		PacketDistributor.sendToPlayersInDimension(level, message);
	}

	private record FabricPayloadContext(Player player) implements PayloadContext {
	}
}
