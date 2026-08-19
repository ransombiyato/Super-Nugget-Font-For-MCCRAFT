package vazkii.psi.common.network.message;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.Psi;
import vazkii.psi.common.item.ItemFlashRing;
import vazkii.psi.common.network.PayloadContext;

public record MessageFlashRingSync(Spell spell) implements CustomPacketPayload {

	public static final Identifier ID = Psi.location("message_flash_ring_sync");
	public static final CustomPacketPayload.Type<MessageFlashRingSync> TYPE = new CustomPacketPayload.Type<>(ID);

	public static final StreamCodec<RegistryFriendlyByteBuf, MessageFlashRingSync> CODEC = StreamCodec.composite(
			Spell.STREAM_CODEC, MessageFlashRingSync::spell,
			MessageFlashRingSync::new);

	@Override
	public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(PayloadContext ctx) {
		ctx.enqueueWork(() -> {
			Player player = ctx.player();
			ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
			if(!stack.isEmpty() && stack.getItem() instanceof ItemFlashRing) {
				PsiAPI.getItemCapability(stack, PsiAPI.SPELL_ACCEPTOR_CAPABILITY).setSpell(player, spell);
				ISpellAcceptor.acceptor(stack).setSpell(player, spell);
			} else {
				stack = player.getItemInHand(InteractionHand.OFF_HAND);
				if(!stack.isEmpty() && stack.getItem() instanceof ItemFlashRing) {
					PsiAPI.getItemCapability(stack, PsiAPI.SPELL_ACCEPTOR_CAPABILITY).setSpell(player, spell);
					ISpellAcceptor.acceptor(stack).setSpell(player, spell);
				}
			}
		});
	}
}
