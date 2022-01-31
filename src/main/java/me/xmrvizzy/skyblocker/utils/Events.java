package me.xmrvizzy.skyblocker.utils;

import me.xmrvizzy.skyblocker.SkyblockerMod;
import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Events {
    public static MinecraftClient client = MinecraftClient.getInstance();
    public static Logger logger = LoggerFactory.getLogger(SkyblockerMod.NAMESPACE);

    public static void onSkyblockJoin(){
        Utils.isOnSkyblock = true;
        logger.info("Joined Skyblock");
        if (UpdateChecker.shouldUpdate() && SkyblockerConfig.get().general.enableUpdateNotification){
            LiteralText link = new LiteralText("https://modrinth.com/mod/skyblocker-liap/versions");
            client.player.sendMessage(Text.of("You are running an outdated version of Skyblocker! Click the link below to find the newest version!"), false);
            client.player.sendMessage(link.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/skyblocker-liap/versions"))), false);
        }
    }

    public static void onSkyblockDisconnect(){
        logger.info("Disconnected from Skyblock");
        SkyblockerMod.getInstance().discordRPCManager.stop();
        Utils.isOnSkyblock = false;
        Utils.isInDungeons = false;
    }
}
