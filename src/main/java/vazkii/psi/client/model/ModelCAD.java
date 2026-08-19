/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ICADAssembly;
import vazkii.psi.common.Psi;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ModelCAD implements BakedModel {

	public static final Identifier DEFAULT_MODEL = Psi.location("item/cad_iron");
	public static final Identifier[] CAD_MODELS = {
			Psi.location("item/cad_iron"),
			Psi.location("item/cad_gold"),
			Psi.location("item/cad_psimetal"),
			Psi.location("item/cad_ivory_psimetal"),
			Psi.location("item/cad_ebony_psimetal"),
			Psi.location("item/cad_creative")
	};

	private final BakedModel original;

	public ModelCAD(BakedModel original) {
		this.original = original;
	}

	/**
	 * @deprecated Use {@link #getQuads(BlockState, Direction, RandomSource,
	 *             vazkii.psi.client.model.PsiModelData,
	 *             net.minecraft.client.renderer.rendertype.RenderType)}
	 */
	@Deprecated
	@Override
	public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource random) {
		return original.getQuads(state, side, random);
	}

	public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @org.jetbrains.annotations.Nullable Direction side, @NotNull RandomSource rand, @NotNull PsiModelData data, @org.jetbrains.annotations.Nullable RenderType renderType) {
		return original.getQuads(state, side, rand);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return original.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return original.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return original.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return original.isCustomRenderer();
	}

	/**
	 * @deprecated Use {@link #getParticleIcon(vazkii.psi.client.model.PsiModelData)}
	 */
	@NotNull
	@Override
	@Deprecated
	public TextureAtlasSprite getParticleIcon() {
		return original.getParticleIcon();
	}

	@NotNull
	public TextureAtlasSprite getParticleIcon(@NotNull PsiModelData data) {
		return original.getParticleIcon();
	}

	@NotNull
	@Override
	public ItemOverrides getOverrides() {
		return original.getOverrides();
	}

	@NotNull
	@Override
	public ItemTransforms getTransforms() {
		return original.getTransforms();
	}

	private static BakedModel getModel(Identifier modelId) {
		return ((FabricBakedModelManager) Minecraft.getInstance().getModelManager()).getModel(modelId);
	}

	private static Identifier getModelForStack(ItemStack stack) {
		if(stack.getItem() instanceof ICAD cad) {
			ItemStack assemblyStack = cad.getComponentInSlot(stack, EnumCADComponent.ASSEMBLY);
			if(assemblyStack.getItem() instanceof ICADAssembly assembly) {
				return assembly.getCADModel(assemblyStack, stack);
			}
		}

		return DEFAULT_MODEL;
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitItemQuads(ItemStack stack, java.util.function.Supplier<RandomSource> randomSupplier, RenderContext context) {
		BakedModel model = getModel(getModelForStack(stack));
		((FabricBakedModel) (model == null ? original : model)).emitItemQuads(stack, randomSupplier, context);
	}

}
