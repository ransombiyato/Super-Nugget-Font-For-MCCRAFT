/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.cad;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import vazkii.psi.api.PsiAPI;

import java.util.List;

public interface ICADAssembly {

	default ItemStack createCADStack(ItemStack stack, List<ItemStack> allComponents) {
		return PsiAPI.internalHandler.createDefaultCAD(allComponents);
	}

	/**
	 * @return Path to a model json file, e.g. <code>psi:item/cad_iron</code>
	 */
	@Environment(EnvType.CLIENT)
	Identifier getCADModel(ItemStack stack, ItemStack cad);

	@Environment(EnvType.CLIENT)
	Identifier getCadTexture(ItemStack stack, ItemStack cad);

}
