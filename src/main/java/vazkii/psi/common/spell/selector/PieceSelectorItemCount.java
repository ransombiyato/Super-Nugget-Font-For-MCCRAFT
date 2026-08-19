/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.common.spell.selector;

import net.minecraft.world.item.ItemStack;

import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceSelector;

public class PieceSelectorItemCount extends PieceSelector {

	public PieceSelectorItemCount(Spell spell) {
		super(spell);
	}

	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		var inventory = context.caster.getInventory();
		ItemStack toCount = inventory.getItem(context.getTargetSlot());
		return java.util.stream.IntStream.range(0, inventory.getContainerSize())
				.mapToObj(inventory::getItem)
				.filter(stack -> ItemStack.isSameItem(stack, toCount))
				.mapToInt(ItemStack::getCount)
				.sum();
	}

	@Override
	public Class<?> getEvaluationType() {
		return Double.class;
	}
}
