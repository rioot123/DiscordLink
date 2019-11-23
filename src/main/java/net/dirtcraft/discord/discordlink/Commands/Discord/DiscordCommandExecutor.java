package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface DiscordCommandExecutor {
    void execute(Member member, String[] command, MessageReceivedEvent event) throws DiscordCommandException;
}
