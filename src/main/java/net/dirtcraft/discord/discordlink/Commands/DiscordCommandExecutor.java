package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public interface DiscordCommandExecutor {
    void execute(GuildMember source, List<String> args, MessageReceivedEvent event) throws DiscordCommandException;
}
