/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.spell.selector;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.piece.PieceSelector;

public class PieceSelectorTickTime extends PieceSelector {

	public PieceSelectorTickTime(Spell spell) {
		super(spell);
	}

	public static double getMspt(SpellContext context) {
		if(context.focalPoint == null || context.focalPoint.level().isClientSide()) {
			return 0;
		}

		return 50;
	}

	private static long mean(long[] values) {
		long sum = 0L;
		for(long val : values) {
			sum = sum + val;
		}

		return sum / values.length;
	}

	@Override
	public Class<?> getEvaluationType() {
		return Double.class;
	}

	@Override
	public Object execute(SpellContext context) {
		return getMspt(context);
	}
}
