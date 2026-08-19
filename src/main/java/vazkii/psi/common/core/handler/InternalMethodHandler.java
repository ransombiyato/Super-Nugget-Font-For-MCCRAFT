/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.core.handler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import vazkii.psi.api.internal.IInternalMethodHandler;
import vazkii.psi.api.internal.IPlayerData;
import vazkii.psi.api.spell.*;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.lib.LibResources;
import vazkii.psi.common.spell.SpellCache;
import vazkii.psi.common.spell.SpellCompiler;

import java.util.List;

public final class InternalMethodHandler implements IInternalMethodHandler {

	@Override
	public IPlayerData getDataForPlayer(Player player) {
		return PlayerDataHandler.get(player);
	}

	@Override
	public Identifier getProgrammerTexture() {
		return Identifier.parse(LibResources.GUI_PROGRAMMER);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public RenderType getProgrammerLayer() {
		return RenderTypes.entityTranslucent(getProgrammerTexture());
	}

	@Override
	public ISpellCompiler getCompiler() {
		return new SpellCompiler();
	}

	@Override
	public ISpellCache getSpellCache() {
		return SpellCache.instance;
	}

	@Override
	public void delayContext(SpellContext context) {
		if(!context.caster.level().isClientSide()) {
			PlayerDataHandler.delayedContexts.add(context);
		}
	}

	@Override
	public void setCrashData(CompiledSpell spell, SpellPiece piece) {
		CrashReportHandler.setSpell(spell, piece);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void renderTooltip(GuiGraphics graphics, int x, int y, List<Component> tooltipData, int color, int color2, int width, int height) {
		// The legacy immediate tooltip renderer must be translated to the deferred
		// ClientTooltipComponent pipeline before visual rendering can resume.
	}

	@Override
	public ItemStack createDefaultCAD(List<ItemStack> components) {
		return ItemCAD.makeCAD(components);
	}

	@Override
	public ItemStack createCAD(ItemStack base, List<ItemStack> components) {
		return ItemCAD.makeCAD(base, components);
	}
}
