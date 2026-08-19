/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.client.render.tile;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.internal.PsiRenderHelper;
import vazkii.psi.common.Psi;
import vazkii.psi.common.block.BlockConjured;
import vazkii.psi.common.block.base.ModBlocks;
import vazkii.psi.common.block.tile.TileConjured;

import java.util.Arrays;

public class RenderTileConjured implements BlockEntityRenderer<TileConjured> {
	private static final int FULLBRIGHT = 0xF000F0;
	private static final int PARTICLES_PER_EDGE = 3;
	private static final float PARTICLE_SIZE = 0.07F;
	private static final RenderType LAYER = RenderType.create(PsiAPI.MOD_ID + ":conjured_block",
			DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,
			VertexFormat.Mode.QUADS,
			256,
			false,
			true,
			RenderType.CompositeState.builder()
					.setShaderState(RenderStateShard.POSITION_COLOR_LIGHTMAP_SHADER)
					.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
					.setCullState(RenderStateShard.NO_CULL)
					.setLightmapState(RenderStateShard.LIGHTMAP)
					.setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
					.createCompositeState(false));

	public RenderTileConjured(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void render(TileConjured tile, float partialTicks, @NotNull PoseStack ms, @NotNull MultiBufferSource buffers, int worldLight, int overlay) {
		BlockState state = tile.getBlockState();
		if(state.getBlock() != ModBlocks.conjured.get()) {
			return;
		}

		int color = Psi.proxy.getColorForColorizer(tile.colorizer);
		int r = PsiRenderHelper.r(color);
		int g = PsiRenderHelper.g(color);
		int b = PsiRenderHelper.b(color);
		VertexConsumer buffer = buffers.getBuffer(LAYER);
		Matrix4f mat = ms.last().pose();
		float time = tile.getLevel() == null ? partialTicks : tile.getLevel().getGameTime() + partialTicks;

		if(state.getValue(BlockConjured.SOLID)) {
			renderMovingEdges(tile, state, buffer, mat, r, g, b, time);
		}
	}

	private static void renderMovingEdges(TileConjured tile, BlockState state, VertexConsumer buffer, Matrix4f mat, int r, int g, int b, float time) {
		boolean[] edges = new boolean[12];
		Arrays.fill(edges, true);

		if(state.getValue(BlockConjured.BLOCK_DOWN)) {
			removeEdges(edges, 0, 1, 2, 3);
		}
		if(state.getValue(BlockConjured.BLOCK_UP)) {
			removeEdges(edges, 4, 5, 6, 7);
		}
		if(state.getValue(BlockConjured.BLOCK_NORTH)) {
			removeEdges(edges, 3, 7, 8, 11);
		}
		if(state.getValue(BlockConjured.BLOCK_SOUTH)) {
			removeEdges(edges, 1, 5, 9, 10);
		}
		if(state.getValue(BlockConjured.BLOCK_EAST)) {
			removeEdges(edges, 2, 6, 10, 11);
		}
		if(state.getValue(BlockConjured.BLOCK_WEST)) {
			removeEdges(edges, 0, 4, 8, 9);
		}

		float salt = tile.getBlockPos().getX() * 0.071F + tile.getBlockPos().getY() * 0.043F + tile.getBlockPos().getZ() * 0.097F;

		renderParticleLine(edges[0], buffer, mat, 0, 0, 0, 0, 0, 1, r, g, b, time, salt + 0);
		renderParticleLine(edges[1], buffer, mat, 0, 0, 1, 1, 0, 1, r, g, b, time, salt + 1);
		renderParticleLine(edges[2], buffer, mat, 1, 0, 0, 1, 0, 1, r, g, b, time, salt + 2);
		renderParticleLine(edges[3], buffer, mat, 0, 0, 0, 1, 0, 0, r, g, b, time, salt + 3);

		renderParticleLine(edges[4], buffer, mat, 0, 1, 0, 0, 1, 1, r, g, b, time, salt + 4);
		renderParticleLine(edges[5], buffer, mat, 0, 1, 1, 1, 1, 1, r, g, b, time, salt + 5);
		renderParticleLine(edges[6], buffer, mat, 1, 1, 0, 1, 1, 1, r, g, b, time, salt + 6);
		renderParticleLine(edges[7], buffer, mat, 0, 1, 0, 1, 1, 0, r, g, b, time, salt + 7);

		renderParticleLine(edges[8], buffer, mat, 0, 0, 0, 0, 1, 0, r, g, b, time, salt + 8);
		renderParticleLine(edges[9], buffer, mat, 0, 0, 1, 0, 1, 1, r, g, b, time, salt + 9);
		renderParticleLine(edges[10], buffer, mat, 1, 0, 1, 1, 1, 1, r, g, b, time, salt + 10);
		renderParticleLine(edges[11], buffer, mat, 1, 0, 0, 1, 1, 0, r, g, b, time, salt + 11);
	}

	private static void renderParticleLine(boolean render, VertexConsumer buffer, Matrix4f mat, float x1, float y1, float z1, float x2, float y2, float z2, int r, int g, int b, float time, float salt) {
		if(!render) {
			return;
		}

		for(int i = 0; i < PARTICLES_PER_EDGE; i++) {
			float progress = frac(time * 0.055F + salt * 0.113F + i / (float) PARTICLES_PER_EDGE);
			renderMovingParticle(buffer, mat, lerp(x1, x2, progress), lerp(y1, y2, progress), lerp(z1, z2, progress), PARTICLE_SIZE, r, g, b, 188);

			float trail = frac(progress - 0.08F);
			renderMovingParticle(buffer, mat, lerp(x1, x2, trail), lerp(y1, y2, trail), lerp(z1, z2, trail), PARTICLE_SIZE * 0.68F, r, g, b, 70);
		}
	}

	private static void renderMovingParticle(VertexConsumer buffer, Matrix4f mat, float x, float y, float z, float size, int r, int g, int b, int a) {
		float half = size / 2F;
		renderBox(buffer, mat, x - half, y - half, z - half, x + half, y + half, z + half, r, g, b, a);
	}

	private static float lerp(float a, float b, float progress) {
		return a + (b - a) * progress;
	}

	private static float frac(float value) {
		return value - (float) Math.floor(value);
	}

	private static void renderBox(VertexConsumer buffer, Matrix4f mat, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int r, int g, int b, int a) {
		quad(buffer, mat, minX, minY, minZ, maxX, minY, minZ, maxX, maxY, minZ, minX, maxY, minZ, r, g, b, a);
		quad(buffer, mat, maxX, minY, maxZ, minX, minY, maxZ, minX, maxY, maxZ, maxX, maxY, maxZ, r, g, b, a);
		quad(buffer, mat, minX, minY, maxZ, minX, minY, minZ, minX, maxY, minZ, minX, maxY, maxZ, r, g, b, a);
		quad(buffer, mat, maxX, minY, minZ, maxX, minY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, r, g, b, a);
		quad(buffer, mat, minX, minY, maxZ, maxX, minY, maxZ, maxX, minY, minZ, minX, minY, minZ, r, g, b, a);
		quad(buffer, mat, minX, maxY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a);
	}

	private static void quad(VertexConsumer buffer, Matrix4f mat, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int r, int g, int b, int a) {
		buffer.addVertex(mat, x1, y1, z1).setColor(r, g, b, a).setLight(FULLBRIGHT);
		buffer.addVertex(mat, x2, y2, z2).setColor(r, g, b, a).setLight(FULLBRIGHT);
		buffer.addVertex(mat, x3, y3, z3).setColor(r, g, b, a).setLight(FULLBRIGHT);
		buffer.addVertex(mat, x4, y4, z4).setColor(r, g, b, a).setLight(FULLBRIGHT);
	}

	private static void removeEdges(boolean[] edges, int... positions) {
		for(int position : positions) {
			edges[position] = false;
		}
	}

	@Override
	public boolean shouldRenderOffScreen(@NotNull TileConjured blockEntity) {
		return true;
	}
}
