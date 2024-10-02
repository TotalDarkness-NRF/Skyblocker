package de.hysky.skyblocker.skyblock.mining;

import de.hysky.skyblocker.config.HudConfigScreen;
import de.hysky.skyblocker.config.SkyblockerConfig;
import de.hysky.skyblocker.skyblock.tabhud.widget.Widget;
import de.hysky.skyblocker.skyblock.tabhud.widget.hud.MiningAbilityWidget;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class MiningAbilityConfigScreen extends HudConfigScreen {

	public MiningAbilityConfigScreen() {
		this(null);
	}

	public MiningAbilityConfigScreen(Screen parent) {
		super(Text.literal("Mining Ability HUD Config"), parent, List.of(MiningAbilityWidget.INSTANCE_CFG));
	}

	@SuppressWarnings("SuspiciousNameCombination")
	@Override
	protected List<IntIntMutablePair> getConfigPos(SkyblockerConfig config) {
		return List.of(
				IntIntMutablePair.of(config.mining.miningAbilityHud.abilityX, config.mining.miningAbilityHud.abilityY)
		);
	}

	@Override
	protected void renderWidget(DrawContext context, List<Widget> widgets) {
		MiningAbilityHud.render(MiningAbilityWidget.INSTANCE_CFG, context, widgets.getFirst().getX(), widgets.getFirst().getY());
	}

	@Override
	protected void savePos(SkyblockerConfig configManager, List<Widget> widgets) {
		configManager.mining.miningAbilityHud.abilityX = widgets.getFirst().getX();
		configManager.mining.miningAbilityHud.abilityY = widgets.getFirst().getY();
	}
}
