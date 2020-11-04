package net.dirtcraft.discord.discordlink.Commands.Bungee;

import net.dirtcraft.discord.discordlink.API.GameChats;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Database;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.CompletableFuture;

public class Discord extends Command {
    public Discord() {
        super("discord");
    }

    @Override
    @SuppressWarnings("deprecation")
    public void execute(CommandSender commandSender, String[] strings) {
        String message = Utility.formatColourCodes(PluginConfiguration.Format.discordInvite.replace("{url}", PluginConfiguration.Main.DISCORD_INVITE));
        commandSender.sendMessage(message);
    }
}
