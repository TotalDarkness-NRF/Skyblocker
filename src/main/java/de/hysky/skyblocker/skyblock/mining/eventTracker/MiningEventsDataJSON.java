package de.hysky.skyblocker.skyblock.mining.eventTracker;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.hysky.skyblocker.utils.Location;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class MiningEventsDataJSON {
	public record MiningEventDataSend (
			@Expose @SerializedName("server_type") Location serverType,
			@Expose @SerializedName("server_id") String serverId,
			@Expose MiningEvent event,
			@Expose @SerializedName("time_left") Long timeRemaining,
			@Expose @SerializedName("reporter_uuid") String uuid
	) {}

	public record MiningEventDataReceive (
			@Expose boolean success,
			@Expose MiningEventData data,
			@Expose String cause
	) {}

	public record MiningEventData (
			@Expose @SerializedName("event_datas") Map<Location, Map<MiningEvent, EventData>> eventData,
			@Expose @SerializedName("running_events") Map<Location, List<RunningEvent>> runningEvents,
			@Expose @SerializedName("total_lobbys") Map<Location, Integer> totalLobbies,
			@Expose @SerializedName("update_in") Long updateIn,
			@Expose @SerializedName("curr_time") Long currentTime
	) {}

	public record EventData (
			@Expose @SerializedName("starts_at_min") Long startMin,
			@Expose @SerializedName("starts_at_max") Long startMax,
			@Expose @SerializedName("ends_at_min") Long endMin,
			@Expose @SerializedName("ends_at_max") Long endMax,
			@Expose @SerializedName("lobby_count") Long lobbyCount
	) {}

	public record RunningEvent (
			@Expose MiningEvent event,
			@Expose @SerializedName("ends_at") Long endsAt,
			@Expose @SerializedName("lobby_count") Integer lobbyCount,
			@Expose @SerializedName("is_double") Boolean isDoubleEvent
	) {}

	public enum MiningEvent {
		GONE_WITH_THE_WIND("Gone With the Wind","Wind", Duration.ofMinutes(18),false),
		DOUBLE_POWDER("2X Powder", "2x", Duration.ofMinutes(15), false),
		GOBLIN_RAID("Goblin Raid", "Raid", Duration.ofMinutes(5), true),
		BETTER_TOGETHER("Better Together", "Better", Duration.ofMinutes(18), false),
		RAFFLE("Raffle", "Raffle", Duration.ofSeconds(160), true),
		MITHRIL_GOURMAND("Mithril Gourmand", "Gourmand", Duration.ofMinutes(10), true);

		public final String eventName;
		public final String shortName;
		public final Duration defaultLength;
		public final Boolean dwarvenSpecific;

		MiningEvent (String eventName, String shortName, Duration defaultLength, Boolean dwarvenSpecific) {
			this.eventName = eventName;
			this.shortName = shortName;
			this.defaultLength = defaultLength;
			this.dwarvenSpecific = dwarvenSpecific;
		}
	}
}
