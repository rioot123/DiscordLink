package net.dirtcraft.discord.discordlink.Commands.Sources;

import java.util.Collection;

public interface ScheduledSender {
    void dispatch(String message);
}
