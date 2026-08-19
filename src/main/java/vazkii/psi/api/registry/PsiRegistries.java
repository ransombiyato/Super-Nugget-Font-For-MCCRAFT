/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.registry;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import vazkii.psi.api.recipe.condition.ICondition;

public final class PsiRegistries {
	private PsiRegistries() {}

	public static final class Keys {
		public static final ResourceKey<Registry<MapCodec<? extends ICondition>>> CONDITION_CODECS =
				ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("psi", "condition_codecs"));

		private Keys() {}
	}
}
