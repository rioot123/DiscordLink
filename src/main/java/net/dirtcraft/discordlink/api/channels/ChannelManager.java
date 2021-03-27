package net.dirtcraft.discordlink.api.channels;

import net.dv8tion.jda.api.entities.MessageChannel;

public interface ChannelManager {

    GameChatChannel getGameChat();

    LogChannel getLogChannel();

    DiscordChannel getChannel(long channel);

    DiscordChannel getChannel(long channel, boolean isPrivate);

    boolean isGamechat(MessageChannel channel);
}
