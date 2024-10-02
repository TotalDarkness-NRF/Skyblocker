package de.hysky.skyblocker.skyblock.mining;

import com.mojang.brigadier.Command;
import de.hysky.skyblocker.SkyblockerMod;
import de.hysky.skyblocker.annotations.Init;
import de.hysky.skyblocker.utils.Utils;
import de.hysky.skyblocker.utils.scheduler.MessageScheduler;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientScreenInputEvent;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

import static de.hysky.skyblocker.skyblock.mining.AbilitiesUpdater.abilitiesFound;
import static de.hysky.skyblocker.skyblock.mining.Ability.getLastAbility;
import static de.hysky.skyblocker.skyblock.mining.Ability.getNextAbility;
import static de.hysky.skyblocker.skyblock.mining.HOTMSolver.*;

public class QuickSelectAbility {
	public static KeyBinding quickSelectKeyBind;
	public static boolean abilitySelected;
	private static boolean quickSelectAbilityEnabled;

	//@Init(priority = 100) //TODO Crashes whyy
	public static void init() {
		quickSelectKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.quickSelectAbility",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_F,
				"key.categories.skyblocker"
		));

		ClientRawInputEvent.KEY_PRESSED.register(QuickSelectAbility::handleKeyPress); // Keybinding
		ClientScreenInputEvent.KEY_PRESSED_PRE.register(QuickSelectAbility::handleKeyPressScreen);
		ClientScreenInputEvent.MOUSE_CLICKED_PRE.register(QuickSelectAbility::handleMouseClickedScreen);

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal(SkyblockerMod.NAMESPACE)
				.then(ClientCommandManager.literal("quickSelectAbility")
						.executes(context -> {
							openHOTM();
							return Command.SINGLE_SUCCESS;
						}))));

	}

	private static EventResult handleKeyPress(MinecraftClient client, int keyCode, int scanCode, int action, int modifiers) {
		return getEventResult(CLIENT.currentScreen == null && quickSelectKeyBind.wasPressed() && openHOTM());
	}

	private static EventResult handleKeyPressScreen(MinecraftClient client, Screen screen, int keyCode, int scanCode, int modifiers) {
		return handleScreenInput(screen, keyCode, keyCode == GLFW.GLFW_KEY_LEFT_SHIFT);
	}

	private static EventResult handleMouseClickedScreen(MinecraftClient client, Screen screen, double mouseX, double mouseY, int button) {
		return handleScreenInput(screen, button, button == GLFW.GLFW_MOUSE_BUTTON_RIGHT);
	}

	private static EventResult handleScreenInput(Screen screen, int keyCode, boolean reverse) {
		if (!quickSelectAbilityEnabled) return EventResult.pass();
		if (abilitySelected) {
			quickSelectAbilityEnabled = false;
			abilitySelected = false;
			screen.close();
			return getEventResult(true);
		}
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			if (canHandleInput() && isHOTMScreen(CLIENT.currentScreen)) {
				quickSelectAbilityEnabled = false;
				CLIENT.inGameHud.getChatHud().addMessage(Text.literal("Closing HOTM menu."));
			}
			return EventResult.pass();
		}
		return getEventResult(!selectAbility(screen, reverse));
	}

	private static boolean openHOTM() {
		final boolean canOpenHOTM = canHandleInput();
		if (canOpenHOTM) {
			quickSelectAbilityEnabled = true;
			MessageScheduler.INSTANCE.sendMessageAfterCooldown("/hotm");
		} else CLIENT.inGameHud.getChatHud().addMessage(Text.literal("Can't open Heart of the Mountain menu!"));
		return canOpenHOTM;
	}

	private static boolean canHandleInput() {
		return CLIENT != null && CLIENT.player != null && Utils.isOnSkyblock();
	}

	private static boolean isHOTMScreen(Screen screen) {
		return screen instanceof GenericContainerScreen containerScreen
				&& HOTM.matcher(containerScreen.getTitle().getString()).matches();
	}

	private static boolean selectAbility(Screen screen, boolean reverse) {
		if (abilitySelected) {
			abilitySelected = false;
			quickSelectAbilityEnabled = false;
			screen.close();
			return true;
		}

		if (!canHandleInput() || !isHOTMScreen(screen)) return false;
		final GenericContainerScreen HOTMScreen = (GenericContainerScreen) screen;

		Optional<Ability> ability = reverse ? getLastAbility() : getNextAbility();
		HOTMScreen.getScreenHandler().slots.stream()
				.filter(slot -> abilitiesFound)
				.filter(slot -> slot.getStack().isOf(Items.EMERALD_BLOCK))
				.filter(slot -> ability.isPresent())
				.filter(slot -> ability.get().equalName(slot.getStack().getName().getString()))
				.findFirst()
				.ifPresentOrElse(slot -> clickNextAbility(slot.id), () -> clickScrollSlots(HOTMScreen));
		return true; //Cancel because in HOTM screen
	}

	private static void clickNextAbility(int slotId) {
		click(slotId, false, GLFW.GLFW_MOUSE_BUTTON_LEFT);
		abilitySelected = true;
	}

	private static void clickScrollSlots(GenericContainerScreen screen){
		// TODO refactor messy and confusing
		getScrollUpSlot(screen.getScreenHandler().slots)
				.ifPresentOrElse(slot -> click(SCROLL_UP, false, GLFW.GLFW_MOUSE_BUTTON_RIGHT),
						() -> getScrollDownSlot(screen.getScreenHandler().slots)
								.ifPresentOrElse(slot -> {
											if (!abilitiesFound) abilitiesFound = true;
											click(SCROLL_DOWN, false, GLFW.GLFW_MOUSE_BUTTON_RIGHT);
										},
										() -> abilitySelected = false));
	}

	private static void click(int slotId, boolean shift, int clickType) {
		if (canHandleInput() && CLIENT.interactionManager != null
				&& CLIENT.currentScreen instanceof GenericContainerScreen screen) {
			SlotActionType actionType = shift ? SlotActionType.QUICK_MOVE : SlotActionType.PICKUP;
			CLIENT.interactionManager.clickSlot(screen.getScreenHandler().syncId, slotId, clickType, actionType, CLIENT.player);
		}
	}

	private static EventResult getEventResult(boolean pass) {
		return pass ? EventResult.pass() : EventResult.interruptTrue();
	}
}
