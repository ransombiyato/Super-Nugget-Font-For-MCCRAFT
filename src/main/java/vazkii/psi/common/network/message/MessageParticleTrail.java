/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.network.message;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

import vazkii.psi.api.internal.PsiRenderHelper;
import vazkii.psi.common.Psi;
import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.PayloadContext;

public record MessageParticleTrail(Vec3 position, Vec3 direction, double length, int time,
		ItemStack cad) implements CustomPacketPayload {

	public static final Identifier ID = Psi.location("message_particle_trail");
	public static final CustomPacketPayload.Type<MessageParticleTrail> TYPE = new Type<>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, MessageParticleTrail> CODEC = StreamCodec.composite(
			MessageRegister.VEC3, MessageParticleTrail::position,
			MessageRegister.VEC3, MessageParticleTrail::direction,
			ByteBufCodecs.DOUBLE, MessageParticleTrail::length,
			ByteBufCodecs.INT, MessageParticleTrail::time,
			ItemStack.OPTIONAL_STREAM_CODEC, MessageParticleTrail::cad,
			MessageParticleTrail::new);
	private static final int STEPS_PER_UNIT = 4;

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(PayloadContext ctx) {
		ctx.enqueueWork(() -> {
			Level world = Psi.proxy.getClientWorld();
			if(world == null) {
				return;
			}

			int color = Psi.proxy.getColorForCAD(cad);

			float red = PsiRenderHelper.r(color) / 255F;
			float green = PsiRenderHelper.g(color) / 255F;
			float blue = PsiRenderHelper.b(color) / 255F;

			Vec3 ray = direction.normalize().scale(1f / STEPS_PER_UNIT);
			int steps = (int) (length * STEPS_PER_UNIT);

			for(int i = 0; i < steps; i++) {
				double x = position.x + ray.x * i;
				double y = position.y + ray.y * i;
				double z = position.z + ray.z * i;

				Psi.proxy.sparkleFX(world, x, y, z, red, green, blue, 0, 0, 0, 1f, time);
			}
		});
	}
}
