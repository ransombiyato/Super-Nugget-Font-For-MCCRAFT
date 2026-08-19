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
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public final class DeferredHolder<R, T extends R> implements Supplier<T> {
	private final ResourceKey<? extends Registry<R>> registryKey;
	private final Identifier id;
	private final T value;

	DeferredHolder(ResourceKey<? extends Registry<R>> registryKey, Identifier id, T value) {
		this.registryKey = registryKey;
		this.id = id;
		this.value = value;
	}

	@Override
	public T get() {
		return value;
	}

	public Identifier getId() {
		return id;
	}

	public ResourceKey<R> getKey() {
		return ResourceKey.create(registryKey, id);
	}
}
