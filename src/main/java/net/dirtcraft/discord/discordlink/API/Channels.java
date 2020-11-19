package net.dirtcraft.discord.discordlink.API;

import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.dirtcraft.discord.discordlink.Storage.PluginConfiguration.Main.*;

public class Channels {
    public static final JDA jda = DiscordLink.getJDA();
    private static final Channel logChannel = new Channel(serverLogChannelID);
    private static final Channel defaultChannel = new Channel(defaultChannelID);
    private static final long category = GAMECHAT_CATEGORY_ID;
    private static final long guild = getCategory().getGuild().getIdLong();
    private static final Map<Long, Channel> channels = getCategory().getChannels().stream()
            .filter(TextChannel.class::isInstance)
            .map(ISnowflake::getIdLong)
            .collect(Collectors.toMap(c->c, Channel::new));

    public static Channel getDefaultChat(){
        return defaultChannel;
    }

    public static Channel getLogChannel() {
        return logChannel;
    }

    public static boolean isGamechat(MessageChannel channel){
        return channels.containsKey(channel.getIdLong());
    }

    public static Guild getGuild(){
        return jda.getGuildById(guild);
    }

    public static Category getCategory(){
        return jda.getCategoryById(category);
    }


    public static Optional<Channel> getChannel(TextChannel channel){
        return Optional.ofNullable(channels.get(channel.getIdLong()));
    }
}
