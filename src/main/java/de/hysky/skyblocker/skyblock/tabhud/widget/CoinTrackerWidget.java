package de.hysky.skyblocker.skyblock.tabhud.widget;

import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.config.configs.MiningConfig;
import de.hysky.skyblocker.skyblock.tabhud.widget.component.Component;
import de.hysky.skyblocker.skyblock.tabhud.widget.component.IcoTextComponent;
import de.hysky.skyblocker.skyblock.tabhud.widget.component.PlainTextComponent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static de.hysky.skyblocker.skyblock.mining.coinTracker.CoinTracker.*;

// this widget shows info about coins made while mining

public class CoinTrackerWidget extends Widget {
	private static final MutableText TITLE = Text.literal("Coin Tracker").formatted(Formatting.AQUA, Formatting.BOLD);

	public static final CoinTrackerWidget INSTANCE = new CoinTrackerWidget();
	public static final CoinTrackerWidget INSTANCE_CFG = new CoinTrackerWidget();

	public CoinTrackerWidget() {
		super(TITLE, Formatting.AQUA.getColorValue());
	}

	@Override
	public void updateContent() {
		// TODO allow to change scale
		MiningConfig.CoinTrackerItemStyle style = SkyblockerConfigManager.get().mining.coinTrackerHud.itemNameStyle;
		String priceType = SkyblockerConfigManager.get().mining.coinTrackerHud.priceType.toString();
		AtomicReference<Double> total = new AtomicReference<>(0.0);
		// TODO want to order stuff by amount made from it
		getCoinTracker().forEach((item, amount) -> {
			String id = item.getSkyblockId();
			Optional<Double> price = getItemPrice(id);
			if (price.isEmpty() || item.getName().getLiteralString() == null) return;
			String itemName = item.getName().getLiteralString()/*.replaceAll("§.", "")*/;
			// TODO maybe put priceType in title?
			total.updateAndGet(v -> v + price.get() * amount);
			MutableText text = Text.empty();
			text.append(Text.literal(String.format("x%,d", amount)).formatted(Formatting.GOLD, Formatting.BOLD));
			if (style != MiningConfig.CoinTrackerItemStyle.ICON)
				text.append(" ").append(Text.literal(itemName).formatted(Formatting.BOLD));
			text.append(Text.literal(":").formatted(Formatting.AQUA, Formatting.BOLD)).append(" ");
			text.append(Text.literal(String.format("$%,.2f", price.get()*amount)).formatted(Formatting.GREEN));

			MutableText priceTypeText = Text.empty();
			if (SkyblockerConfigManager.get().mining.coinTrackerHud.showPriceType) {
				priceTypeText.append(Text.literal(priceType).formatted(Formatting.LIGHT_PURPLE));
			}
			if (SkyblockerConfigManager.get().mining.coinTrackerHud.showPriceOfOne) {
				priceTypeText.append(Text.literal(String.format("@$%,.2f", price.get())).formatted(Formatting.GREEN));
			}
			if (!priceTypeText.equals(Text.empty())) {
				text.append(" ").
						append(Text.literal("(").formatted(Formatting.AQUA))
						.append(priceTypeText)
						.append(Text.literal(")").formatted(Formatting.AQUA));
			}

			Component component = style == MiningConfig.CoinTrackerItemStyle.NAME ?
					new PlainTextComponent(text) : new IcoTextComponent(item, text);
			this.addComponent(component);
		});
		if (SkyblockerConfigManager.get().mining.coinTrackerHud.showTotalPrice) {
			String totals = String.format("Total: $%,.2f (%s)", total.get(), priceType);
			// TODO format ??
			this.addComponent(new PlainTextComponent(Text.literal(totals)));
		}
		if (getTimeFirstBlockMined() != null) {
			MutableText statsText = Text.empty();
			Duration duration = Duration.ofMillis(System.currentTimeMillis() - getTimeFirstBlockMined());
			if (SkyblockerConfigManager.get().mining.coinTrackerHud.showUptime) {
				String hours = "";
				if (duration.toHoursPart() > 0) hours = String.format("%,d:", duration.toHoursPart());
				String uptime = String.format("Uptime: %s%02d:%02d", hours, duration.toMinutesPart(), duration.toSecondsPart());
				statsText.append(Text.literal(uptime).formatted(Formatting.GOLD, Formatting.BOLD));
			}
			if (SkyblockerConfigManager.get().mining.coinTrackerHud.statsStyle != MiningConfig.CoinTrackerStatsStyle.NONE) {
				// (Money made / milliseconds) * (millisecond / second) * (second / minute) * (minute / hour)
				// (Money made / second) * 1000 * 60 *  60
				double moneyStats = total.get() / duration.toMillis();
				String moneyText = switch (SkyblockerConfigManager.get().mining.coinTrackerHud.statsStyle) {
					case MONEY_SECOND -> String.format("$%,.2f%s", moneyStats * 1000, "/s");
					case MONEY_MINUTE -> String.format("$%,.2f%s", moneyStats * 60000, "/m");
					case MONEY_HOUR -> String.format("$%,.2f%s", moneyStats * 3600000, "/h");
					case NONE -> "";
				};
				if (!statsText.equals(Text.empty())) statsText.append(" ");
				statsText.append(Text.literal("(").formatted(Formatting.AQUA))
						.append(Text.literal(moneyText).formatted(Formatting.GREEN))
						.append(Text.literal(")").formatted(Formatting.AQUA));
			}
			this.addComponent(new PlainTextComponent(statsText));
		}
	}

}
