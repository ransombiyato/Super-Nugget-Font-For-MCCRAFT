/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import vazkii.psi.common.Psi;
import vazkii.psi.common.lib.LibEntityNames;

import static net.minecraft.world.entity.MobCategory.MISC;

public final class ModEntities {
	public static EntityType<EntitySpellProjectile> spellProjectile;
	public static EntityType<EntitySpellCircle> spellCircle;
	public static EntityType<EntitySpellGrenade> spellGrenade;
	public static EntityType<EntitySpellCharge> spellCharge;
	public static EntityType<EntitySpellMine> spellMine;

	private static <T extends Entity> EntityType<T> build(Identifier id, EntityType.Builder<T> builder) {
		return builder.build(ResourceKey.create(Registries.ENTITY_TYPE, id));
	}

	public static void register() {
		if(spellProjectile != null) {
			return;
		}

		Identifier spellProjectileId = Psi.location(LibEntityNames.SPELL_PROJECTILE);
		spellProjectile = Registry.register(BuiltInRegistries.ENTITY_TYPE, spellProjectileId,
				build(spellProjectileId, EntityType.Builder.of((EntityType.EntityFactory<EntitySpellProjectile>) EntitySpellProjectile::new, MISC)
						.clientTrackingRange(256)
						.updateInterval(10)
						.alwaysUpdateVelocity(true)
						.sized(0.0F, 0.0F)));
		Identifier spellCircleId = Psi.location(LibEntityNames.SPELL_CIRCLE);
		spellCircle = Registry.register(BuiltInRegistries.ENTITY_TYPE, spellCircleId,
				build(spellCircleId, EntityType.Builder.of(EntitySpellCircle::new, MISC)
						.clientTrackingRange(256)
						.updateInterval(10)
						.alwaysUpdateVelocity(false)
						.sized(3.0f, 0.3f)
						.fireImmune()));
		Identifier spellGrenadeId = Psi.location(LibEntityNames.SPELL_GRENADE);
		spellGrenade = Registry.register(BuiltInRegistries.ENTITY_TYPE, spellGrenadeId,
				build(spellGrenadeId, EntityType.Builder.of((EntityType.EntityFactory<EntitySpellGrenade>) EntitySpellGrenade::new, MISC)
						.clientTrackingRange(256)
						.updateInterval(10)
						.alwaysUpdateVelocity(true)
						.sized(0.0F, 0.0F)));
		Identifier spellChargeId = Psi.location(LibEntityNames.SPELL_CHARGE);
		spellCharge = Registry.register(BuiltInRegistries.ENTITY_TYPE, spellChargeId,
				build(spellChargeId, EntityType.Builder.of((EntityType.EntityFactory<EntitySpellCharge>) EntitySpellCharge::new, MISC)
						.clientTrackingRange(256)
						.updateInterval(10)
						.alwaysUpdateVelocity(true)
						.sized(0.0F, 0.0F)));
		Identifier spellMineId = Psi.location(LibEntityNames.SPELL_MINE);
		spellMine = Registry.register(BuiltInRegistries.ENTITY_TYPE, spellMineId,
				build(spellMineId, EntityType.Builder.of((EntityType.EntityFactory<EntitySpellMine>) EntitySpellMine::new, MISC)
						.clientTrackingRange(256)
						.updateInterval(10)
						.alwaysUpdateVelocity(true)
						.sized(0.0F, 0.0F)));
	}
}
