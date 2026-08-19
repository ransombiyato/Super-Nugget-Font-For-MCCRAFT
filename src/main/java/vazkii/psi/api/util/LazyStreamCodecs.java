/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.util;

import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

public final class LazyStreamCodecs {
	private LazyStreamCodecs() {}

	public static <B, V> StreamCodec<B, V> lazy(Supplier<StreamCodec<B, V>> supplier) {
		return new StreamCodec<>() {
			@Override
			public V decode(B buffer) {
				return supplier.get().decode(buffer);
			}

			@Override
			public void encode(B buffer, V value) {
				supplier.get().encode(buffer, value);
			}
		};
	}
}
