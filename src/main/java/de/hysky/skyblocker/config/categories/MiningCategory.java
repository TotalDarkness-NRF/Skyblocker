package de.hysky.skyblocker.config.categories;

import de.hysky.skyblocker.config.ConfigUtils;
import de.hysky.skyblocker.config.SkyblockerConfig;
import de.hysky.skyblocker.config.configs.MiningConfig;
import de.hysky.skyblocker.skyblock.dwarven.CrystalsHudConfigScreen;
import de.hysky.skyblocker.skyblock.dwarven.DwarvenHudConfigScreen;
import de.hysky.skyblocker.skyblock.mining.MiningAbilityConfigScreen;
import de.hysky.skyblocker.skyblock.mining.coinTracker.CoinTrackerConfigScreen;
import de.hysky.skyblocker.skyblock.mining.eventTracker.MiningEventsConfigScreen;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.awt.*;

public class MiningCategory {

    public static ConfigCategory create(SkyblockerConfig defaults, SkyblockerConfig config) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("skyblocker.config.mining"))

                //Uncategorized Options
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("skyblocker.config.mining.enableDrillFuel"))
                        .binding(defaults.mining.enableDrillFuel,
                                () -> config.mining.enableDrillFuel,
                                newValue -> config.mining.enableDrillFuel = newValue)
                        .controller(ConfigUtils::createBooleanController)
                        .build())

				.option(Option.<Boolean>createBuilder()
						.name(Text.translatable("skyblocker.config.mining.enablePrivateIslandAbilityBlock"))
						.binding(defaults.mining.enablePrivateIslandAbilityBlock,
								() -> config.mining.enablePrivateIslandAbilityBlock,
								newValue -> config.mining.enablePrivateIslandAbilityBlock = newValue)
						.controller(ConfigUtils::createBooleanController)
						.build())

                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("skyblocker.config.mining.commissionHighlight"))
                        .binding(defaults.mining.commissionHighlight,
                                () -> config.mining.commissionHighlight,
                                newValue -> config.mining.commissionHighlight = newValue)
                        .controller(ConfigUtils::createBooleanController)
                        .build())

				// Coin Tracker Hud
				.group(OptionGroup.createBuilder()
						.name(Text.translatable("skyblocker.config.mining.coinTrackerHud"))
						.collapsed(false)
						.option(Option.<Boolean>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.coinTrackerHud.enableCoinTrackerHud"))
								.binding(defaults.mining.coinTrackerHud.enableCoinTrackerHud,
										() -> config.mining.coinTrackerHud.enableCoinTrackerHud,
										newValue -> config.mining.coinTrackerHud.enableCoinTrackerHud = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<MiningConfig.CoinTrackerItemStyle>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.coinTrackerHud.itemNameStyle"))
								.description(OptionDescription.of(Text.translatable("skyblocker.config.mining.coinTrackerHud.itemNameStyle.@Tooltip[0]"),
										Text.translatable("skyblocker.config.mining.coinTrackerHud.itemNameStyle.@Tooltip[1]"),
										Text.translatable("skyblocker.config.mining.coinTrackerHud.itemNameStyle.@Tooltip[2]")))
								.binding(defaults.mining.coinTrackerHud.itemNameStyle,
										() -> config.mining.coinTrackerHud.itemNameStyle,
										newValue -> config.mining.coinTrackerHud.itemNameStyle = newValue)
								.controller(ConfigUtils::createEnumCyclingListController)
								.build())
						.option(Option.<MiningConfig.CoinTrackerStatsStyle>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.coinTrackerHud.statsStyle"))
								.description(OptionDescription.of(Text.translatable("skyblocker.config.mining.coinTrackerHud.statsStyle.@Tooltip[0]"),
										Text.translatable("skyblocker.config.mining.coinTrackerHud.statsStyle.@Tooltip[1]"),
										Text.translatable("skyblocker.config.mining.coinTrackerHud.statsStyle.@Tooltip[2]")))
								.binding(defaults.mining.coinTrackerHud.statsStyle,
										() -> config.mining.coinTrackerHud.statsStyle,
										newValue -> config.mining.coinTrackerHud.statsStyle = newValue)
								.controller(ConfigUtils::createEnumCyclingListController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.coinTrackerHud.showUptime"))
								.binding(defaults.mining.coinTrackerHud.showUptime,
										() -> config.mining.coinTrackerHud.showUptime,
										newValue -> config.mining.coinTrackerHud.showUptime = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.coinTrackerHud.showTotalPrice"))
								.binding(defaults.mining.coinTrackerHud.showTotalPrice,
										() -> config.mining.coinTrackerHud.showTotalPrice,
										newValue -> config.mining.coinTrackerHud.showTotalPrice = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.coinTrackerHud.showPriceOfOne"))
								.binding(defaults.mining.coinTrackerHud.showPriceOfOne,
										() -> config.mining.coinTrackerHud.showPriceOfOne,
										newValue -> config.mining.coinTrackerHud.showPriceOfOne = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.coinTrackerHud.showPriceType"))
								.binding(defaults.mining.coinTrackerHud.showPriceType,
										() -> config.mining.coinTrackerHud.showPriceType,
										newValue -> config.mining.coinTrackerHud.showPriceType = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Integer>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.coinTrackerHud.timeToReset"))
								.description(OptionDescription.of(Text.translatable("skyblocker.config.mining.coinTrackerHud.timeToReset@Tooltip")))
								.binding(defaults.mining.coinTrackerHud.timeToReset,
										() -> config.mining.coinTrackerHud.timeToReset,
										newValue -> config.mining.coinTrackerHud.timeToReset = newValue)
								.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(-1, 60).step(1))
								.build())
						.option(Option.<MiningConfig.PriceType>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.coinTrackerHud.priceType"))
								.description(OptionDescription.of(Text.translatable("skyblocker.config.mining.coinTrackerHud.priceType.@Tooltip[0]"),
										Text.translatable("skyblocker.config.mining.coinTrackerHud.priceType.@Tooltip[1]"),
										Text.translatable("skyblocker.config.mining.coinTrackerHud.priceType.@Tooltip[2]")))
								.binding(defaults.mining.coinTrackerHud.priceType,
										() -> config.mining.coinTrackerHud.priceType,
										newValue -> config.mining.coinTrackerHud.priceType = newValue)
								.controller(ConfigUtils::createEnumCyclingListController)
								.build())
						.option(Option.<String>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.coinTrackerHud.focusRegex"))
								.binding(defaults.mining.coinTrackerHud.focusRegex,
										() -> config.mining.coinTrackerHud.focusRegex,
										newValue -> config.mining.coinTrackerHud.focusRegex = newValue)
								.controller(StringControllerBuilder::create)
								.build())
						.option(ButtonOption.createBuilder()
								.name(Text.translatable("skyblocker.config.mining.coinTrackerHud.screen"))
								.text(Text.translatable("text.skyblocker.open"))
								.action((screen, opt) -> MinecraftClient.getInstance().setScreen(new CoinTrackerConfigScreen(screen)))
								.build())
						.build())

				// Mining Ability Hud
				.group(OptionGroup.createBuilder()
						.name(Text.translatable("skyblocker.config.mining.miningAbilityHud"))
						.collapsed(false)
						.option(Option.<Boolean>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.miningAbilityHud.enableAbilityHud"))
								.binding(defaults.mining.miningAbilityHud.enableAbilityHud,
										() -> config.mining.miningAbilityHud.enableAbilityHud,
										newValue -> config.mining.miningAbilityHud.enableAbilityHud = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.miningAbilityHud.alwaysDisplay"))
								.binding(defaults.mining.miningAbilityHud.alwaysDisplay,
										() -> config.mining.miningAbilityHud.alwaysDisplay,
										newValue -> config.mining.miningAbilityHud.alwaysDisplay = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.miningAbilityHud.showIcon"))
								.binding(defaults.mining.miningAbilityHud.showIcon,
										() -> config.mining.miningAbilityHud.showIcon,
										newValue -> config.mining.miningAbilityHud.showIcon = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(ButtonOption.createBuilder()
								.name(Text.translatable("skyblocker.config.mining.miningAbilityHud.screen"))
								.text(Text.translatable("text.skyblocker.open"))
								.action((screen, opt) -> MinecraftClient.getInstance().setScreen(new MiningAbilityConfigScreen(screen)))
								.build())
						.build())

				// Mining Events Widget
				.group(OptionGroup.createBuilder()
						.name(Text.translatable("skyblocker.config.mining.miningEventsWidget"))
						.collapsed(false)
						.option(Option.<Boolean>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.miningEventsWidget.enableEventsWidget"))
								.binding(defaults.mining.miningEventsWidget.enableEventsWidget,
										() -> config.mining.miningEventsWidget.enableEventsWidget,
										newValue -> config.mining.miningEventsWidget.enableEventsWidget = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.miningEventsWidget.alwaysDisplay"))
								.binding(defaults.mining.miningEventsWidget.alwaysDisplay,
										() -> config.mining.miningEventsWidget.alwaysDisplay,
										newValue -> config.mining.miningEventsWidget.alwaysDisplay = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.miningEventsWidget.compact"))
								.binding(defaults.mining.miningEventsWidget.compact,
										() -> config.mining.miningEventsWidget.compact,
										newValue -> config.mining.miningEventsWidget.compact = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.miningEventsWidget.showIcon"))
								.binding(defaults.mining.miningEventsWidget.showIcon,
										() -> config.mining.miningEventsWidget.showIcon,
										newValue -> config.mining.miningEventsWidget.showIcon = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.miningEventsWidget.shortLocationName"))
								.binding(defaults.mining.miningEventsWidget.shortLocationName,
										() -> config.mining.miningEventsWidget.shortLocationName,
										newValue -> config.mining.miningEventsWidget.shortLocationName = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.miningEventsWidget.shortEventName"))
								.binding(defaults.mining.miningEventsWidget.shortEventName,
										() -> config.mining.miningEventsWidget.shortEventName,
										newValue -> config.mining.miningEventsWidget.shortEventName = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(ButtonOption.createBuilder()
								.name(Text.translatable("skyblocker.config.mining.miningEventsWidget.screen"))
								.text(Text.translatable("text.skyblocker.open"))
								.action((screen, opt) -> MinecraftClient.getInstance().setScreen(new MiningEventsConfigScreen(screen)))
								.build())
						.build())

				//HOTM solver
				.group(OptionGroup.createBuilder()
						.name(Text.translatable("skyblocker.config.mining.hotmSolver"))
						.collapsed(false)
						.option(Option.<Boolean>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.hotmSolver.enableHOTMSolver"))
								.binding(defaults.mining.hotmSolver.enableHOTMSolver,
										() -> config.mining.hotmSolver.enableHOTMSolver,
										newValue -> config.mining.hotmSolver.enableHOTMSolver = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.hotmSolver.highlightAbilities"))
								.binding(defaults.mining.hotmSolver.highlightAbilities,
										() -> config.mining.hotmSolver.highlightAbilities,
										newValue -> config.mining.hotmSolver.highlightAbilities = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.hotmSolver.highlightEnabledPerks"))
								.binding(defaults.mining.hotmSolver.highlightEnabledPerks,
										() -> config.mining.hotmSolver.highlightEnabledPerks,
										newValue -> config.mining.hotmSolver.highlightEnabledPerks = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.hotmSolver.highlightMaxedPerks"))
								.binding(defaults.mining.hotmSolver.highlightMaxedPerks,
										() -> config.mining.hotmSolver.highlightMaxedPerks,
										newValue -> config.mining.hotmSolver.highlightMaxedPerks = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.option(Option.<Boolean>createBuilder()
								.name(Text.translatable("skyblocker.config.mining.hotmSolver.highlightDisabledPerks"))
								.binding(defaults.mining.hotmSolver.highlightDisabledPerks,
										() -> config.mining.hotmSolver.highlightDisabledPerks,
										newValue -> config.mining.hotmSolver.highlightDisabledPerks = newValue)
								.controller(ConfigUtils::createBooleanController)
								.build())
						.build())

				//Dwarven Mines
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("skyblocker.config.mining.dwarvenMines"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.dwarvenMines.solveFetchur"))
                                .binding(defaults.mining.dwarvenMines.solveFetchur,
                                        () -> config.mining.dwarvenMines.solveFetchur,
                                        newValue -> config.mining.dwarvenMines.solveFetchur = newValue)
                                .controller(ConfigUtils::createBooleanController)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.dwarvenMines.solvePuzzler"))
                                .binding(defaults.mining.dwarvenMines.solvePuzzler,
                                        () -> config.mining.dwarvenMines.solvePuzzler,
                                        newValue -> config.mining.dwarvenMines.solvePuzzler = newValue)
                                .controller(ConfigUtils::createBooleanController)
                                .build())
                        .build())

                //Dwarven HUD
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("skyblocker.config.mining.dwarvenHud"))
                        .collapsed(false)
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.dwarvenHud.enabledCommissions"))
                                .binding(defaults.mining.dwarvenHud.enabledCommissions,
                                        () -> config.mining.dwarvenHud.enabledCommissions,
                                        newValue -> config.mining.dwarvenHud.enabledCommissions = newValue)
                                .controller(ConfigUtils::createBooleanController)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.dwarvenHud.enabledPowder"))
                                .binding(defaults.mining.dwarvenHud.enabledPowder,
                                        () -> config.mining.dwarvenHud.enabledPowder,
                                        newValue -> config.mining.dwarvenHud.enabledPowder = newValue)
                                .controller(ConfigUtils::createBooleanController)
                                .build())
                        .option(Option.<MiningConfig.DwarvenHudStyle>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.dwarvenHud.style"))
                                .description(OptionDescription.of(Text.translatable("skyblocker.config.mining.dwarvenHud.style.@Tooltip[0]"),
                                        Text.translatable("skyblocker.config.mining.dwarvenHud.style.@Tooltip[1]"),
                                        Text.translatable("skyblocker.config.mining.dwarvenHud.style.@Tooltip[2]")))
                                .binding(defaults.mining.dwarvenHud.style,
                                        () -> config.mining.dwarvenHud.style,
                                        newValue -> config.mining.dwarvenHud.style = newValue)
                                .controller(ConfigUtils::createEnumCyclingListController)
                                .build())
                        .option(ButtonOption.createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.dwarvenHud.screen"))
                                .text(Text.translatable("text.skyblocker.open"))
                                .action((screen, opt) -> MinecraftClient.getInstance().setScreen(new DwarvenHudConfigScreen(screen)))
                                .build())
                        .build())

                //Crystal Hollows
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("skyblocker.config.mining.crystalHollows"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.crystalHollows.metalDetectorHelper"))
                                .description(OptionDescription.of(Text.translatable("skyblocker.config.mining.crystalHollows.metalDetectorHelper.@Tooltip")))
                                .binding(defaults.mining.crystalHollows.metalDetectorHelper,
                                        () -> config.mining.crystalHollows.metalDetectorHelper,
                                        newValue -> config.mining.crystalHollows.metalDetectorHelper = newValue)
                                .controller(ConfigUtils::createBooleanController)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.crystalHollows.nucleusWaypoints"))
                                .description(OptionDescription.of(Text.translatable("skyblocker.config.mining.crystalHollows.nucleusWaypoints.@Tooltip")))
                                .binding(defaults.mining.crystalHollows.nucleusWaypoints,
                                        () -> config.mining.crystalHollows.nucleusWaypoints,
                                        newValue -> config.mining.crystalHollows.nucleusWaypoints = newValue)
                                .controller(ConfigUtils::createBooleanController)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.crystalHollows.chestHighlighter"))
                                .description(OptionDescription.of(Text.translatable("skyblocker.config.mining.crystalHollows.chestHighlighter.@Tooltip")))
                                .binding(defaults.mining.crystalHollows.chestHighlighter,
                                        () -> config.mining.crystalHollows.chestHighlighter,
                                        newValue -> config.mining.crystalHollows.chestHighlighter = newValue)
                                .controller(ConfigUtils::createBooleanController)
                                .build())
                        .option(Option.<Color>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.crystalHollows.chestHighlighter.color"))
                                .description(OptionDescription.of(Text.translatable("skyblocker.config.mining.crystalHollows.chestHighlighter.color.@Tooltip")))
                                .binding(defaults.mining.crystalHollows.chestHighlightColor,
                                        () -> config.mining.crystalHollows.chestHighlightColor,
                                        newValue -> config.mining.crystalHollows.chestHighlightColor = newValue)
                                .controller(v -> ColorControllerBuilder.create(v).allowAlpha(true))
                                .build())
                        .build())

                //Crystal Hollows Map
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("skyblocker.config.mining.crystalsHud"))
                        .collapsed(false)
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.crystalsHud.enabled"))
                                .binding(defaults.mining.crystalsHud.enabled,
                                        () -> config.mining.crystalsHud.enabled,
                                        newValue -> config.mining.crystalsHud.enabled = newValue)
                                .controller(ConfigUtils::createBooleanController)
                                .build())
                        .option(ButtonOption.createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.crystalsHud.screen"))
                                .text(Text.translatable("text.skyblocker.open"))
                                .action((screen, opt) -> MinecraftClient.getInstance().setScreen(new CrystalsHudConfigScreen(screen)))
                                .build())
                        .option(Option.<Float>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.crystalsHud.mapScaling"))
                                .binding(defaults.mining.crystalsHud.mapScaling,
                                        () -> config.mining.crystalsHud.mapScaling,
                                        newValue -> config.mining.crystalsHud.mapScaling = newValue)
                                .controller(FloatFieldControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.crystalsHud.showLocations"))
                                .description(OptionDescription.of(Text.translatable("skyblocker.config.mining.crystalsHud.showLocations.@Tooltip")))
                                .binding(defaults.mining.crystalsHud.showLocations,
                                        () -> config.mining.crystalsHud.showLocations,
                                        newValue -> config.mining.crystalsHud.showLocations = newValue)
                                .controller(ConfigUtils::createBooleanController)
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.crystalsHud.showLocations.locationSize"))
                                .description(OptionDescription.of(Text.translatable("skyblocker.config.mining.crystalsHud.showLocations.locationSize.@Tooltip")))
                                .binding(defaults.mining.crystalsHud.locationSize,
                                        () -> config.mining.crystalsHud.locationSize,
                                        newValue -> config.mining.crystalsHud.locationSize = newValue)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(4, 12).step(2))
                                .build())
                        .build())

                //Crystal Hollows waypoints
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("skyblocker.config.mining.crystalsWaypoints"))
                        .collapsed(false)
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.crystalsWaypoints.enabled"))
                                .description(OptionDescription.of(Text.translatable("skyblocker.config.mining.crystalsWaypoints.enabled.@Tooltip")))
                                .binding(defaults.mining.crystalsWaypoints.enabled,
                                        () -> config.mining.crystalsWaypoints.enabled,
                                        newValue -> config.mining.crystalsWaypoints.enabled = newValue)
                                .controller(ConfigUtils::createBooleanController)
                                .build())
                        .option(Option.<Float>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.crystalsWaypoints.textScale"))
                                .description(OptionDescription.of(Text.translatable("skyblocker.config.mining.crystalsWaypoints.textScale.@Tooltip")))
                                .binding(defaults.mining.crystalsWaypoints.textScale,
                                        () -> config.mining.crystalsWaypoints.textScale,
                                        newValue -> config.mining.crystalsWaypoints.textScale = newValue)
                                .controller(FloatFieldControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.crystalsWaypoints.findInChat"))
                                .description(OptionDescription.of(Text.translatable("skyblocker.config.mining.crystalsWaypoints.findInChat.@Tooltip")))
                                .binding(defaults.mining.crystalsWaypoints.findInChat,
                                        () -> config.mining.crystalsWaypoints.findInChat,
                                        newValue -> config.mining.crystalsWaypoints.findInChat = newValue)
                                .controller(ConfigUtils::createBooleanController)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.crystalsWaypoints.wishingCompassSolver"))
                                .description(OptionDescription.of(Text.translatable("skyblocker.config.mining.crystalsWaypoints.wishingCompassSolver.@Tooltip")))
                                .binding(defaults.mining.crystalsWaypoints.wishingCompassSolver,
                                        () -> config.mining.crystalsWaypoints.wishingCompassSolver,
                                        newValue -> config.mining.crystalsWaypoints.wishingCompassSolver = newValue)
                                .controller(ConfigUtils::createBooleanController)
                                .build())

                        .build())

                //commission waypoints
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("skyblocker.config.mining.commissionWaypoints"))
                        .collapsed(false)
                        .option(Option.<MiningConfig.CommissionWaypointMode>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.commissionWaypoints.mode"))
                                .description(OptionDescription.of(Text.translatable("skyblocker.config.mining.commissionWaypoints.mode.@Tooltip[0]"),
                                        Text.translatable("skyblocker.config.mining.commissionWaypoints.mode.@Tooltip[1]"),
                                        Text.translatable("skyblocker.config.mining.commissionWaypoints.mode.@Tooltip[2]"),
                                        Text.translatable("skyblocker.config.mining.commissionWaypoints.mode.@Tooltip[3]"),
                                        Text.translatable("skyblocker.config.mining.commissionWaypoints.mode.@Tooltip[4]")))
                                .binding(defaults.mining.commissionWaypoints.mode,
                                        () -> config.mining.commissionWaypoints.mode,
                                        newValue -> config.mining.commissionWaypoints.mode = newValue)
                                .controller(ConfigUtils::createEnumCyclingListController)
                                .build())
                        .option(Option.<Float>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.commissionWaypoints.textScale"))
                                .description(OptionDescription.of(Text.translatable("skyblocker.config.mining.commissionWaypoints.textScale.@Tooltip")))
                                .binding(defaults.mining.commissionWaypoints.textScale,
                                        () -> config.mining.commissionWaypoints.textScale,
                                        newValue -> config.mining.commissionWaypoints.textScale = newValue)
                                .controller(FloatFieldControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.commissionWaypoints.useColor"))
                                .description(OptionDescription.of(Text.translatable("skyblocker.config.mining.commissionWaypoints.useColor.@Tooltip")))
                                .binding(defaults.mining.commissionWaypoints.useColor,
                                        () -> config.mining.commissionWaypoints.useColor,
                                        newValue -> config.mining.commissionWaypoints.useColor = newValue)
                                .controller(ConfigUtils::createBooleanController)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.commissionWaypoints.showBaseCamp"))
                                .description(OptionDescription.of(Text.translatable("skyblocker.config.mining.commissionWaypoints.showBaseCamp.@Tooltip")))
                                .binding(defaults.mining.commissionWaypoints.showBaseCamp,
                                        () -> config.mining.commissionWaypoints.showBaseCamp,
                                        newValue -> config.mining.commissionWaypoints.showBaseCamp = newValue)
                                .controller(ConfigUtils::createBooleanController)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.commissionWaypoints.showEmissary"))
                                .description(OptionDescription.of(Text.translatable("skyblocker.config.mining.commissionWaypoints.showEmissary.@Tooltip")))
                                .binding(defaults.mining.commissionWaypoints.showEmissary,
                                        () -> config.mining.commissionWaypoints.showEmissary,
                                        newValue -> config.mining.commissionWaypoints.showEmissary = newValue)
                                .controller(ConfigUtils::createBooleanController)
                                .build())
                        .build())

                //Glacite Tunnels
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("skyblocker.config.mining.glacite"))
                        .collapsed(false)
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("skyblocker.config.mining.glacite.coldOverlay"))
                                .description(OptionDescription.of(Text.translatable("skyblocker.config.mining.glacite.coldOverlay@Tooltip")))
                                .binding(defaults.mining.glacite.coldOverlay,
                                        () -> config.mining.glacite.coldOverlay,
                                        newValue -> config.mining.glacite.coldOverlay = newValue)
                                .controller(ConfigUtils::createBooleanController)
                                .build())
                        .build())
                .build();
    }
}
