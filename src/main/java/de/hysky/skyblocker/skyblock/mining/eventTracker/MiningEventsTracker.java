package de.hysky.skyblocker.skyblock.mining.eventTracker;

import com.mojang.logging.LogUtils;
import de.hysky.skyblocker.SkyblockerMod;
import de.hysky.skyblocker.skyblock.mining.eventTracker.MiningEventsDataJSON.*;
import de.hysky.skyblocker.utils.Http;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MiningEventsTracker {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static long canRequestAt = 0;
	private static int apiError = 0;
	private static MiningEventData miningEventData; // TODO cache data? sometimes data gets removed when requesting

	// TODO send data???

	public static Optional<MiningEventData> getMiningEventData() {
		if (canRequestAt < System.currentTimeMillis()) {
			canRequestAt = System.currentTimeMillis() + Duration.ofMinutes(1).toMillis();
			refreshEvents();
		}
		return miningEventData == null ? Optional.empty() : Optional.of(miningEventData);
	}

	private static void refreshEvents() {
		CompletableFuture.supplyAsync(() -> {
			try {
				return SkyblockerMod.GSON.fromJson(Http.sendGetRequest("https://api.soopy.dev/skyblock/chevents/get"), MiningEventDataReceive.class);
			} catch (Exception e) {
				apiError++;
				canRequestAt = System.currentTimeMillis() + Duration.ofMinutes(apiError >= 5 ? 20 : 1).toMillis();
				LOGGER.error("[Skyblocker] Failed to download mining events list", e);
			}
			return new MiningEventDataReceive(false, null, "API Error");
		}).thenAccept(eventsJson -> {
			canRequestAt = System.currentTimeMillis() + eventsJson.data().updateIn(); // TODO works?
			if (!eventsJson.success()) {
				System.out.println("Receiving mining event data was unsuccessful. Cause: " + eventsJson.cause());
			}
			apiError = 0;
			System.out.println("Updating mining event data");
			miningEventData = eventsJson.data();
		}).exceptionally(MiningEventsTracker::itBorked);
		System.out.println("Cant request until " + (canRequestAt - System.currentTimeMillis())/1000 + " seconds");
	}

	private static Void itBorked(Throwable throwable) {
		LOGGER.error("[Skyblocker] Event loading borked, sowwy :3", throwable);
		return null;
	}
}
