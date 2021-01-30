package net.dirtcraft.discordlink.api.channels;

public interface LogChannel extends DiscordChannel {
    void sendLog(String header, String message);
}
