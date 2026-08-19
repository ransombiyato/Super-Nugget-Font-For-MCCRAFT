/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.core.handler;

import org.apache.commons.lang3.tuple.Pair;

import vazkii.psi.common.platform.SimpleConfigSpec;

public class ConfigHandler {

	public static final Client CLIENT;
	public static final SimpleConfigSpec CLIENT_SPEC;
	public static final Common COMMON;
	public static final SimpleConfigSpec COMMON_SPEC;

	static {
		final Pair<Client, SimpleConfigSpec> specPair = new SimpleConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	static {
		final Pair<Common, SimpleConfigSpec> specPair = new SimpleConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	public static class Client {

		public final SimpleConfigSpec.BooleanValue useShaders;
		public final SimpleConfigSpec.BooleanValue psiBarOnRight;
		public final SimpleConfigSpec.BooleanValue contextSensitiveBar;
		public final SimpleConfigSpec.BooleanValue pauseGameInProgrammer;
		public final SimpleConfigSpec.IntValue maxPsiBarScale;
		public final SimpleConfigSpec.BooleanValue changeGridCoordinatesToLetterNumber;

		public Client(SimpleConfigSpec.Builder builder) {
			useShaders = builder.comment("Controls whether Psi's shaders are used. If you're using the GLSL Shaders mod and are having graphical troubles with Psi stuff, you may want to turn this off.")
					.define("client.useShaders", true);

			psiBarOnRight = builder.comment("Controls whether the Psi Bar should be rendered on the right of the screen or not.")
					.define("client.psiBarOnRight", true);

			contextSensitiveBar = builder.comment("Controls whether the Psi Bar should be hidden if it's full and the player is holding an item that uses Psi.")
					.define("client.contextSensitiveBar", true);

			maxPsiBarScale = builder.comment("The maximum scale your Psi bar can be. This prevents it from being too large on a bigger GUI scale. This is maximum amount of \\\"on screen pixels\\\" each actual pixel can take.")
					.defineInRange("client.maxPsiBarScale", 3, 1, 5);

			pauseGameInProgrammer = builder.comment("Controls whether the Spell Programmer screen will pause the game in singleplayer.")
					.define("client.pauseGameInProgrammer", true);

			changeGridCoordinatesToLetterNumber = builder.comment("Controls whether or not the Programmer will display the coordinates as a pair of two numbers or as a letter and a number")
					.define("client.changeGridCoordinatesToLetterNumber", false);
		}

	}

	public static class Common {

		public final SimpleConfigSpec.BooleanValue magiPsiClientSide;
		public final SimpleConfigSpec.IntValue spellCacheSize;
		public final SimpleConfigSpec.IntValue cadHarvestLevel;

		public Common(SimpleConfigSpec.Builder builder) {

			magiPsiClientSide = builder.comment("Set this to true to disable all server side features from Magical Psi, to allow you to use it purely as a client side mod")
					.define("common.magiPsiClientSide", false);

			spellCacheSize = builder.comment("How many compiled spells should be kept in a cache. Probably best not to mess with it if you don't know what you're doing.")
					.defineInRange("common.spellCacheSize", 200, 0, Integer.MAX_VALUE);

			cadHarvestLevel = builder.comment("The harvest level of a CAD for the purposes of block breaking spells. Defaults to 3 (diamond level)")
					.defineInRange("common.cadHarvestLevel", 3, 0, 255);

		}
	}

}
