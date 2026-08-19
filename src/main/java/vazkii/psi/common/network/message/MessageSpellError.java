/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.network.message;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import org.jetbrains.annotations.NotNull;

import vazkii.psi.common.Psi;
import vazkii.psi.common.network.PayloadContext;

public record MessageSpellError(String message, int x, int y) implements CustomPacketPayload {

	public static final Identifier ID = Psi.location("message_spell_error");
	public static final CustomPacketPayload.Type<MessageSpellError> TYPE = new Type<>(ID);

	public static final StreamCodec<RegistryFriendlyByteBuf, MessageSpellError> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, MessageSpellError::message,
			ByteBufCodecs.INT, MessageSpellError::x,
			ByteBufCodecs.INT, MessageSpellError::y,
			MessageSpellError::new);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(PayloadContext ctx) {
		ctx.enqueueWork(() -> {
			ChatComponent chatGui = Minecraft.getInstance().gui.getChat();
			Component chatMessage = Component.translatable(message, gridColumn(x), y).setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
			chatGui.addMessage(chatMessage);
		});
	}

	private static String gridColumn(int x) {
		return String.valueOf((char) ('A' + Math.max(0, x - 1)));
	}
}
