/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.core.capability;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.capability.EntityCapability;
import vazkii.psi.api.capability.ICapabilityProvider;
import vazkii.psi.api.exosuit.PsiArmorEvent;
import vazkii.psi.api.spell.detonator.IDetonationHandler;

public record CapabilityTriggerSensor(
		Player player) implements IDetonationHandler, ICapabilityProvider<EntityCapability<?, Void>, Void, CapabilityTriggerSensor> {

	public static final String TRIGGER_TICK = PsiAPI.MOD_ID + ":LastTriggeredDetonation";
	private static final java.util.Map<java.util.UUID, Long> LAST_TRIGGERED = new java.util.HashMap<>();

	@Nullable
	@Override
	public CapabilityTriggerSensor getCapability(@NotNull EntityCapability<?, Void> capability, @Nullable Void facing) {
		if(capability == PsiAPI.DETONATION_HANDLER_CAPABILITY) {
			return this;
		}
		return null;
	}

	@Override
	public void detonate() {
		long detonated = LAST_TRIGGERED.getOrDefault(player.getUUID(), Long.MIN_VALUE);
		long worldTime = player.level().getGameTime();

		if(detonated != worldTime) {
			LAST_TRIGGERED.put(player.getUUID(), worldTime);

			PsiArmorEvent.post(new PsiArmorEvent(player, PsiArmorEvent.DETONATE));
		}
	}

	@Override
	public Vec3 objectLocus() {
		return player.position();
	}
}
