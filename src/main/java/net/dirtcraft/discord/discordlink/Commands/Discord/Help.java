package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class Help implements DiscordCommandExecutor {
    @Override
    public void execute(GuildMember source, List<String> args, MessageReceivedEvent event) throws DiscordCommandException {
        StringBuilder result = new StringBuilder("The following commands are available:\n");
        DiscordLink.getDiscordCommandManager().getCommandMap().forEach((alias, cmd)->{
            if (!cmd.hasPermission(source)) return;
            result.append(" **-** ")
                    .append(PluginConfiguration.Main.botPrefix)
                    .append(alias)
                    .append("\n");
        });
        GameChat.sendMessage(result.toString());
    }
}
