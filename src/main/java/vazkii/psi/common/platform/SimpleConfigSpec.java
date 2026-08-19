/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.platform;

import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;

public class SimpleConfigSpec {
	public static class Builder {
		public Builder comment(String comment) {
			return this;
		}

		public BooleanValue define(String path, boolean defaultValue) {
			return new BooleanValue(defaultValue);
		}

		public IntValue defineInRange(String path, int defaultValue, int min, int max) {
			return new IntValue(defaultValue);
		}

		public <T> Pair<T, SimpleConfigSpec> configure(Function<Builder, T> factory) {
			return Pair.of(factory.apply(this), new SimpleConfigSpec());
		}
	}

	public static class BooleanValue {
		private final boolean value;

		public BooleanValue(boolean value) {
			this.value = value;
		}

		public boolean get() {
			return value;
		}
	}

	public static class IntValue {
		private final int value;

		public IntValue(int value) {
			this.value = value;
		}

		public int get() {
			return value;
		}
	}
}
