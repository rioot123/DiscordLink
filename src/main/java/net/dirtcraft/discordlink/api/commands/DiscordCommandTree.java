package net.dirtcraft.discordlink.api.commands;

import net.dirtcraft.discordlink.common.users.MessageSourceImpl;

import java.util.Map;

public interface DiscordCommandTree {
    void register(DiscordCommand command, String... alias);

    Map<String, ? extends DiscordCommand> getCommandMap();
}
