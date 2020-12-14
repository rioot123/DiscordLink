package net.dirtcraft.discord.discordlink.Commands.Discord.Mute;

import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Utility.Utility;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mute implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        GuildMember target = parseDiscord(args).orElseThrow(()->new DiscordCommandException("Discord user not specified."));
        Timestamp expires = getExpireDate(args);
        String reason = args.isEmpty()? "None given.": String.join(" ", args);
        Utility.setRoleIfAbsent(Channels.getGuild(), target, Roles.MUTED);
        DiscordLink.getInstance()
                .getStorage()
                .registerMute(source.getIdLong(), target, expires, reason);
        String t = expires.toString();
        target.sendMessage("You have been muted by " + source.getEffectiveName() + " for \"" + reason + "\". Feel free to make an appeal in <#576254302490722306>.");
    }

    public Timestamp getExpireDate(List<String> args){
        if (args.isEmpty() || !args.get(0).matches("(?i)(\\d+)([smhdwy]|mo)")) return null;
        Pattern pattern = Pattern.compile("(?i)(\\d+)([smhdwy]|mo)");
        Matcher matcher = pattern.matcher(args.remove(0));
        String duration = matcher.group(1);
        String unit = matcher.group(2);
        return Timestamp.from(Instant.now().plus(Long.parseLong(duration), getUnit(unit)));
    }

    public ChronoUnit getUnit(String input){
        switch (input){
            case "s": return ChronoUnit.SECONDS;
            case "m": return ChronoUnit.MINUTES;
            case "h": return ChronoUnit.HOURS;
            case "w": return ChronoUnit.WEEKS;
            case "mo": return ChronoUnit.MONTHS;
            case "y": return ChronoUnit.YEARS;
            default: return ChronoUnit.FOREVER;
        }
    }
}
