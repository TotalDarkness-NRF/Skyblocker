package de.hysky.skyblocker.config.configs;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.resource.language.I18n;

import java.awt.*;

public class MiningConfig {
    @SerialEntry
    public boolean enableDrillFuel = true;

	@SerialEntry
	public boolean enablePrivateIslandAbilityBlock = true;

	@SerialEntry
	public CoinTrackerHud coinTrackerHud = new CoinTrackerHud();

	@SerialEntry
	public MiningAbilityHud miningAbilityHud = new MiningAbilityHud();

	@SerialEntry
	public MiningEventsWidget miningEventsWidget = new MiningEventsWidget();

	@SerialEntry
	public HOTMSolver hotmSolver = new HOTMSolver();


	@SerialEntry
    public DwarvenMines dwarvenMines = new DwarvenMines();

    @SerialEntry
    public DwarvenHud dwarvenHud = new DwarvenHud();

    @SerialEntry
    public CrystalHollows crystalHollows = new CrystalHollows();

    @SerialEntry
    public CrystalsHud crystalsHud = new CrystalsHud();

    @SerialEntry
    public CrystalsWaypoints crystalsWaypoints = new CrystalsWaypoints();

    @SerialEntry
    public CommissionWaypoints commissionWaypoints = new CommissionWaypoints();

    @SerialEntry
    public Glacite glacite = new Glacite();

    @SerialEntry
    public boolean commissionHighlight = true;

    public static class DwarvenMines {
        @SerialEntry
        public boolean solveFetchur = true;

        @SerialEntry
        public boolean solvePuzzler = true;
    }

	public static class CoinTrackerHud {
		@SerialEntry
		public boolean enableCoinTrackerHud = true;

		@SerialEntry
		public CoinTrackerItemStyle itemNameStyle = CoinTrackerItemStyle.ICON_NAME;

		@SerialEntry
		public CoinTrackerStatsStyle statsStyle = CoinTrackerStatsStyle.MONEY_HOUR;

		@SerialEntry
		public boolean showUptime = true;

		@SerialEntry
		public boolean showPriceOfOne = true;

		@SerialEntry
		public boolean showTotalPrice = true;

		@SerialEntry
		public boolean showPriceType = true;

		@SerialEntry
		public int timeToReset = 5;

		@SerialEntry
		public PriceType priceType = PriceType.BAZAAR_INSTANT_SELL;

		@SerialEntry
		public String focusRegex = "";

		@SerialEntry
		public int hudX = 10;

		@SerialEntry
		public int hudY = 10;
	}

	public static class MiningAbilityHud {
		@SerialEntry
		public boolean enableAbilityHud = true;

		@SerialEntry
		public boolean alwaysDisplay = true;

		@SerialEntry
		public boolean showIcon = true;

		@SerialEntry
		public int abilityX = 10;

		@SerialEntry
		public int abilityY = 10;
	}

	public static class MiningEventsWidget {
		@SerialEntry
		public boolean enableEventsWidget = true;

		@SerialEntry
		public boolean alwaysDisplay = true;

		@SerialEntry
		public boolean compact = true;

		@SerialEntry
		public boolean showIcon = true;

		@SerialEntry
		public boolean shortLocationName = true;

		@SerialEntry
		public boolean shortEventName = true;

		@SerialEntry
		public int widgetX = 10;

		@SerialEntry
		public int widgetY = 10;
	}

	public static class HOTMSolver {
		@SerialEntry
		public boolean enableHOTMSolver = true;

		@SerialEntry
		public boolean highlightAbilities = true;

		@SerialEntry
		public boolean highlightEnabledPerks = true;

		@SerialEntry
		public boolean highlightMaxedPerks = true;

		@SerialEntry
		public boolean highlightDisabledPerks = true;
	}

	public static class DwarvenHud {
        @SerialEntry
        public boolean enabledCommissions = true;

        @SerialEntry
        public boolean enabledPowder = true;

        @SerialEntry
        public DwarvenHudStyle style = DwarvenHudStyle.SIMPLE;

        @SerialEntry
        public int commissionsX = 10;

        @SerialEntry
        public int commissionsY = 10;

        @SerialEntry
        public int powderX = 10;

        @SerialEntry
        public int powderY = 70;
    }

    public static class CrystalHollows {
        @SerialEntry
        public boolean metalDetectorHelper = true;

        @SerialEntry
        public boolean nucleusWaypoints = false;

        @SerialEntry
        public boolean chestHighlighter = true;

        @SerialEntry
        public Color chestHighlightColor = new Color(0, 0, 255, 128);
    }

    public static class CrystalsHud {
        @SerialEntry
        public boolean enabled = true;

        @SerialEntry
        public boolean showLocations = true;

        @SerialEntry
        public int locationSize = 8;

        @SerialEntry
        public int x = 10;

        @SerialEntry
        public int y = 130;

        @SerialEntry
        public float mapScaling = 1f;
    }

    public static class CrystalsWaypoints {
        @SerialEntry
        public boolean enabled = true;

        @SerialEntry
        public float textScale = 1;

        @SerialEntry
        public boolean findInChat = true;

        @SerialEntry
        public boolean wishingCompassSolver = true;
    }

    public static class CommissionWaypoints {
        @SerialEntry
        public CommissionWaypointMode mode = CommissionWaypointMode.BOTH;

        @SerialEntry
        public float textScale = 1;

        @SerialEntry
        public boolean useColor = true;

        @SerialEntry
        public boolean showBaseCamp = false;

        @SerialEntry
        public boolean showEmissary = true;
    }

    public enum CommissionWaypointMode {
        OFF, DWARVEN, GLACITE, BOTH;

        @Override
        public String toString() {
            return I18n.translate("skyblocker.config.mining.commissionWaypoints.mode." + name());
        }
    }

    public static class Glacite {
        @SerialEntry
        public boolean coldOverlay = true;
    }

    public enum DwarvenHudStyle {
        SIMPLE, FANCY, CLASSIC;

        @Override
        public String toString() {
            return switch (this) {
                case SIMPLE -> "Simple";
                case FANCY -> "Fancy";
                case CLASSIC -> "Classic";
            };
        }
    }

	public enum PriceType {
		BAZAAR_INSTANT_SELL, BAZAAR_SELL_OFFER, NPC;

		@Override
		public String toString() {
			return switch (this) {
				case BAZAAR_INSTANT_SELL -> "Instant Sell";
				case BAZAAR_SELL_OFFER -> "Sell Offer";
				case NPC -> "NPC";
			};
		}
	}

	public enum CoinTrackerItemStyle {
		ICON_NAME, NAME, ICON;

		@Override
		public String toString() {
			return switch (this) {
				case ICON_NAME -> "Icon and Name";
				case NAME -> "Name";
				case ICON -> "Icon";
			};
		}
	}

	public enum CoinTrackerStatsStyle {
		MONEY_HOUR, MONEY_MINUTE, MONEY_SECOND, NONE;

		@Override
		public String toString() {
			return switch (this) {
				case MONEY_HOUR -> "Money Per Hour";
				case MONEY_MINUTE -> "Money Per Minute";
				case MONEY_SECOND -> "Money Per Second";
				case NONE -> "None";
			};
		}
	}
}
