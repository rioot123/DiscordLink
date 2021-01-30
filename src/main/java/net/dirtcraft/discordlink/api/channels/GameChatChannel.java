package net.dirtcraft.discordlink.api.channels;

public interface GameChatChannel extends DiscordChannel {
    void sendPlayerMessage(String prefix, String playerName, String message);
}
