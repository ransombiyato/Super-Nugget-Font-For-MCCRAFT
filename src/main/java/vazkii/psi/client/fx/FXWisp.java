/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.client.fx;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;

import org.jetbrains.annotations.NotNull;

// https://github.com/Vazkii/Botania/blob/1.15/src/main/java/vazkii/botania/client/fx/FXWisp.java
@Environment(EnvType.CLIENT)
public class FXWisp extends SingleQuadParticle {

	public static final ParticleRenderType NORMAL_RENDER = ParticleRenderType.SINGLE_QUADS;

	private final float moteParticleScale;
	private final int moteHalfLife;

	public FXWisp(ClientLevel world, double d, double d1, double d2, double xSpeed, double ySpeed, double zSpeed,
			float size, float red, float green, float blue, float maxAgeMul, TextureAtlasSprite sprite) {
		super(world, d, d1, d2, 0, 0, 0, sprite);
		// super applies wiggle to motion so set it here instead
		xd = xSpeed;
		yd = ySpeed;
		zd = zSpeed;
		rCol = red;
		gCol = green;
		bCol = blue;
		alpha = 0.375F;
		gravity = 0;
		quadSize = (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F * size;
		moteParticleScale = quadSize;
		lifetime = (int) (28D / (Math.random() * 0.3D + 0.7D) * maxAgeMul);

		moteHalfLife = lifetime / 2;
		setSize(0.01F, 0.01F);

		xo = x;
		yo = y;
		zo = z;
		this.hasPhysics = true;
	}

	@Override
	public float getQuadSize(float scaleFactor) {
		float ageScale = (float) age / (float) moteHalfLife;
		if(ageScale > 1F) {
			ageScale = 2 - ageScale;
		}

		quadSize = moteParticleScale * ageScale * 0.5F;
		return quadSize;
	}

	@Override
	protected int getLightColor(float partialTicks) {
		return 0xF000F0;
	}

	@NotNull
	@Override
	public ParticleRenderType getGroup() {
		return NORMAL_RENDER;
	}

	@Override
	protected Layer getLayer() {
		return Layer.TRANSLUCENT;
	}

	// [VanillaCopy] of super, without drag when onGround is true
	@Override
	public void tick() {
		if(this.age++ >= this.lifetime) {
			this.remove();
			return;
		}

		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		this.yd -= 0.04D * (double) this.gravity;
		this.move(this.xd, this.yd, this.zd);
		this.xd *= 0.9800000190734863D;
		this.yd *= 0.9800000190734863D;
		this.zd *= 0.9800000190734863D;
	}

	public static class Factory implements ParticleProvider<WispParticleData> {
		private final SpriteSet sprite;

		public Factory(SpriteSet sprite) {
			this.sprite = sprite;
		}

		@Override
		public Particle createParticle(WispParticleData data, @NotNull ClientLevel world, double x, double y, double z, double mx, double my, double mz, RandomSource random) {
			return new FXWisp(world, x, y, z, mx, my, mz, data.size(), data.r(), data.g(), data.b(), data.maxAgeMul(), sprite.get(random));
		}
	}
}
