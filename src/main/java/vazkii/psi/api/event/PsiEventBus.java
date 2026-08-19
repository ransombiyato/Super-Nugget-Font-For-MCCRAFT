/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in GitHub:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.api.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class PsiEventBus {
	private static final Map<Class<?>, List<Consumer<? super PsiEvent>>> LISTENERS = new ConcurrentHashMap<>();

	private PsiEventBus() {}

	@SuppressWarnings("unchecked")
	public static <T extends PsiEvent> Subscription register(Class<T> eventType, Consumer<? super T> listener) {
		Consumer<? super PsiEvent> castListener = (Consumer<? super PsiEvent>) listener;
		LISTENERS.computeIfAbsent(eventType, ignored -> new CopyOnWriteArrayList<>()).add(castListener);
		return () -> LISTENERS.getOrDefault(eventType, List.of()).remove(castListener);
	}

	public static <T extends PsiEvent> T post(T event) {
		LISTENERS.forEach((eventType, listeners) -> {
			if(eventType.isInstance(event)) {
				listeners.forEach(listener -> listener.accept(event));
			}
		});
		return event;
	}

	@FunctionalInterface
	public interface Subscription extends AutoCloseable {
		@Override
		void close();
	}
}
