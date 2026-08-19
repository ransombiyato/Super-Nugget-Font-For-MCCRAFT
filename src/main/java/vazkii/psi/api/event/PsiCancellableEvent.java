/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.event;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public interface PsiCancellableEvent {
	Map<PsiCancellableEvent, Boolean> CANCELED = Collections.synchronizedMap(new WeakHashMap<>());

	default boolean isCanceled() {
		return CANCELED.getOrDefault(this, false);
	}

	default void setCanceled(boolean canceled) {
		if(canceled) {
			CANCELED.put(this, true);
		} else {
			CANCELED.remove(this);
		}
	}
}
