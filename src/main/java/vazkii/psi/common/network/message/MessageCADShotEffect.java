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
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.common.Psi;
import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.PayloadContext;

public record MessageCADShotEffect(Vec3 playerPosition, Vec3 shotPosition, Vec3 look, double width, int particles,
		ItemStack cad) implements CustomPacketPayload {

	public static final Identifier ID = Psi.location("message_cad_shot_effect");
	public static final CustomPacketPayload.Type<MessageCADShotEffect> TYPE = new Type<>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, MessageCADShotEffect> CODEC = StreamCodec.composite(
			MessageRegister.VEC3, MessageCADShotEffect::playerPosition,
			MessageRegister.VEC3, MessageCADShotEffect::shotPosition,
			MessageRegister.VEC3, MessageCADShotEffect::look,
			ByteBufCodecs.DOUBLE, MessageCADShotEffect::width,
			ByteBufCodecs.INT, MessageCADShotEffect::particles,
			ItemStack.OPTIONAL_STREAM_CODEC, MessageCADShotEffect::cad,
			MessageCADShotEffect::new);

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
			float r = PsiRenderHelper.r(color) / 255F;
			float g = PsiRenderHelper.g(color) / 255F;
			float b = PsiRenderHelper.b(color) / 255F;

			for(int i = 0; i < particles; i++) {
				double x = playerPosition.x + (Math.random() - 0.5) * 2.1 * width;
				double y = playerPosition.y + 0.35D;
				double z = playerPosition.z + (Math.random() - 0.5) * 2.1 * width;
				float grav = -0.15F - (float) Math.random() * 0.03F;
				Psi.proxy.sparkleFX(world, x, y, z, r, g, b, 0, -grav, 0, 0.25F, 15);
			}

			Vec3 normalizedLook = look.normalize();
			Vector3 lookOrig = new Vector3(normalizedLook);
			for(int i = 0; i < 25; i++) {
				Vector3 particleMotion = lookOrig.copy();
				double spread = 0.25;
				particleMotion.x += (Math.random() - 0.5) * spread;
				particleMotion.y += (Math.random() - 0.5) * spread;
				particleMotion.z += (Math.random() - 0.5) * spread;
				particleMotion.normalize().multiply(0.15);

				Psi.proxy.sparkleFX(world, shotPosition.x, shotPosition.y, shotPosition.z, r, g, b,
						(float) particleMotion.x, (float) particleMotion.y, (float) particleMotion.z, 0.3F, 5);
			}
		});
	}
}
