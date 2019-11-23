package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.API.PlayerDiscord;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface DiscordCommandExecutor {
    void execute(PlayerDiscord member, String[] command, MessageReceivedEvent event) throws DiscordCommandException;
}
