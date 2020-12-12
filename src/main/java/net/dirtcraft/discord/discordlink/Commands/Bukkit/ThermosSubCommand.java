package net.dirtcraft.discord.discordlink.Commands.Bukkit;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class ThermosSubCommand {
    protected String permission;

    public ThermosSubCommand(){
        this.permission = null;
    }

    public abstract boolean onCommand(CommandSender sender, List<String> strings);

    public ThermosSubCommand(String permission){
        this.permission = permission;
    }

    public boolean hasPermission(CommandSender sender){
        return permission == null || sender.hasPermission(permission);
    }
}
