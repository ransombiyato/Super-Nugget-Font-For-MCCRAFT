/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.item.component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.ItemStack;

import vazkii.psi.common.core.helper.PsiColorHelper;

import java.awt.*;

public class ItemCADColorizerRainbow extends ItemCADColorizer {
	public ItemCADColorizerRainbow(Properties properties) {
		super(properties);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(ItemStack stack) {
		float time = PsiColorHelper.animationTime();
		return Color.HSBtoRGB(time * 0.005F, 1F, 1F);
	}
}
