/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.platform;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.Optional;

public final class FabricModLookup {
	private FabricModLookup() {}

	public static boolean isLoaded(String modid) {
		return FabricLoader.getInstance().isModLoaded(modid);
	}

	public static Optional<ModInfo> getMod(String modid) {
		return FabricLoader.getInstance().getModContainer(modid).map(FabricModLookup::toInfo);
	}

	public static Optional<String> getVersion(String modid) {
		return getMod(modid).map(ModInfo::version);
	}

	private static ModInfo toInfo(ModContainer container) {
		return new ModInfo(
				container.getMetadata().getId(),
				container.getMetadata().getName(),
				container.getMetadata().getVersion().toString());
	}

	public record ModInfo(String id, String displayName, String version) {
	}
}
