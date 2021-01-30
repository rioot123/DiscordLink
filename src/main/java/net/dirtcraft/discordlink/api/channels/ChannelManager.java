package net.dirtcraft.discordlink.api.channels;

public interface ChannelManager {

    GameChatChannel getGameChat();

    LogChannel getLogChannel();

    DiscordChannel getChannel(long channel);

    DiscordChannel getChannel(long channel, boolean isPrivate);
}
