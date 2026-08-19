/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.recipe.condition;

import com.mojang.serialization.MapCodec;

public interface ICondition {
	boolean test(IContext context);

	MapCodec<? extends ICondition> codec();

	interface IContext {
	}
}
