package de.hysky.skyblocker.skyblock.mining;

import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.utils.ItemUtils;
import de.hysky.skyblocker.utils.Utils;
import de.hysky.skyblocker.utils.container.SimpleContainerSolver;
import de.hysky.skyblocker.utils.render.gui.ColorHighlight;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class HOTMSolver extends SimpleContainerSolver {
	public static MinecraftClient CLIENT = MinecraftClient.getInstance();

	public static final Pattern HOTM = Pattern.compile("^Heart of the Mountain$");
	public static final Pattern RESET_HOTM = Pattern.compile("^Reset your Heart of the Mountain! Your Perks and Abilities have been reset.");
	public static final Pattern DRILL = Pattern.compile("Breaking Power (?<power>\\d+)");
	public static final Pattern ABILITY = Pattern.compile("Ability: (?<ability>[^\\s].+[^\\s])\\s+RIGHT CLICK");
	public static final Pattern ABILITY_SELECTED = Pattern.compile("You selected (?<ability>[^\\s].+[^\\s]) as your Pickaxe Ability. This ability will apply to all of your pickaxes!");
	public static final Pattern ABILITY_USED = Pattern.compile("You used your (?<ability>[^\\s].+[^\\s]) Pickaxe Ability!");
	public static final Pattern COOLDOWN = Pattern.compile("Cooldown: (?<cooldown>\\d+)s");
	public static final Pattern ABILITY_ON_COOLDOWN = Pattern.compile("This ability is on cooldown for (?<cooldown>\\d+)s.");
	public static final int SCROLL_UP = 8;
	public static final int SCROLL_DOWN = 53;
	// TODO track skymall perk

	public HOTMSolver() {
		super(HOTM.pattern());
		AbilitiesUpdater.init();
	}

	public static boolean isHOTMScreen(Screen screen) {
		return Utils.isOnSkyblock() && screen instanceof GenericContainerScreen containerScreen
				&& HOTM.matcher(containerScreen.getTitle().getString()).matches();
	}

	@Override
	public boolean isEnabled() {
		return SkyblockerConfigManager.get().mining.hotmSolver.enableHOTMSolver;
	}

	@Override
	public List<ColorHighlight> getColors(Int2ObjectMap<ItemStack> slots) {
		List<ColorHighlight> abilities = slots.int2ObjectEntrySet().stream()
				.filter(slot -> SkyblockerConfigManager.get().mining.hotmSolver.highlightAbilities)
				.filter(slot -> slot.getValue().isOf(Items.EMERALD_BLOCK))
				.map(slot -> ItemUtils.getLoreLineIfMatch(slot.getValue(), Pattern.compile("SELECTED")) != null ?
						ColorHighlight.green(slot.getIntKey()) : ColorHighlight.red(slot.getIntKey()))
				.toList();

		List<Int2ObjectMap.Entry<ItemStack>> perks = slots.int2ObjectEntrySet().stream()
				.filter(slot -> slot.getValue().isOf(Items.DIAMOND) || slot.getValue().isOf(Items.EMERALD))
				.toList();

		List<ColorHighlight> enabledPerks = perks.stream()
				.filter(slot -> SkyblockerConfigManager.get().mining.hotmSolver.highlightEnabledPerks)
				.filter(slot -> ItemUtils.getLoreLineIfMatch(slot.getValue(), Pattern.compile("ENABLED")) != null)
				.map(slot -> slot.getValue().isOf(Items.DIAMOND) || !SkyblockerConfigManager.get().mining.hotmSolver.highlightMaxedPerks ?
						ColorHighlight.green(slot.getIntKey()) : ColorHighlight.yellow(slot.getIntKey()))
				.toList();

		List<ColorHighlight> disabledPerks = perks.stream()
				.filter(slot -> SkyblockerConfigManager.get().mining.hotmSolver.highlightDisabledPerks)
				.filter(slot -> ItemUtils.getLoreLineIfMatch(slot.getValue(), Pattern.compile("DISABLED")) != null)
				.map(slot -> ColorHighlight.red(slot.getIntKey()))
				.toList();

		return Stream.of(abilities, enabledPerks, disabledPerks)
				.flatMap(Collection::stream)
				.toList();
	}

	public static Optional<Slot> getScrollUpSlot(List<Slot> slots) {
		return slots.stream()
				.filter(slot -> slot.getStack().isOf(Items.ARROW))
				.filter(slot -> slot.id == SCROLL_UP)
				.filter(slot -> slot.getStack().getName().getString().equals("Scroll Up"))
				.findFirst();
	}

	public static Optional<Slot> getScrollDownSlot(List<Slot> slots) {
		return slots.stream()
				.filter(slot -> slot.getStack().isOf(Items.ARROW))
				.filter(slot -> slot.id == SCROLL_DOWN)
				.filter(slot -> slot.getStack().getName().getString().equals("Scroll Down"))
				.findFirst();
	}
}
