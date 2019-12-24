package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.DiscordSource;
import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Help implements DiscordCommandExecutor {
    @Override
    public void execute(DiscordSource source, String[] args, MessageReceivedEvent event) throws DiscordCommandException {
        StringBuilder result = new StringBuilder("The following commands are available:\n");
        DiscordLink.getCommandManager().getCommandMap().forEach((alias, cmd)->{
            if (!source.hasPermission(cmd)) return;
            result.append(" **-** ")
                    .append(PluginConfiguration.Main.botPrefix)
                    .append(alias)
                    .append("\n");
        });
        GameChat.sendMessage(result.toString());
    }
}
