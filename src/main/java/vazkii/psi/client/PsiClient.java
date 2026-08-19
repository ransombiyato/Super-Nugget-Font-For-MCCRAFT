package vazkii.psi.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

import vazkii.psi.client.core.handler.ClientTickHandler;
import vazkii.psi.client.core.handler.KeybindHandler;
import vazkii.psi.client.core.proxy.ClientProxy;
import vazkii.psi.client.fx.FXSparkle;
import vazkii.psi.client.fx.FXWisp;
import vazkii.psi.client.fx.ModParticles;
import vazkii.psi.client.network.ClientNetworkHelper;
import vazkii.psi.common.Psi;

/**
 * Client registration retained for gameplay, networking, particles, menus, controls, and item tinting.
 * The former baked-model, immediate-mode HUD/shader, and bespoke render-state hooks are intentionally
 * omitted until they are rebuilt against Minecraft 1.21.11's ItemModel and submit-node renderer APIs.
 */
public final class PsiClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Psi.proxy = new ClientProxy();
		registerParticleProviders();
		registerClientEvents();
		ClientNetworkHelper.registerReceivers();
	}

	private static void registerClientEvents() {
		KeyBindingHelper.registerKeyBinding(KeybindHandler.keybind);
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			ClientTickHandler.tickClient(client);
		});
	}

	private static void registerParticleProviders() {
		ParticleFactoryRegistry.getInstance().register(ModParticles.WISP.get(), FXWisp.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.SPARKLE.get(), FXSparkle.Factory::new);
	}

}
