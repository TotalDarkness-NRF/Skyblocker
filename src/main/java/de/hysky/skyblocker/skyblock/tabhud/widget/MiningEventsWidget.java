package de.hysky.skyblocker.skyblock.tabhud.widget;

import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.skyblock.mining.eventTracker.MiningEventsDataJSON.MiningEventData;
import de.hysky.skyblocker.skyblock.mining.eventTracker.MiningEventsDataJSON.MiningEvent;
import de.hysky.skyblocker.skyblock.mining.eventTracker.MiningEventsDataJSON.RunningEvent;

import de.hysky.skyblocker.skyblock.tabhud.util.Ico;
import de.hysky.skyblocker.skyblock.tabhud.widget.component.*;
import de.hysky.skyblocker.utils.Location;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.hysky.skyblocker.skyblock.profileviewer.utils.ProfileViewerUtils.createSkull;
import static de.hysky.skyblocker.skyblock.mining.HOTMSolver.CLIENT;
import static de.hysky.skyblocker.skyblock.mining.eventTracker.MiningEventsTracker.getMiningEventData;
import static java.util.Map.entry;

// this widget shows info about mining events
// dwarven mines and crystal hollows or everywhere if enabled

public class MiningEventsWidget extends Widget {
	private static final MutableText TITLE = Text.literal("Mining Events").formatted(Formatting.AQUA, Formatting.BOLD);

	public static final MiningEventsWidget INSTANCE = new MiningEventsWidget();
	public static final MiningEventsWidget INSTANCE_CFG = new MiningEventsWidget();
	private static ItemStack betterTogetherSkull = new ItemStack(Items.PLAYER_HEAD);
	private static final Component ARROW_COMPONENT = new PlainTextComponent(Text.literal(" -> ").formatted(Formatting.AQUA, Formatting.BOLD));

	// TODO make something like this for event data
	public static final Map<String, ItemStack> ICON_MAP = Map.ofEntries(
			entry("Wind", new ItemStack(Items.COMPASS)),
			entry("2x", new ItemStack(Items.LIGHT_BLUE_DYE)),
			entry("Raid", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzE3Mjg1MDkwNmI3ZjBkOTUyYzBlNTA4MDczY2M0MzlmZDMzNzRjY2Y1Yjg4OWMwNmY3ZThkOTBjYzBjYzI1NWMifX19")),
			//entry("Raid", SkullCreator.createSkull("ewogICJ0aW1lc3RhbXAiIDogMTYwNzQ2NDg4MTMwOCwKICAicHJvZmlsZUlkIiA6ICJhMmY4MzQ1OTVjODk0YTI3YWRkMzA0OTcxNmNhOTEwYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJiUHVuY2giLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTcyODUwOTA2YjdmMGQ5NTJjMGU1MDgwNzNjYzQzOWZkMzM3NGNjZjViODg5YzA2ZjdlOGQ5MGNjMGNjMjU1YyIKICAgIH0KICB9Cn0=")),
			entry("Better", betterTogetherSkull), // TODO make two heads
			entry("Raffle", new ItemStack(Items.NAME_TAG)),
			entry("Gourmand", new ItemStack(Items.CYAN_DYE)),
			entry("DWARVEN_MINES", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzZiMjBiMjNjMWFhMmJlMDI3MGYwMTZiNGM5MGQ2ZWU2YjgzMzBhMTdjZmVmODc4NjlkNmFkNjBiMmZmYmYzYjUifX19")),
			entry("CRYSTAL_HOLLOWS", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzIxZGJlMzBiMDI3YWNiY2ViNjEyNTYzYmQ4NzdjZDdlYmI3MTllYTZlZDEzOTkwMjdkY2VlNThiYjkwNDlkNGEifX19")),
			entry("MINESHAFT", createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzI0NjFlYzNiZDY1NGY2MmNhOWEzOTNhMzI2MjllMjFiNGU0OTdjODc3ZDNmMzM4MGJjZjJkYjBlMjBmYzAyNDQifX19"))
	);

	public MiningEventsWidget() {
		super(TITLE, Formatting.AQUA.getColorValue());
	}

	@Override
	public void updateContent() {
		// TODO add past events?
		// TODO Look something like this
		// Mines: Wind -> 2x
		// Crystal: 2x -> Better
		// Mineshaft: 2x -> Wind
		getMiningEventData().ifPresent(this::components);
	}

	private void components(MiningEventData miningEventData) {
		if (miningEventData == null || miningEventData.runningEvents() == null) return;

        /*
        // TODO option for table instead
       Table component results in ugly spaces and adds ugly lines
       |--------------------------------------------------------|  Components (ARROW shows if needed)
       |Mines      |eventName0| ->            | ... |eventNameN||  Location | eventName0 | ARROW  | + |...| + | ARROW | + |eventNameN
       |Crystal:   |eventName0| ->            | ... |eventNameN||
       |Mineshaft: |eventName0| ->            | ... |eventNameN||
       |--------------------------------------------------------|

       // TODO create more like this
       // TODO need some sort of component adder, that shifts components based off other components x and y values
       |---------------------------------------|  Components (ARROW shows if needed)
       |Mines: eventName0 -> ... eventNameN    |  Location: + eventName0 + ARROW + ... + ARROW + eventNameN
       |Crystal: eventName0 -> ... eventNameN  |
       |Mineshaft: eventName0 -> ... eventNameN|
       |---------------------------------------|
         */
		if (SkyblockerConfigManager.get().mining.miningEventsWidget.compact)
			componentsCompact(miningEventData);
		else componentsTable(miningEventData);
	}

	private void componentsCompact(MiningEventData miningEventData) {
		miningEventData.runningEvents().forEach((location, runningEvents) -> {
			MultiComponent eventComponents = new MultiComponent();
			eventComponents.add(getLocationComponent(location));
			runningEvents.forEach(runningEvent -> {
				if (runningEvent == null || runningEvent.event() == null) return;
				Optional<Component> eventComponent = getEventComponent(runningEvent);
				if (eventComponent.isEmpty()) return;
				if (eventComponents.size() <= 1) eventComponents.add(eventComponent.get());
				else {
					eventComponents.add(ARROW_COMPONENT);
					eventComponents.add(eventComponent.get());
				}
			});
			this.addComponent(eventComponents);
		});
	}

	private void componentsTable(MiningEventData miningEventData) {
		List<List<Component>> components = new ArrayList<>();
		AtomicInteger row = new AtomicInteger();
		AtomicInteger column = new AtomicInteger();
		miningEventData.runningEvents().forEach((location, runningEvents) -> {
			List<Component> eventComponents = new ArrayList<>();
			eventComponents.add(getLocationComponent(location));
			row.getAndIncrement(); // New row
			for (RunningEvent runningEvent : runningEvents) {
				if (runningEvent == null || runningEvent.event() == null) continue;
				Optional<Component> eventComponent = getEventComponent(runningEvent);
				if (eventComponent.isEmpty()) continue;
				eventComponents.add(eventComponent.get());
				if (eventComponents.size() > column.get()) column.set(eventComponents.size()); // Increase column if needed
			}
			components.add(eventComponents);
		});
		TableComponent tableComponent = new TableComponent(column.get(), row.get(), Formatting.WHITE.getColorValue());
		for (int i = 0; i < components.size(); i++) {
			for(int j = 0; j < components.get(i).size(); j++) {
				Component component = components.get(i).get(j);
				tableComponent.addToCell(j, i, component);
			}
		}
		this.addComponent(tableComponent);
	}

	private Optional<Component> getEventComponent(RunningEvent runningEvent) {
		//  TODO if a double event do twice?
		if (runningEvent == null || runningEvent.event() == null) return Optional.empty();
		String text = getEventName(runningEvent.event());
		if (SkyblockerConfigManager.get().mining.miningEventsWidget.showIcon)
			return Optional.of(getIconComponent(runningEvent, Text.literal(text)));
		else return Optional.of(new PlainTextComponent(Text.literal(text)));
	}

	private Component getIconComponent(RunningEvent runningEvent, Text text) {
		String iconName = runningEvent.event().shortName;
		//if (iconName.equals("Better")) {
		long elapsedTime = runningEvent.endsAt() - System.currentTimeMillis();
		//if (elapsedTime%1000 == 0) updateBetterTogetherSkull();
		//}
		ItemStack icon = ICON_MAP.getOrDefault(iconName, Ico.BARRIER);
		return new IcoTextComponent(icon, text);
	}

	private void updateBetterTogetherSkull() {
		if (CLIENT.getNetworkHandler() == null) return;
		Pattern namePattern = Pattern.compile("\\[(?<level>\\d+)] (?<name>\\w+) .+");
		List<String> playerList = CLIENT.getNetworkHandler()
				.getPlayerList()
				.stream()
				.filter(p -> p.getDisplayName() != null)
				.map(p -> {
					// TODO get GameProfiles and set to a skull
					Matcher matcher = namePattern.matcher(p.getDisplayName().getString());
					if(matcher.matches()) {
						//System.out.println(p.getProfile());
						//System.out.println(matcher.group("name"));
						return matcher.group("name");//p.getSkinTextures().texture().getPath().substring("skins/".length());//matcher.group("name");
					}
					else return null;
				})
				.filter(Objects::nonNull)
				.toList();
		//playerList.forEach(System.out::println);
		if (playerList.isEmpty()) return;
		//ItemStack skull = new ItemStack(Items.PLAYER_HEAD);
		String name = playerList.get(Random.create().nextBetweenExclusive(0, playerList.size()));
		PlayerListEntry p =  CLIENT.getNetworkHandler().getPlayerListEntry(name);

		if (p != null) System.out.println("Test"  + p.getDisplayName());

		//GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);
		//SkinTextures skinTextures = CLIENT.getSkinProvider().getSkinTextures(gameProfile);
		//System.out.println(skinTextures);
		//ProfileComponent profile = new ProfileComponent(playerList.get(Random.create().nextBetweenExclusive(0, playerList.size())));
		//skull.set(DataComponentTypes.PROFILE, profile);
		//betterTogetherSkull = skull;
		// {"textures":{"SKIN":{"url":"https://textures.minecraft.net/texture/172850906b7f0d952c0e508073cc439fd3374ccf5b889c06f7e8d90cc0cc255c"}}}

		//String texture = String.format("\"{\"textures\":{\"SKIN\":{\"url\":\"https://textures.minecraft.net/texture/%s\"}}}", texturePath);
		//System.out.println(texture);
		//betterTogetherSkull = SkullCreator.createSkull(Base64.getEncoder().encodeToString(texture.getBytes()));;
		System.out.println("Changing better together skull");
	}

	private Component getLocationComponent(Location location) {
		String text = getLocationName(location);
		if (SkyblockerConfigManager.get().mining.miningEventsWidget.compact)
			text = text.concat(": ");
		if (SkyblockerConfigManager.get().mining.miningEventsWidget.showIcon) {
			ItemStack icon = ICON_MAP.getOrDefault(location.name(), Ico.BARRIER);
			return new IcoTextComponent(icon, Text.literal(text));
		}
		else return new PlainTextComponent(Text.literal(text));
	}

	private String getEventName(MiningEvent event) {
		if (event.eventName == null) return "No Name";
		else if (SkyblockerConfigManager.get().mining.miningEventsWidget.shortEventName)
			return event.shortName;
		else return event.eventName;
	}

	private String getLocationName(Location location) {
		if (location == null) location = Location.UNKNOWN;
		boolean shortName = SkyblockerConfigManager.get().mining.miningEventsWidget.shortLocationName;
		return switch (location) {
			case DWARVEN_MINES -> shortName ? "Mines" : "Dwarven Mines";
			case CRYSTAL_HOLLOWS -> shortName ? "Crystal" : "Crystal Hollows";
			case MINESHAFT -> "Mineshaft";
			default -> location.name();
		};
	}
}
