package de.hysky.skyblocker.skyblock.mining;

import com.mojang.brigadier.Command;
import de.hysky.skyblocker.SkyblockerMod;
import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.events.SkyblockEvents;
import de.hysky.skyblocker.utils.ItemUtils;
import de.hysky.skyblocker.utils.Location;
import de.hysky.skyblocker.utils.Utils;
import de.hysky.skyblocker.utils.scheduler.Scheduler;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.regex.Matcher;

import static de.hysky.skyblocker.skyblock.mining.Ability.*;
import static de.hysky.skyblocker.skyblock.mining.HOTMSolver.*;

public class AbilitiesUpdater {
	private static long lastUpdate;
	public static boolean abilitiesFound;

	public static void init() {
		SkyblockEvents.LOCATION_CHANGE.register(AbilitiesUpdater::handleLocationChange);
		ClientReceiveMessageEvents.GAME.register(AbilitiesUpdater::onChatMessage);
		Scheduler.INSTANCE.scheduleCyclic(AbilitiesUpdater::update, 10);

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
				dispatcher.register(ClientCommandManager.literal(SkyblockerMod.NAMESPACE)
						.then(ClientCommandManager.literal("resetAbilities")
								.executes(context -> {
									reset();
									return Command.SINGLE_SUCCESS;
								}))));

	}

	protected static void reset() {
		abilitiesFound = false;
		Ability.reset();
	}

	private static void handleLocationChange(Location location) {
		getAbilities().forEach(a -> a.setCooldownLeft(a.getCooldownLocationChange()));
	}

	private static void onChatMessage(Text text, boolean overlay) {
		if (overlay || CLIENT.player == null) return;

		// HOTM reset message, reset abilities list
		if (RESET_HOTM.matcher(text.getString()).matches()) {
			AbilitiesUpdater.reset();
			CLIENT.inGameHud.getChatHud().addMessage(Text.literal("HOTM menu reset!"));
			return;
		}

		if (!SkyblockerConfigManager.get().mining.miningAbilityHud.enableAbilityHud) return;

		// Ability Selected message, update enabled ability
		Matcher matcher = ABILITY_SELECTED.matcher(text.getString());
		if (matcher.matches()) {
			String abilityName = matcher.group("ability");
			getAbility(abilityName).ifPresentOrElse(a -> {}, () -> searchForEnabledAbility(abilityName));
			return;
		}
		//Ability Used message,
		matcher = ABILITY_USED.matcher(text.getString());
		if (matcher.matches()) {
			getAbility(matcher.group("ability")).ifPresent(Ability::resetCooldown);
			return;
		}
		//Ability On Cooldown, update enabled ability cooldown
		matcher = ABILITY_ON_COOLDOWN.matcher(text.getString());
		if (matcher.matches()) {
			int cooldown = Integer.parseInt(matcher.group("cooldown"));
			getEnabled().ifPresent(ability -> ability.setCooldownLeft(cooldown));
		}
	}

	public static void update() {
		if (CLIENT.player == null || !Utils.isOnSkyblock()) return;
		searchForEnabledAbility();
		updateAbilitiesCooldown();
		updateAbilityList();
	}

	private static void updateAbilitiesCooldown() {
		long elapsedTime = System.currentTimeMillis() - lastUpdate;
		if (elapsedTime > 1000) {
			lastUpdate = System.currentTimeMillis();
			getAbilities().forEach(Ability::decreaseCooldown);
		}
	}

	private static void updateAbilityList() {
		if (CLIENT.currentScreen == null || !isHOTMScreen(CLIENT.currentScreen)) return;
		final GenericContainerScreen HOTMScreen = (GenericContainerScreen) CLIENT.currentScreen;
		HOTMScreen.getScreenHandler().slots.stream()
				.filter(slot -> slot.getStack().isOf(Items.EMERALD_BLOCK))
				.forEach(slot -> {
							String abilityName = slot.getStack().getName().getString();
							Matcher cooldownMatcher = ItemUtils.getLoreLineIfMatch(slot.getStack(), COOLDOWN);
							if (cooldownMatcher == null) return;
							int cooldown = Integer.parseInt(cooldownMatcher.group("cooldown"));
							Ability ability = new Ability(abilityName, cooldown);
							getEnabled().ifPresent(a -> ability.setCooldownLeft(ability.getCooldownLate()));
							if (addAbility(ability)) {
								CLIENT.inGameHud.getChatHud().addMessage(Text.literal("Adding " + abilityName));
							}
							if (ItemUtils.getLoreLineIf(slot.getStack(), s -> s.contains("SELECTED")) != null)
								ability.enable();
						}
				);
	}

	private static void searchForEnabledAbility() {
		searchForEnabledAbility(null);
	}

	private static void searchForEnabledAbility(String name) {
		if (CLIENT.player == null || !Utils.isOnSkyblock()) return;
		CLIENT.player.getInventory().main.stream()
				.filter(itemStack -> ItemUtils.getLoreLineIfMatch(itemStack, DRILL) != null)
				.findFirst()
				.ifPresent(drill -> {
					String abilityName;
					if (name == null) {
						Matcher abilityMatcher = ItemUtils.getLoreLineIfMatch(drill, ABILITY);
						if (abilityMatcher == null) return;
						abilityName = abilityMatcher.group("ability");
					} else abilityName = name;

					Matcher cooldownMatcher = ItemUtils.getLoreLineIfMatch(drill, COOLDOWN);
					if (cooldownMatcher == null) return;

					int cooldown = Integer.parseInt(cooldownMatcher.group("cooldown"));
					Ability ability = new Ability(abilityName, cooldown);
					if (addAbility(ability)) {
						CLIENT.inGameHud.getChatHud().addMessage(Text.literal("Adding " + abilityName));
					}
					ability.enable();
				});
	}
}
