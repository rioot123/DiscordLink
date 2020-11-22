package net.dirtcraft.discord.discordlink.API;

import net.dirtcraft.discord.discordlink.Commands.Sources.ResponseScheduler;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.dirtcraft.discord.discordlink.Storage.PluginConfiguration.Main.*;

public class Channels {
    public static final JDA jda = DiscordLink.getJDA();
    private static final Channel logChannel = new Channel(serverLogChannelID);
    private static final Channel defaultChannel = new Channel(defaultChannelID);
    private static final long guild = Optional.of(GAMECHAT_CATEGORY_ID)
            .map(jda::getCategoryById)
            .map(Category::getGuild)
            .map(Guild::getIdLong)
            .orElse(-1L);
    private static final Map<Long, Channel> gameChats = Optional.of(GAMECHAT_CATEGORY_ID)
            .map(jda::getCategoryById)
            .map(Category::getChannels)
            .map(List::stream)
            .orElse(Stream.of())
            .map(ISnowflake::getIdLong)
            .collect(Collectors.toMap(c->c, Channel::new));

    public static Channel getDefaultChat(){
        return defaultChannel;
    }

    public static Channel getLogChannel() {
        return logChannel;
    }

    public static boolean isDefault(MessageChannel channel) {
        return channel.getIdLong() == defaultChannel.getId();
    }

    public static boolean isGamechat(MessageChannel channel){
        return gameChats.containsKey(channel.getIdLong());
    }

    public static Guild getGuild(){
        return jda.getGuildById(guild);
    }

    public static void sendPlayerMessage(String prefix, String playerName, String message) {
        final Channel gamechat = getDefaultChat();
        final String output = PluginConfiguration.Format.serverToDiscord
                .replace("{prefix}", prefix)
                .replace("{username}", playerName)
                .replace("{message}", message);
        ResponseScheduler.submit(gamechat.getChatResponder(), output);
    }
}
