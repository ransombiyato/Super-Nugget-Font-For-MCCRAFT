/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.Map;

public class PsiRegistryBuilder<T> {
	private static final Map<ResourceKey<? extends Registry<?>>, Registry<?>> REGISTRIES = new HashMap<>();

	private final ResourceKey<Registry<T>> key;

	public PsiRegistryBuilder(ResourceKey<Registry<T>> key) {
		this.key = key;
	}

	@SuppressWarnings("unchecked")
	static <T> Registry<T> getOrCreate(ResourceKey<? extends Registry<T>> key) {
		Registry<T> builtin = (Registry<T>) BuiltInRegistries.REGISTRY.getValue(key.identifier());
		if(builtin != null) {
			return builtin;
		}

		return (Registry<T>) REGISTRIES.computeIfAbsent((ResourceKey<? extends Registry<?>>) key, registryKey -> net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder.createSimple((ResourceKey) registryKey).buildAndRegister());
	}

	public Registry<T> create() {
		Registry<T> registry = getOrCreate(key);
		REGISTRIES.put(key, registry);
		return registry;
	}
}
