package vazkii.psi.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.psi.client.fx.PsiParticleRenderType;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {
	@Inject(
		method = "render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;F)V",
		at = @At(value = "JUMP", opcode = Opcodes.GOTO),
		slice = @Slice(
			from = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferUploader;drawWithShader(Lcom/mojang/blaze3d/vertex/MeshData;)V", remap = false),
			to = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;depthMask(Z)V", remap = false)
		)
	)
	private void afterRenderedType(LightTexture lightTexture, Camera camera, float partialTick, CallbackInfo ci, @Local ParticleRenderType particleRenderType) {
		if(particleRenderType instanceof PsiParticleRenderType psiParticleRenderType) {
			psiParticleRenderType.end();
		}
	}
}
