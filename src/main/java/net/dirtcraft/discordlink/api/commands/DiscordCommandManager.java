package net.dirtcraft.discordlink.api.commands;

import net.dirtcraft.discordlink.commands.DiscordCommandImpl;

import java.util.Map;

public interface DiscordCommandManager {
    void register(DiscordCommand command, String... alias);

    Map<String, DiscordCommandImpl> getCommandMap();
}
