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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import vazkii.psi.api.spell.*;

import java.util.List;

/**
 * This is a dummy class. You'll never interact with it, it's just here so
 * in case something goes really wrong the field isn't null.
 */
public final class DummyMethodHandler implements IInternalMethodHandler {

	@Override
	public IPlayerData getDataForPlayer(Player player) {
		return new DummyPlayerData();
	}

	@Override
	public Identifier getProgrammerTexture() {
		return Identifier.withDefaultNamespace("");
	}

	@Override
	@Environment(EnvType.CLIENT)
	public RenderType getProgrammerLayer() {
		return null;
	}

	@Override
	public ISpellCompiler getCompiler() {
		return null;
	}

	@Override
	public ISpellCache getSpellCache() {
		return null;
	}

	@Override
	public void delayContext(SpellContext context) {
		// NO-OP
	}

	@Override
	public void setCrashData(CompiledSpell spell, SpellPiece piece) {
		// NO-OP
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void renderTooltip(GuiGraphics graphics, int x, int y, List<Component> tooltipData, int color, int color2, int width, int height) {
		// NO-OP
	}

	@Override
	public ItemStack createDefaultCAD(List<ItemStack> components) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack createCAD(ItemStack base, List<ItemStack> components) {
		return ItemStack.EMPTY;
	}
}
