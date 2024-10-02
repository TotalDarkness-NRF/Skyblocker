package de.hysky.skyblocker.skyblock.mining.coinTracker;

import de.hysky.skyblocker.config.HudConfigScreen;
import de.hysky.skyblocker.config.SkyblockerConfig;
import de.hysky.skyblocker.skyblock.tabhud.widget.CoinTrackerWidget;
import de.hysky.skyblocker.skyblock.tabhud.widget.Widget;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class CoinTrackerConfigScreen extends HudConfigScreen {

	public CoinTrackerConfigScreen() {
		this(null);
	}

	public CoinTrackerConfigScreen(Screen parent) {
		super(Text.literal("Coin Tracker HUD Config"), parent, List.of(CoinTrackerWidget.INSTANCE_CFG));
	}

	@SuppressWarnings("SuspiciousNameCombination")
	@Override
	protected List<IntIntMutablePair> getConfigPos(SkyblockerConfig config) {
		return List.of(
				IntIntMutablePair.of(config.mining.coinTrackerHud.hudX, config.mining.coinTrackerHud.hudY)
		);
	}

	@Override
	protected void renderWidget(DrawContext context, List<Widget> widgets) {
		CoinTrackerHud.render(CoinTrackerWidget.INSTANCE_CFG, context, widgets.getFirst().getX(), widgets.getFirst().getY());
	}

	@Override
	protected void savePos(SkyblockerConfig configManager, List<Widget> widgets) {
		configManager.mining.coinTrackerHud.hudX = widgets.getFirst().getX();
		configManager.mining.coinTrackerHud.hudY = widgets.getFirst().getY();
	}
}
