package net.dirtcraft.discordlink.api.commands;

import net.dirtcraft.discordlink.common.users.MessageSourceImpl;

import java.util.Map;

public interface DiscordCommandManager extends DiscordCommandTree {
    void process(MessageSourceImpl member, String args);
}
