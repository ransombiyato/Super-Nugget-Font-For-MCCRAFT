/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.capability;

import net.minecraft.resources.Identifier;

public final class EntityCapability<T, C> {
	private final Identifier id;
	private final Class<T> type;

	private EntityCapability(Identifier id, Class<T> type) {
		this.id = id;
		this.type = type;
	}

	public static <T> EntityCapability<T, Void> createVoid(Identifier id, Class<T> type) {
		return new EntityCapability<>(id, type);
	}

	public Identifier id() {
		return id;
	}

	public Class<T> type() {
		return type;
	}
}
