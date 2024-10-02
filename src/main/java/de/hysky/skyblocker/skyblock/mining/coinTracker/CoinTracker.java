package de.hysky.skyblocker.skyblock.mining.coinTracker;

import com.mojang.brigadier.Command;
import de.hysky.skyblocker.SkyblockerMod;
import de.hysky.skyblocker.annotations.Init;
import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.config.configs.MiningConfig;
import de.hysky.skyblocker.events.SkyblockEvents;
import de.hysky.skyblocker.skyblock.item.tooltip.info.TooltipInfoType;
import de.hysky.skyblocker.skyblock.itemlist.ItemRepository;
import de.hysky.skyblocker.skyblock.itemlist.SkyblockCraftingRecipe;
import de.hysky.skyblocker.utils.Constants;
import de.hysky.skyblocker.utils.Location;
import de.hysky.skyblocker.utils.Utils;
import de.hysky.skyblocker.utils.scheduler.Scheduler;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static de.hysky.skyblocker.skyblock.mining.HOTMSolver.CLIENT;

//TODO this could work for more then just mining
public class CoinTracker {
	private static final Logger LOGGER = LoggerFactory.getLogger(CoinTracker.class);
	private final static Pattern SACKS = Pattern.compile("\\[Sacks]( [+-][\\d,]+ items[,.]){1,2} \\(Last \\d+s\\.\\)");
	private final static Pattern SACK_LINE = Pattern.compile("[+-](?<amount>[\\d,]+) (?<item>.+) \\((?<sack>.+)\\)");
	private final static Pattern PRISTINE = Pattern.compile("PRISTINE! You found (?<item>.+) x(?<amount>[\\d,]+)!");
	private final static Pattern COMPACT = Pattern.compile("COMPACT! You found an? (?<item>.+)!");
	// TODO refined?
	// TODO find other stuff like farming stuff
	private static Long timeFirstBlockMined = null;
	private static long timeLastBlockMined;
	private final static Map<ItemStack,Integer> coinTracker = new HashMap<>();
	private final static Map<ItemStack,Integer> coinTrackerInventory = new HashMap<>();
	private final static Map<ItemStack,Integer> coinTrackerEstimate = new HashMap<>();

	@Init
	public static void init() {
		ClientReceiveMessageEvents.GAME.register(CoinTracker::onChatMessage);
		SkyblockEvents.LOCATION_CHANGE.register(CoinTracker::handleLocationChange);
		Scheduler.INSTANCE.scheduleCyclic(CoinTracker::update, 10);
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
				dispatcher.register(ClientCommandManager.literal(SkyblockerMod.NAMESPACE)
						.then(ClientCommandManager.literal("resetCoinTracker")
								.executes(context -> {
									reset();
									return Command.SINGLE_SUCCESS;
								}))));
	}

	private static void update() {
		if (!Utils.isOnSkyblock()) return;
		if (!SkyblockerConfigManager.get().mining.coinTrackerHud.enableCoinTrackerHud) return;
		if (timeFirstBlockMined == null) return;
		updateInventory();
		if (SkyblockerConfigManager.get().mining.coinTrackerHud.timeToReset == -1) return;
		Duration elapsed = Duration.ofMillis(System.currentTimeMillis() - timeLastBlockMined);
		if (elapsed.toMinutesPart() >= SkyblockerConfigManager.get().mining.coinTrackerHud.timeToReset)
			reset();
	}

	private static void updateTimers() {
		if (!SkyblockerConfigManager.get().mining.coinTrackerHud.enableCoinTrackerHud) return;
		if (coinTracker.isEmpty() && coinTrackerEstimate.isEmpty()) return;
		if (timeFirstBlockMined == null) timeFirstBlockMined = System.currentTimeMillis();
		timeLastBlockMined = System.currentTimeMillis();
	}

	private static void updateInventory() {
		if (CLIENT.player == null) return;
		Map<ItemStack, Integer> inventory = new HashMap<>();
		CLIENT.player.getInventory().main.stream()
				.filter(itemStack -> isFocusedItem(itemStack.getName().getString()))
				.filter(itemStack -> getItemPrice(itemStack.getSkyblockId()).isPresent())
				.forEach(itemStack -> {
					Optional<ItemStack> item = getItem(itemStack.getSkyblockId());
					if (item.isEmpty()) return;
					int amount = itemStack.getCount();
					if (inventory.containsKey(item.get())) amount += inventory.get(item.get());
					inventory.put(item.get(), amount);
				});
		if (inventory.equals(coinTrackerInventory)) return;
		coinTrackerInventory.clear();
		coinTrackerEstimate.clear();
		coinTrackerInventory.putAll(inventory);
		updateTimers();
	}

	private static void reset() {
		coinTracker.clear();
		coinTrackerEstimate.clear();
		timeFirstBlockMined = null;
		timeLastBlockMined = 0;
	}

	public static Map<ItemStack, Integer> getCoinTracker() {
		Map<ItemStack, Integer> coinTrackerCopy = new HashMap<>(Map.copyOf(coinTracker));
		coinTrackerEstimate.forEach((item, amount) -> {
			if (coinTrackerCopy.containsKey(item)) amount += coinTrackerCopy.get(item);
			coinTrackerCopy.put(item, amount);
		});
		coinTrackerInventory.forEach((item, amount) -> {
			if (coinTrackerCopy.containsKey(item)) amount += coinTrackerCopy.get(item);
			coinTrackerCopy.put(item, amount);
		});
		return coinTrackerCopy;
		//return new TreeMap<>(coinTracker); // TODO need to sort by price make comparable
	}

	private static boolean handleSacksMessage(Text text) {
		if (!SACKS.matcher(text.getString()).matches()) return false;
		List<Text> hoverLines = text.getSiblings().stream()
				.filter(t -> t.getString().startsWith("+") || t.getString().startsWith("-"))
				.map(t -> t.getStyle().getHoverEvent())
				.filter(Objects::nonNull)
				.map(t ->  t.getValue(HoverEvent.Action.SHOW_TEXT))
				.filter(Objects::nonNull)
				.toList();
		hoverLines.forEach(hoverText ->
				Arrays.stream(hoverText.getString().split("\\r?\\n"))
						.map(String::trim).toList().reversed()
						.forEach(CoinTracker::handleSackLine)
		);
		return !hoverLines.isEmpty();
	}

	private static void handleSackLine(String sackLine) {
		if (sackLine == null) return;
		Matcher matcher = SACK_LINE.matcher(sackLine);
		if (!matcher.matches()) return;
		String itemName = matcher.group("item");
		if (!isFocusedItem(itemName)) return;
		int amount = Integer.parseInt(matcher.group("amount").replaceAll(",",""));
		Optional<String> id = getItemId(itemName);
		if (id.isEmpty()) return;
		Optional<ItemStack> item = getItem(id.get());
		if (item.isEmpty()) return;
		if (sackLine.startsWith("-")) {
			if (!coinTracker.containsKey(item.get())) return;
			int oldAmount = coinTracker.get(item.get());
			if (oldAmount > amount) {
				coinTracker.put(item.get(), oldAmount - amount);
				handleUpgrades(item.get());
			} else coinTracker.remove(item.get());
		} else {
			if (coinTracker.containsKey(item.get())) amount += coinTracker.get(item.get());
			coinTracker.put(item.get(), amount);
			coinTrackerEstimate.clear();
		}
	}

	private static boolean handlePristineMessage(Text text) {
		Matcher matcher = PRISTINE.matcher(text.getString());
		if (!matcher.matches()) return false;
		String itemName = matcher.group("item");
		if (!isFocusedItem(itemName)) return false;
		int amount = Integer.parseInt(matcher.group("amount").replaceAll(",",""));
		Optional<String> id = getItemId(itemName);
		if (id.isEmpty()) return false;
		Optional<ItemStack> item = getItem(id.get());
		if (item.isEmpty()) return false;
		if (coinTrackerEstimate.containsKey(item.get())) amount += coinTrackerEstimate.get(item.get());
		coinTrackerEstimate.put(item.get(), amount);
		return true;
	}

	private static boolean handleCompactMessage(Text text) {
		Matcher matcher = COMPACT.matcher(text.getString());
		if (!matcher.matches()) return false;
		String itemName = matcher.group("item");
		if (!isFocusedItem(itemName)) return false;
		int amount = 1;
		Optional<String> id = getItemId(itemName);
		if (id.isEmpty()) return false;
		Optional<ItemStack> item = getItem(id.get());
		if (item.isEmpty()) return false;
		if (coinTrackerEstimate.containsKey(item.get())) amount += coinTrackerEstimate.get(item.get());
		coinTrackerEstimate.put(item.get(), amount);
		return true;
	}

	private static void onChatMessage(Text text, boolean overlay) {
		if (!SkyblockerConfigManager.get().mining.coinTrackerHud.enableCoinTrackerHud) return;
		if (handleSacksMessage(text) || handlePristineMessage(text) || handleCompactMessage(text))
			updateTimers();
	}

	private static void handleLocationChange(Location location) {
		coinTrackerEstimate.forEach((item, amount) -> {
			if (coinTracker.containsKey(item))
				coinTracker.put(item, coinTracker.get(item) + amount);
			else coinTracker.put(item, amount);
		});
		coinTrackerEstimate.clear();
	}

	private static void handleUpgrades(ItemStack item) {
		if (!coinTracker.containsKey(item)) return;
		getNextUpgrade(item).ifPresent(recipe -> {
			ItemStack itemUpgrade = recipe.getResult();
			boolean containsItems = false;
			for (ItemStack itemStack : coinTracker.keySet()) {
				if (!itemStack.getName().equals(itemUpgrade.getName())) continue;
				containsItems = true;
				break;
			}
			if (containsItems) {
				int amount = coinTracker.get(item);
				int amountToUpgrade = recipe.getGrid().stream().filter(i -> item.getName().equals(i.getName())).mapToInt(ItemStack::getCount).sum();
				if (amountToUpgrade == 0) {
					System.out.println("Amount to upgrade was 0. Item: " + item.getName());
					return;
				}
				int amountUpgraded = Math.floorDiv(amount, amountToUpgrade);
				int resourcesUsed = amountUpgraded * amountToUpgrade;
				if (resourcesUsed >= amount) coinTracker.remove(item);
				else coinTracker.put(item, amount - resourcesUsed);
			}
		});
	}

	private static Optional<String> getItemId(String itemName) {
		if (itemName == null) return Optional.empty();
		for (ItemStack itemStack : ItemRepository.getItems()) {
			String name = itemStack.getName().getLiteralString();
			if (name != null && name.replaceAll("§.", "").equals(itemName))
				return Optional.ofNullable(itemStack.getSkyblockId());
		}
		return Optional.empty();
	}

	private static Optional<ItemStack> getItem(String itemId) {
		if (itemId == null) return Optional.empty();
		return ItemRepository.getItemsStream()
				.filter(i -> itemId.equals(i.getSkyblockId()))
				.findFirst();
	}

	public static Optional<Double> getItemPrice(String id) {
		// TODO may not work if certain options in Tooltip info are turned off, have fallback
		if (SkyblockerConfigManager.get().mining.coinTrackerHud.priceType.equals(MiningConfig.PriceType.NPC)) {
			if (TooltipInfoType.NPC.getData() == null) return Optional.empty();
			return Optional.of(TooltipInfoType.NPC.getData().getDouble(id));
		} else {
			if (TooltipInfoType.BAZAAR.getData() == null) return Optional.empty();
			boolean isInstantSell = SkyblockerConfigManager.get().mining.coinTrackerHud.priceType.equals(MiningConfig.PriceType.BAZAAR_INSTANT_SELL);
			OptionalDouble price = isInstantSell ? TooltipInfoType.BAZAAR.getData().get(id).sellPrice() : TooltipInfoType.BAZAAR.getData().get(id).buyPrice();
			if (price.isPresent()) return Optional.of(price.getAsDouble());
			else return Optional.empty();
		}
	}

	public static Optional<SkyblockCraftingRecipe> getNextUpgrade(ItemStack item) {
		if (item == null) return Optional.empty();
		return ItemRepository.getRecipes(item.getSkyblockId()).stream()
				.filter(result -> isAnUpgrade(result.getResult().getName().getString()))
				.filter(recipe -> !item.getName().equals(recipe.getResult().getName()))
				.findFirst();
	}

	private static boolean isAnUpgrade(String itemName) {
		if (itemName == null) return false;
		return itemName.contains("Enchanted") || itemName.contains("Flawed")
				|| itemName.contains("Fine") || itemName.contains("Flawless");
	}

	private static boolean isFocusedItem(String itemName) {
		String focusRegex = SkyblockerConfigManager.get().mining.coinTrackerHud.focusRegex;
		if (focusRegex == null || focusRegex.isEmpty()) return true;
		boolean matches = false;
		try {
			matches = itemName.matches(focusRegex);
		} catch (PatternSyntaxException e) {
			LOGGER.error("[Skyblocker] Failed to parse focusRegex with regex: {}", focusRegex, e);
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			if (player != null) {
				player.sendMessage(Constants.PREFIX.get().append(Text.literal("Invalid regex in focusRegex!").formatted(Formatting.RED)), false);
			}
		}
		return matches;
	}

	public static Long getTimeFirstBlockMined() {
		return timeFirstBlockMined;
	}
}
