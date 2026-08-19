/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import vazkii.psi.api.registry.PsiRegistryBuilder;

@Environment(EnvType.CLIENT)
public class ClientPsiAPI {
	public static final ResourceKey<Registry<Material>> SPELL_PIECE_MATERIAL = ResourceKey.createRegistryKey(PsiAPI.location("spell_piece_material_key"));
	public static final Registry<Material> SPELL_PIECE_MATERIAL_REGISTRY = (new PsiRegistryBuilder<>(SPELL_PIECE_MATERIAL)).create();
}
