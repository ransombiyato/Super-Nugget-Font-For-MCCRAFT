/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.client.core.proxy;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.client.fx.*;
import vazkii.psi.common.block.tile.TileProgrammer;
import vazkii.psi.common.core.proxy.IProxy;

@Environment(EnvType.CLIENT)
public class ClientProxy implements IProxy {

	@Override
	public boolean hasAdvancement(Identifier advancementLocation, Player playerEntity) {
		if(playerEntity instanceof LocalPlayer clientPlayerEntity) {
			return clientPlayerEntity.connection.getAdvancements().get(advancementLocation) != null;
		}

		return false;
	}

	@Override
	public void addParticleForce(Level world, ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		world.addParticle(particleData, true, true, x, y, z, xSpeed, ySpeed, zSpeed);
	}

	@Override
	public Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	@Override
	public Level getClientWorld() {
		return Minecraft.getInstance().level;
	}

	@Override
	public int getColorForCAD(ItemStack cadStack) {
		if(cadStack.isEmpty() || !(cadStack.getItem() instanceof ICAD icad)) {
			return ICADColorizer.DEFAULT_SPELL_COLOR;
		}
		return icad.getSpellColor(cadStack);
	}

	@Override
	public int getColorForColorizer(ItemStack colorizer) {
		if(colorizer.isEmpty() || !(colorizer.getItem() instanceof ICADColorizer icc)) {
			return ICADColorizer.DEFAULT_SPELL_COLOR;
		}
		return icc.getColor(colorizer);
	}

	@Override
	public void sparkleFX(Level world, double x, double y, double z, float r, float g, float b, float motionX, float motionY, float motionZ, float size, int m) {
		if(m == 0) {
			return;
		}

		SparkleParticleData data = new SparkleParticleData(size, r, g, b, m, motionX, motionY, motionZ);
		addParticleForce(world, data, x, y, z, motionX, motionY, motionZ);
	}

	@Override
	public void sparkleFX(double x, double y, double z, float r, float g, float b, float motionX, float motionY, float motionZ, float size, int m) {
		sparkleFX(Minecraft.getInstance().level, x, y, z, r, g, b, motionX, motionY, motionZ, size, m);
	}

	@Override
	public void wispFX(Level world, double x, double y, double z, float r, float g, float b, float size, float motionX, float motionY, float motionZ, float maxAgeMul) {
		if(maxAgeMul == 0) {
			return;
		}

		WispParticleData data = new WispParticleData(size, r, g, b, maxAgeMul);
		addParticleForce(world, data, x, y, z, motionX, motionY, motionZ);
	}

	@Override
	public void wispFX(double x, double y, double z, float r, float g, float b, float size, float motionX, float motionY, float motionZ, float maxAgeMul) {
		wispFX(Minecraft.getInstance().level, x, y, z, r, g, b, size, motionX, motionY, motionZ, maxAgeMul);
	}

	@Override
	public void openProgrammerGUI(TileProgrammer programmer) {
		// The programmer screen is being rebuilt for the 1.21.11 item-model/render-state pipeline.
		// Do not attempt to instantiate the removed immediate-mode screen here.
	}

	@Override
	public void openFlashRingGUI(ItemStack stack) {
		// The flash-ring screen depends on the same legacy programmer widget API.
	}
}
