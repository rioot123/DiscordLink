package net.dirtcraft.discordlink.api.commands;

import java.util.Map;

public interface DiscordCommandManager {
    void register(DiscordCommand command, String... alias);

    Map<String, ? extends DiscordCommand> getCommandMap();
}
