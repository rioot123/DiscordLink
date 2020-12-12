package net.dirtcraft.discord.discordlink.Commands.Bukkit.Prefix;

import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.Default.PexProvider;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class Group implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Optional<PlatformUser> other = args.length == 0? Optional.empty() : PlatformUtils.getPlayerOffline(args[0]);
        Optional<String> group = Optional.of(other.isPresent()? 1 : 0).filter(i->args.length > i).map(i->args[i]);
        if (!(sender instanceof Player) && !other.isPresent()) sender.sendMessage("You must specify a player target!");
        else if (!group.isPresent()) usage(sender, other.orElse(PlatformUtils.getPlayerOffline((OfflinePlayer) sender)));
        else command(sender, other.orElse(PlatformUtils.getPlayerOffline((OfflinePlayer) sender)), group.get());
        return true;
    }

    public void usage(CommandSender source, PlatformUser target){
        AtomicBoolean flip = new AtomicBoolean(false);
        Map<String, String> groups = ((PexProvider) PexProvider.INSTANCE).getUserGroupPrefixMap(target);
        if (!(source instanceof Player)){
            groups.keySet().forEach(source::sendMessage);
        } else {
            groups.entrySet().stream().map(g -> {
                boolean flipper = flip.get();
                flip.set(!flipper);
                char color = flipper ? '3' : 'b';
                TextComponent x = new TextComponent(TextComponent.fromLegacyText("&" + color + g.getKey()));
                x.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(g.getValue())));
                x.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/prefix group " + target.getName() + " " + g.getKey()));
                return x;
            }).forEach(m -> ((Player) source).spigot().sendMessage(m));
        }
    }

    public void command(CommandSender source, PlatformUser target, String group){
        Optional<String> prefix = ((PexProvider) PexProvider.INSTANCE).getGroupPrefix(group);
        if (source instanceof Player && !prefix.isPresent()){
            ((Player)source).spigot().sendMessage(TextComponent.fromLegacyText("&cThe specified group does not have a prefix or does not exist."));
        } else if (!prefix.isPresent()) {
            source.sendMessage("The specified group does not have a prefix or does not exist.");
        } else if (source instanceof Player && !target.hasPermission("group."+group)){
            ((Player)source).spigot().sendMessage(TextComponent.fromLegacyText("&cThe target is not a member of that group."));
        } else if (!target.hasPermission("group."+group)){
            source.sendMessage("The target is not a member of that group.");
        } else {
            PermissionUtils.INSTANCE.setPlayerPrefix(getSource(source), target, prefix.get());
        }

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
