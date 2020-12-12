package net.dirtcraft.discord.discordlink.Commands.Bukkit.Prefix;

import net.dirtcraft.discord.discordlink.Commands.Bukkit.ThermosSubCommand;
import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Storage.Permission;
import net.dirtcraft.discord.discordlink.Storage.Settings;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class Test extends ThermosSubCommand {
    public Test(){
        super(Permission.PREFIX_TEST);
    }

    private final java.util.Set<String> forbidden = new HashSet<>(Arrays.asList("staff", "helper", "mod", "moderator", "admin", "manager", "owner"));

    public boolean onCommand(CommandSender commandSender, List<String> strings){
        try {
            doCommand(commandSender, strings);
            return true;
        } catch (DiscordCommandException e){
            if (commandSender instanceof Player) ((Player) commandSender).spigot().sendMessage(Utility.format("&c" + e.getMessage()));
            else commandSender.sendMessage(Utility.stripColorCodes(e.getMessage()));
            return false;
        }
    }

    public void doCommand(CommandSender commandSender, List<String> args) throws DiscordCommandException {
        if (args.isEmpty()) throw new DiscordCommandException("Prefix not specified.");
        Optional<PlatformUser> platformUser = PlatformUtils.getPlayerOffline(args.get(0));
        Optional<String> caratColor = Optional.empty();
        Optional<String> bracketColor = Optional.empty();
        ListIterator<String> argsIter = args.listIterator();
        boolean applyStaffIndicator = true;
        while (argsIter.hasNext()) {
            String arg = argsIter.next();
            if (arg.matches("--?a")) {
                if (!commandSender.hasPermission(Permission.PREFIX_ARROW)) throw new DiscordCommandException("You do not have permission to use the arrow color flag.");
                if (!argsIter.hasNext()) throw new DiscordCommandException("To use the arrow color flag, you must specify a color code.");
                argsIter.remove();
                String color = argsIter.next();
                argsIter.remove();
                if (isNonColor(color)) throw new DiscordCommandException("To use the arrow color flag, you must specify only a color code.");
                caratColor = Optional.of(color);
            } else if (arg.matches("--?c")) {
                if (!commandSender.hasPermission(Permission.PREFIX_BRACKETS)) throw new DiscordCommandException("You do not have permission to use the bracket color flag.");
                if (!argsIter.hasNext()) throw new DiscordCommandException("To use the bracket color flag, you must specify a color code.");
                argsIter.remove();
                String color = argsIter.next();
                argsIter.remove();
                if (isNonColor(color)) throw new DiscordCommandException("To use the bracket color flag, you must specify only a color code.");
                bracketColor = Optional.of(color);
            } else if (arg.matches("--?s")){
                if (!commandSender.hasPermission(Permission.PREFIX_INDICATOR)) throw new DiscordCommandException("You do not have permission disable the staff indicator.");
                argsIter.remove();
                applyStaffIndicator = false;
            }
        }

        if (platformUser.isPresent() && args.size() == 0) throw new DiscordCommandException("Prefix not specified.");
        else if (platformUser.isPresent()) args.remove(0);
        else if (!(commandSender instanceof Player)) throw new DiscordCommandException("Player not specified.");

        PlatformUser target = platformUser.orElse(PlatformUtils.getPlayerOffline((OfflinePlayer) commandSender));
        String carat = getChevron(target, caratColor.orElse("&a&l"));
        String bracket = bracketColor.orElse("&7");
        String prefix = String.join(" ", args);
        if (prefix.length() > 20 && !commandSender.hasPermission(Permission.PREFIX_LONG)) throw new DiscordCommandException("The prefix specified is too long.");
        if (isNotAllowed(prefix) && !commandSender.hasPermission(Permission.ROLES_STAFF)) throw new DiscordCommandException("That prefix is not allowed.");
        setPrefix(commandSender, target, prefix, carat, bracket, applyStaffIndicator);
    }

    private void setPrefix(CommandSender source, PlatformUser target, String prefix, String chevron, String bracketColor, boolean staffIndicator){
        String rankPrefix = staffIndicator? Settings.STAFF_PREFIXES.entrySet().stream()
                .filter(p->target.hasPermission(p.getKey()))
                .findFirst()
                .map(s->String.format("%s[%s%s]", bracketColor, s.getValue(), bracketColor))
                .orElse(bracketColor): bracketColor;
        prefix = String.format("%s %s[%s%s]&r", chevron, rankPrefix, prefix, bracketColor)
                .replaceAll("\\?\"", "");
        String name = target.getUser().isOnline()? target.getUser().getPlayer().getDisplayName(): target.getUser().getName();
        if (source instanceof Player) ((Player) source).spigot().sendMessage(Utility.format(prefix + " " + name));
        else source.sendMessage(Utility.stripColorCodes(prefix + " " + name));
    }

    private String getChevron(PlatformUser user, String colour){
        final String carat = user.hasPermission(Permission.ROLES_DONOR) ? "&l✯" : "&l»";
        return colour + carat;
    }

    private boolean isNonColor(String input){
        return !input.matches("(?i)([§&][0-9a-frlonm]){1,5}");
    }

    private boolean isNotAllowed(String title){
        String raw = title.replaceAll("(?i)([§&][0-9a-frlonm])", "").toLowerCase();
        return forbidden.contains(raw);
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
