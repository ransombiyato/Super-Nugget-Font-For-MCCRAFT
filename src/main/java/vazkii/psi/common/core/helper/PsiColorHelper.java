/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.core.helper;

import net.minecraft.util.Mth;

public final class PsiColorHelper {

	private PsiColorHelper() {}

	public static float animationTime() {
		return System.currentTimeMillis() / 50F;
	}

	public static int slideColor(int[] color, float speed) {
		int n = color.length;
		double t = (animationTime() * speed * n / Math.PI) % n;
		int phase = (int) t;
		double dt = t - phase;
		if(dt == 0) {
			return color[phase];
		}
		int nextPhase = (phase + 1) % n;
		return slideColorTime(color[phase], color[nextPhase], (float) (dt * Math.PI));
	}

	public static int pulseColor(int source, float speed, int magnitude) {
		return pulseColor(source, 1F, speed, magnitude);
	}

	public static int pulseColor(int source, float multiplier, float speed, int magnitude) {
		int add = (int) (Mth.sin(animationTime() * speed) * magnitude);
		int red = (0x00FF0000 & source) >> 16;
		int green = (0x0000FF00 & source) >> 8;
		int blue = 0x000000FF & source;
		int addedRed = Mth.clamp((int) (multiplier * (red + add)), 0, 255);
		int addedGreen = Mth.clamp((int) (multiplier * (green + add)), 0, 255);
		int addedBlue = Mth.clamp((int) (multiplier * (blue + add)), 0, 255);
		return 0xFF000000 | (addedRed << 16) | (addedGreen << 8) | addedBlue;
	}

	public static int slideColorTime(int color, int secondColor, float t) {
		float shift = (1 - Mth.cos(t)) / 2;
		if(shift == 0) {
			return color;
		} else if(shift == 1) {
			return secondColor;
		}

		int redA = (0x00FF0000 & color) >> 16;
		int greenA = (0x0000FF00 & color) >> 8;
		int blueA = 0x000000FF & color;
		int redB = (0x00FF0000 & secondColor) >> 16;
		int greenB = (0x0000FF00 & secondColor) >> 8;
		int blueB = 0x000000FF & secondColor;

		int newRed = (int) (redA * (1 - shift) + redB * shift);
		int newGreen = (int) (greenA * (1 - shift) + greenB * shift);
		int newBlue = (int) (blueA * (1 - shift) + blueB * shift);
		return 0xFF000000 | (newRed << 16) | (newGreen << 8) | newBlue;
	}
}
