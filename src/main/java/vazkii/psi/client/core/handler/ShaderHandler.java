/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.client.core.handler;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.renderer.ShaderInstance;

import vazkii.psi.common.Psi;
import vazkii.psi.common.lib.LibResources;

public final class ShaderHandler {

	private static ShaderInstance psiBarShader;

	public static void registerFabricShaders() {
		CoreShaderRegistrationCallback.EVENT.register(context -> context.register(
				Psi.location(LibResources.SHADER_PSI_BAR),
				DefaultVertexFormat.POSITION_TEX_COLOR,
				shader -> psiBarShader = shader
		));
	}

	public static ShaderInstance getPsiBarShader() {
		return psiBarShader;
	}

}
