package vazkii.psi.client.fx;

import net.minecraft.client.particle.ParticleRenderType;

/**
 * Compatibility holder for the former custom immediate-mode particle stage.
 * Minecraft 1.21.11 batches particles into predefined render groups.
 */
public final class PsiParticleRenderType {
	private PsiParticleRenderType() {}

	public static final ParticleRenderType NORMAL_RENDER = ParticleRenderType.SINGLE_QUADS;
}
