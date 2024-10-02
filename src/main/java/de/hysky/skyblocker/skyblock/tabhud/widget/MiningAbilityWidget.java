package de.hysky.skyblocker.skyblock.tabhud.widget.hud;

import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.skyblock.mining.Ability;
import de.hysky.skyblocker.skyblock.tabhud.widget.Widget;
import de.hysky.skyblocker.skyblock.tabhud.widget.component.Component;
import de.hysky.skyblocker.skyblock.tabhud.widget.component.IcoTextComponent;
import de.hysky.skyblocker.skyblock.tabhud.widget.component.PlainTextComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static de.hysky.skyblocker.skyblock.mining.Ability.getAbilities;

// this widget shows the mining abilities and cooldowns
// dwarven mines and crystal hollows or everywhere if enabled
public class MiningAbilityWidget extends Widget {
	private static final MutableText TITLE = Text.literal("Mining Abilities").formatted(Formatting.DARK_AQUA, Formatting.BOLD);

	public static final MiningAbilityWidget INSTANCE = new MiningAbilityWidget();
	public static final MiningAbilityWidget INSTANCE_CFG = new MiningAbilityWidget();

	public MiningAbilityWidget() {
		super(TITLE, Formatting.DARK_AQUA.getColorValue());
	}

	@Override
	public void updateContent() {
		// TODO refactor
		for (Ability ability : getAbilities()) {
			MutableText abilityNameText = Text.literal(String.format("%s: ", ability.getName()));
			MutableText abilityCooldownText = Text.literal(String.format("%d", ability.getCooldownLeft()));
			MutableText text = Text.literal("s");
			if (ability.getCooldownLeft() > 50) abilityCooldownText.formatted(Formatting.RED);
			else if (ability.getCooldownLeft() > 0) abilityCooldownText.formatted(Formatting.YELLOW);
			else {
				abilityCooldownText = Text.literal("Ready!").formatted(Formatting.GREEN);
				text = Text.empty();
			}

			if (ability.isEnabled()) {
				text.formatted(Formatting.DARK_GREEN);
				text = abilityNameText.formatted(Formatting.DARK_GREEN).append(abilityCooldownText).append(text);
				text.formatted(Formatting.UNDERLINE, Formatting.BOLD);
			} else {
				text.formatted(Formatting.GRAY);
				text = abilityNameText.formatted(Formatting.GRAY).append(abilityCooldownText).append(text);
			}

			Component comp;
			if (SkyblockerConfigManager.get().mining.miningAbilityHud.showIcon) {
				// TODO use separate icons for each
				comp = new IcoTextComponent(new ItemStack(Items.PRISMARINE_SHARD), text);
			} else {
				comp = new PlainTextComponent(text);
			}
			this.addComponent(comp);
		}
	}

}
