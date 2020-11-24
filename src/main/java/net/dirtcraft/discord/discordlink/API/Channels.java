package net.dirtcraft.discord.discordlink.API;

import net.dirtcraft.discord.discordlink.Commands.Sources.ResponseScheduler;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Channels {
    public static final JDA jda = DiscordLink.getJDA();
    private static Channel defaultChannel = new Channel(PluginConfiguration.Channels.gamechatChannel);
    private static Channel playerCountChannel = new Channel(PluginConfiguration.Channels.playerCountChannel);
    private static Channel commandLogChannel = new Channel(PluginConfiguration.Channels.commandLogChannel);
    private static Channel serverLogChannel = new Channel(PluginConfiguration.Channels.serverLogChannel);
    private static long guild = Optional.of(PluginConfiguration.Channels.gamechatCategory)
            .map(jda::getCategoryById)
            .map(Category::getGuild)
            .map(Guild::getIdLong)
            .orElse(-1L);
    private static Map<Long, Channel> gameChats = Optional.of(PluginConfiguration.Channels.gamechatCategory)
            .map(jda::getCategoryById)
            .map(Category::getChannels)
            .map(List::stream)
            .orElse(Stream.of())
            .map(ISnowflake::getIdLong)
            .collect(Collectors.toMap(c->c, Channel::new));

    public static Channel getDefaultChat(){
        return defaultChannel;
    }

    public static Channel getServerLogChannel() {
        return serverLogChannel;
    }

    public static Channel getCommandLogChannel() {
        return commandLogChannel;
    }

    public static Channel getPlayerCountChannel() {
        return playerCountChannel;
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

    public static void update(){
        playerCountChannel = new Channel(PluginConfiguration.Channels.playerCountChannel);
        commandLogChannel = new Channel(PluginConfiguration.Channels.commandLogChannel);
        serverLogChannel = new Channel(PluginConfiguration.Channels.serverLogChannel);
        defaultChannel = new Channel(PluginConfiguration.Channels.gamechatChannel);
        guild = Optional.of(PluginConfiguration.Channels.gamechatCategory)
                .map(jda::getCategoryById)
                .map(Category::getGuild)
                .map(Guild::getIdLong)
                .orElse(-1L);
        gameChats = Optional.of(PluginConfiguration.Channels.gamechatCategory)
                .map(jda::getCategoryById)
                .map(Category::getChannels)
                .map(List::stream)
                .orElse(Stream.of())
                .map(ISnowflake::getIdLong)
                .collect(Collectors.toMap(c->c, Channel::new));

    }
}
