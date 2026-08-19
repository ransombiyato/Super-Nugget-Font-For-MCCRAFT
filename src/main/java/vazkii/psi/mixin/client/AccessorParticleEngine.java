package vazkii.psi.mixin.client;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ParticleEngine.class)
public interface AccessorParticleEngine {
	@Accessor("RENDER_ORDER")
	static List<ParticleRenderType> psi$getRenderOrder() {
		throw new AssertionError();
	}

	@Mutable
	@Accessor("RENDER_ORDER")
	static void psi$setRenderOrder(List<ParticleRenderType> renderOrder) {
		throw new AssertionError();
	}
}
