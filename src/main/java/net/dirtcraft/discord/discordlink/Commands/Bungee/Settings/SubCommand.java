package net.dirtcraft.discord.discordlink.Commands.Bungee.Settings;

import net.md_5.bungee.api.CommandSender;

import java.util.List;

public interface SubCommand {
    public void execute(CommandSender sender, List<String> args);
}
