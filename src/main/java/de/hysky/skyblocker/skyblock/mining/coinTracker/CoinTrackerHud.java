package de.hysky.skyblocker.skyblock.mining.coinTracker;

import de.hysky.skyblocker.annotations.Init;
import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.events.HudRenderEvents;
import de.hysky.skyblocker.skyblock.tabhud.widget.CoinTrackerWidget;
import de.hysky.skyblocker.utils.Utils;
import de.hysky.skyblocker.utils.scheduler.Scheduler;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.gui.DrawContext;

import static de.hysky.skyblocker.skyblock.mining.HOTMSolver.CLIENT;

public class CoinTrackerHud {

	@Init
	public static void init() {
		de.hysky.skyblocker.skyblock.mining.coinTracker.CoinTracker.init();
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("skyblocker")
				.then(ClientCommandManager.literal("hud")
						.then(ClientCommandManager.literal("coinTrackerHud")
								.executes(Scheduler.queueOpenScreenCommand(de.hysky.skyblocker.skyblock.mining.coinTracker.CoinTrackerConfigScreen::new))
						)
				)
		));

		HudRenderEvents.AFTER_MAIN_HUD.register((context, tickCounter) ->
				render(CoinTrackerWidget.INSTANCE, context,
						SkyblockerConfigManager.get().mining.coinTrackerHud.hudX,
						SkyblockerConfigManager.get().mining.coinTrackerHud.hudY)
		);
	}

	protected static void render(CoinTrackerWidget widget, DrawContext context, int hudX, int hudY) {
		// TODO make coinTracker settings
		if (!SkyblockerConfigManager.get().mining.coinTrackerHud.enableCoinTrackerHud
				|| CLIENT.options.playerListKey.isPressed() || CLIENT.player == null
				|| !Utils.isOnSkyblock()) return;
		widget.update();
		widget.setX(hudX);
		widget.setY(hudY);
		widget.render(context, SkyblockerConfigManager.get().uiAndVisuals.tabHud.enableHudBackground);
	}
}
