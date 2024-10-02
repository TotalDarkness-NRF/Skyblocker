package de.hysky.skyblocker.skyblock.mining.eventTracker;

import de.hysky.skyblocker.config.HudConfigScreen;
import de.hysky.skyblocker.config.SkyblockerConfig;
import de.hysky.skyblocker.skyblock.tabhud.widget.MiningEventsWidget;
import de.hysky.skyblocker.skyblock.tabhud.widget.Widget;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class MiningEventsConfigScreen extends HudConfigScreen {

	public MiningEventsConfigScreen() {
		this(null);
	}

	public MiningEventsConfigScreen(Screen parent) {
		super(Text.literal("Mining Events Widget Config"), parent, List.of(MiningEventsWidget.INSTANCE_CFG));
	}

	@SuppressWarnings("SuspiciousNameCombination")
	@Override
	protected List<IntIntMutablePair> getConfigPos(SkyblockerConfig config) {
		return List.of(
				IntIntMutablePair.of(config.mining.miningEventsWidget.widgetX, config.mining.miningEventsWidget.widgetY)
		);
	}

	@Override
	protected void renderWidget(DrawContext context, List<Widget> widgets) {
		MiningEventsHud.render(MiningEventsWidget.INSTANCE_CFG, context, widgets.getFirst().getX(), widgets.getFirst().getY());
	}

	@Override
	protected void savePos(SkyblockerConfig configManager, List<Widget> widgets) {
		configManager.mining.miningEventsWidget.widgetX = widgets.getFirst().getX();
		configManager.mining.miningEventsWidget.widgetY = widgets.getFirst().getY();
	}
}
