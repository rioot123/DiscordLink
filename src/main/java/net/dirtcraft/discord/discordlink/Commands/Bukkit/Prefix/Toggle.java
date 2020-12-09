package net.dirtcraft.discord.discordlink.Commands.Bukkit.Prefix;

import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.Storage.Permission;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class Toggle implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        final Optional<PlatformUser> target = strings.length > 0 ? PlatformUtils.getPlayerOffline(strings[0]): PlatformUtils.getPlayerOffline(commandSender); //args.<User>getOne("Target").orElseThrow(()->new CommandException(Text.of("§cYou must specify a target.")));
        if (!target.isPresent()) {
            commandSender.sendMessage("You must specify a valid player.");
        } else if ((!(commandSender instanceof Player) || !target.map(PlatformUser::getUUID).get().equals(((Player) commandSender).getUniqueId())) && !commandSender.hasPermission(Permission.PREFIX_OTHERS)){
            commandSender.sendMessage("You do not have permission to set other players prefixes.");
        } else if (target.get().hasPermission(Permission.ROLES_DONOR)) {
            commandSender.sendMessage("The target needs to be a donor to toggle their prefix.");
        } else {
            Optional<String> prefix = target.map(PlatformUser::getUUID).flatMap(PermissionUtils.INSTANCE::getPrefix);
            prefix.ifPresent(pre -> {
                if (pre.matches("^(?i)([§&][0-9a-frlonm]){1,5}».*$")) pre = pre.replaceFirst("»", "✯");
                else pre = pre.replaceFirst("✯", "»");
                PermissionUtils.INSTANCE.setPlayerPrefix(getSource(commandSender), target.get(), pre);
            });
        }
        return true;
    }

    private ConsoleSource getSource(CommandSender source){
        return new ConsoleSource(){
            @Override
            public void sendMessage(String message) {
                source.sendMessage(message);
            }
        };
    }
}