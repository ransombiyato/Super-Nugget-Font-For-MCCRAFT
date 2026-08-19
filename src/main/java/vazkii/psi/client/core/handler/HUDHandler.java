package vazkii.psi.client.core.handler;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.regex.Pattern;

/**
 * Compatibility bridge for gameplay code that records the remaining block/item count.
 * The previous immediate-mode HUD integration requires a separate 1.21.11 render-state rewrite.
 */
public final class HUDHandler {
	private HUDHandler() {}

	private static ItemStack remainingDisplayStack = ItemStack.EMPTY;
	private static int remainingCount;

	public static void tick() {}

	public static void setRemaining(ItemStack stack, int count) {
		remainingDisplayStack = stack.copy();
		remainingCount = count;
	}

	public static void setRemaining(Player player, ItemStack displayStack, Pattern pattern) {
		int count = 0;
		for(int i = 0; i < player.getInventory().getContainerSize(); i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if(!stack.isEmpty() && (pattern == null ? ItemStack.isSameItem(displayStack, stack) : pattern.matcher(stack.getItem().getDescriptionId()).find())) {
				count += stack.getCount();
			}
		}
		setRemaining(displayStack, count);
	}

	public static ItemStack getRemainingDisplayStack() {
		return remainingDisplayStack;
	}

	public static int getRemainingCount() {
		return remainingCount;
	}
}
