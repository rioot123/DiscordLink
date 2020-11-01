package net.dirtcraft.discord.discordlink.Commands.Bukkit;

import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Discord implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        String message = Utility.formatColourCodes(PluginConfiguration.Format.discordInvite.replace("{url}", PluginConfiguration.Main.DISCORD_INVITE));
        commandSender.sendMessage(message);
        return true;
    }
}
