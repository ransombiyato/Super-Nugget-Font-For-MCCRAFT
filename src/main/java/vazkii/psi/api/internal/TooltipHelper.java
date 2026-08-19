/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.internal;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class TooltipHelper {

	@Environment(EnvType.CLIENT)
	public static void tooltipIfShift(List<Component> tooltip, Runnable r) {
		if(Minecraft.getInstance().hasShiftDown()) {
			r.run();
		} else {
			tooltip.add(Component.translatable("psimisc.shift_for_info"));
		}
	}

	@Environment(EnvType.CLIENT)
	public static void tooltipIfCtrl(List<Component> tooltip, Runnable r) {
		if(Minecraft.getInstance().hasControlDown()) {
			r.run();
		} else {
			tooltip.add(Component.translatable("psimisc.ctrl_for_stats"));
		}
	}

}
