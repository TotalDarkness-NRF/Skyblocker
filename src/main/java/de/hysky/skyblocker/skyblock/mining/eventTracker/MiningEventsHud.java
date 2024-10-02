package de.hysky.skyblocker.skyblock.mining.eventTracker;

import de.hysky.skyblocker.annotations.Init;
import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.events.HudRenderEvents;
import de.hysky.skyblocker.skyblock.tabhud.widget.MiningEventsWidget;
import de.hysky.skyblocker.utils.Utils;
import de.hysky.skyblocker.utils.scheduler.Scheduler;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.gui.DrawContext;

import static de.hysky.skyblocker.skyblock.mining.HOTMSolver.CLIENT;

public class MiningEventsHud {

	@Init
	public static void init() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("skyblocker")
				.then(ClientCommandManager.literal("hud")
						.then(ClientCommandManager.literal("miningEvents")
								.executes(Scheduler.queueOpenScreenCommand(MiningEventsConfigScreen::new))
						)
				)
		));

		HudRenderEvents.AFTER_MAIN_HUD.register((context, tickCounter) ->
				render(MiningEventsWidget.INSTANCE, context,
						SkyblockerConfigManager.get().mining.miningEventsWidget.widgetX,
						SkyblockerConfigManager.get().mining.miningEventsWidget.widgetY));
	}

	protected static void render(MiningEventsWidget widget, DrawContext context, int widgetX, int widgetY) {
		if (!SkyblockerConfigManager.get().mining.miningEventsWidget.enableEventsWidget
				|| CLIENT.options.playerListKey.isPressed() || CLIENT.player == null
				|| !canDisplayInLocation()) return;
		widget.update();
		widget.setX(widgetX);
		widget.setY(widgetY);
		widget.render(context, SkyblockerConfigManager.get().uiAndVisuals.tabHud.enableHudBackground);
	}

	public static boolean canDisplayInLocation() {
		return Utils.isOnSkyblock() &&
				(SkyblockerConfigManager.get().mining.miningEventsWidget.alwaysDisplay ||
						Utils.isInDwarvenMines() || Utils.isInCrystalHollows());
	}
}
