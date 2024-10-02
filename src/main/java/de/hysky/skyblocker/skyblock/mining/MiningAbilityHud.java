package de.hysky.skyblocker.skyblock.mining;

import de.hysky.skyblocker.annotations.Init;
import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.events.HudRenderEvents;
import de.hysky.skyblocker.skyblock.tabhud.widget.hud.MiningAbilityWidget;
import de.hysky.skyblocker.utils.Utils;
import de.hysky.skyblocker.utils.scheduler.Scheduler;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.gui.DrawContext;

import static de.hysky.skyblocker.skyblock.mining.HOTMSolver.CLIENT;

public class MiningAbilityHud {

	@Init
	public static void init() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("skyblocker")
				.then(ClientCommandManager.literal("hud")
						.then(ClientCommandManager.literal("miningAbility")
								.executes(Scheduler.queueOpenScreenCommand(MiningAbilityConfigScreen::new))
						)
				)
		));

		HudRenderEvents.AFTER_MAIN_HUD.register((context, tickCounter) ->
				render(MiningAbilityWidget.INSTANCE, context,
						SkyblockerConfigManager.get().mining.miningAbilityHud.abilityX,
						SkyblockerConfigManager.get().mining.miningAbilityHud.abilityY));
	}

	protected static void render(MiningAbilityWidget hma, DrawContext context, int abilityHudX, int abilityHudY) {
		if (!SkyblockerConfigManager.get().mining.miningAbilityHud.enableAbilityHud
				|| CLIENT.options.playerListKey.isPressed() || CLIENT.player == null
				|| !canDisplayInLocation()) return;
		hma.update();
		hma.setX(abilityHudX);
		hma.setY(abilityHudY);
		hma.render(context, SkyblockerConfigManager.get().uiAndVisuals.tabHud.enableHudBackground);
	}

	public static boolean canDisplayInLocation() {
		return Utils.isOnSkyblock() &&
				(SkyblockerConfigManager.get().mining.miningAbilityHud.alwaysDisplay ||
						Utils.isInDwarvenMines() || Utils.isInCrystalHollows());
	}
}
