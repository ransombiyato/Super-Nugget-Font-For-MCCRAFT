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

import vazkii.psi.common.core.handler.ContributorSpellCircleHandler;
import vazkii.psi.common.core.helper.PsiColorHelper;

import java.awt.*;
import java.util.Locale;

public class ItemCADColorizerPsi extends ItemCADColorizer {

	public ItemCADColorizerPsi(Properties properties) {
		super(properties);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(ItemStack stack) {
		if(!getContributorName(stack).isEmpty() && ContributorSpellCircleHandler.isContributor(getContributorName(stack).toLowerCase(Locale.ROOT))) {
			return PsiColorHelper.slideColor(ContributorSpellCircleHandler.getColors(getContributorName(stack).toLowerCase(Locale.ROOT)), 0.0125F);
		}
		float time = PsiColorHelper.animationTime();
		float w = (float) (Math.sin(time * 0.4) * 0.5 + 0.5) * 0.1F;
		float r = (float) (Math.sin(time * 0.1) * 0.5 + 0.5) * 0.5F + 0.25F + w;
		float g = 0.5F + w;
		float b = 1F;

		return new Color((int) (r * 255), (int) (g * 255), (int) (b * 255)).getRGB();
	}
}
