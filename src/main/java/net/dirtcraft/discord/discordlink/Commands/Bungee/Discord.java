package net.dirtcraft.discord.discordlink.Commands.Bungee;

import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

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
