package net.dirtcraft.discord.spongediscordlib;

import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class SpongeDiscordLib {
    private static SpongeDiscordLib instance = new SpongeDiscordLib();
    private static long startTime = System.currentTimeMillis();
    private static CompletableFuture<JDA> jda;

    private SpongeDiscordLib() {
        System.out.println("Initializing JDA!");
        jda = CompletableFuture.supplyAsync(this::initJDA);
    }

    public static JDA getJDA() {
        while (!jda.isDone() && !initTimeExceeded()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) { }
        }
        return jda.join();
    }

    public static void setStatus(Activity.ActivityType type, String name, String url) {
        JDA jda = DiscordLink.getJDA();
        if (url != null) {
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(type, name, url));
        } else {
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(type, name));
        }
    }

    public static String getBotToken() {
        return PluginConfiguration.Main.botToken;
    }

    public static String getGamechatChannelID() {
        return PluginConfiguration.Main.GAMECHAT_CHANNEL_ID;
    }

    public static String getServerName() {
        return PluginConfiguration.Main.SERVER_NAME;
    }

    private JDA initJDA() {
        if (jda.getNow(null) != null) return jda.join();
        JDA jda;
        try {
            Collection<GatewayIntent> intents = Arrays.asList(
                    GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.DIRECT_MESSAGES
            );
            jda = JDABuilder.createDefault(getBotToken(), intents)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .build();
            return jda.awaitReady();
        } catch (LoginException | InterruptedException e){
            e.printStackTrace();
            return null;
        }
    }

    private static boolean initTimeExceeded(){
        final long SECOND = 1000;
        final long MINUTE = SECOND * 60;
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("JDA has taken " + duration + "ms to boot!");
        return duration > 5 * MINUTE;
    }
}
