package net.dirtcraft.discord.discordlink.Commands.Bukkit.Prefix;

import net.dirtcraft.discord.discordlink.Commands.Bukkit.ThermosSubCommand;
import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.Storage.Permission;
import net.dirtcraft.discord.discordlink.Storage.Settings;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.Pex.PexProvider;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Group extends ThermosSubCommand {
    public Group(){
        super(Permission.PREFIX_GROUP);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, List<String> args) {
        Optional<PlatformUser> other = args.isEmpty()? Optional.empty() : PlatformUtils.getPlayerOffline(args.get(0));
        Optional<String> group = Optional.of(other.isPresent()? 1 : 0).filter(i->args.size() > i).map(args::get);
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
                TextComponent x = new TextComponent(Utility.format("&" + color + g.getKey()));
                x.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Utility.format(g.getValue())));
                x.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/prefix group " + target.getName().get() + " " + g.getKey()));
                return x;
            }).forEach(m -> ((Player) source).spigot().sendMessage(m));
        }
    }

    public void command(CommandSender source, PlatformUser target, String group){
        Optional<String> prefix = ((PexProvider) PexProvider.INSTANCE).getGroupPrefix(group);
        if (source instanceof Player && !prefix.isPresent()){
            ((Player)source).spigot().sendMessage(Utility.format("&cThe specified group does not have a prefix or does not exist."));
        } else if (!prefix.isPresent()) {
            source.sendMessage("The specified group does not have a prefix or does not exist.");
        } else if (source instanceof Player && !((PexProvider)PexProvider.INSTANCE).isInGroup(target, group)){
            ((Player)source).spigot().sendMessage(Utility.format("&cThe target is not a member of that group."));
        } else if (!((PexProvider)PexProvider.INSTANCE).isInGroup(target, group)){
            source.sendMessage("The target is not a member of that group.");
        } else {
            final Map.Entry<String,String> indicatorSet = Settings.STAFF_PREFIXES.entrySet().stream()
                    .filter(p->target.hasPermission(p.getKey()))
                    .findFirst()
                    .orElse(null);
            boolean applyIndicator = indicatorSet != null && !((PexProvider)PexProvider.INSTANCE).groupHasPermission(group, indicatorSet.getKey());
            if (applyIndicator){
                String indicator = prefix.get().replaceAll("(?i)^.*?(([§&][0-9a-frlonm])+) *\\[.*", "$1");
                if (indicator.equalsIgnoreCase(prefix.get())) indicator = "&f";
                indicator = String.format("%s[%s%s]", indicator, indicatorSet.getValue(), indicator);
                List<String> bits = new ArrayList<>(Arrays.asList(prefix.get().split(" ")));
                String carat = bits.isEmpty()? "" : bits.remove(0) + " ";
                String rest = String.join(" ", bits);
                PermissionUtils.INSTANCE.setPlayerPrefix(getSource(source), target, carat + indicator + rest);
            } else PermissionUtils.INSTANCE.setPlayerPrefix(getSource(source), target, prefix.get());
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
