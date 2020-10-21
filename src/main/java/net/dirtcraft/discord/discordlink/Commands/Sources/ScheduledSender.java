package net.dirtcraft.discord.discordlink.Commands.Sources;

public interface ScheduledSender {
    void sendDiscordResponse(String message);

    default int getCharLimit(){
        return 1024;
    }
}
