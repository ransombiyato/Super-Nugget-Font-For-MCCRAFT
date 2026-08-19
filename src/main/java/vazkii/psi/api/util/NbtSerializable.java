/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.util;

import net.minecraft.core.HolderLookup;

public interface NbtSerializable<T> {
	T serializeNBT(HolderLookup.Provider provider);

	void deserializeNBT(HolderLookup.Provider provider, T nbt);
}
