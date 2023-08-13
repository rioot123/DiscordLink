// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord.mute;

import net.dirtcraft.discordlink.storage.tables.Mutes;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.time.Instant;
import java.sql.Timestamp;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.users.UserManagerImpl;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import java.util.function.Function;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.discordlink.DiscordLink;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class Mute implements DiscordCommandExecutor
{
    public void execute(final MessageSource source, final String command, final List<String> args) throws DiscordCommandException {
        final DiscordLink discordLink = DiscordLink.get();
        final UserManagerImpl userManager = discordLink.getUserManager();
        final DiscordMember target = this.removeIfPresent((List)args, (Function)userManager::getMember).orElseThrow(() -> new DiscordCommandException("Discord user not specified."));
        final Database storage = discordLink.getStorage();
        final Timestamp expires = this.getExpireDate(args);
        final String reason = args.isEmpty() ? "None given." : String.join(" ", args);
        target.setRoleIfAbsent(DiscordRoles.MUTED);
        storage.deactivateMute(source.getIdLong(), target.getIdLong());
        storage.registerMute(source.getIdLong(), target, expires, reason);
        final String message = "You have been muted by " + source.getEffectiveName() + " for ```\n" + reason + "\n```\nThe mute " + this.getDuration(expires) + "\nFeel free to make an appeal in <#590388043379376158>.";
        target.sendPrivateMessage(message);
        source.sendCommandResponse(source.getAsMention() + " Mute has been applied to user " + target.getAsMention(), 30);
        storage.hasActiveMute(target.getIdLong()).ifPresent(data -> source.sendCommandResponse(MuteInfo.getInfo(data), 30));
    }
    
    public Timestamp getExpireDate(final List<String> args) {
        if (args.isEmpty() || !args.get(0).matches("(?i)(\\d+)([smhdwy]|mo)")) {
            return null;
        }
        final String base = args.remove(0);
        final String duration = base.replaceAll("(?i)(\\d+)([smhdwy]|mo)", "$1");
        final String unit = base.replaceAll("(?i)(\\d+)([smhdwy]|mo)", "$2");
        return Timestamp.from(Instant.now().plus(Long.parseLong(duration), (TemporalUnit)this.getUnit(unit)));
    }
    
    public ChronoUnit getUnit(final String input) {
        switch (input) {
            case "s": {
                return ChronoUnit.SECONDS;
            }
            case "m": {
                return ChronoUnit.MINUTES;
            }
            case "h": {
                return ChronoUnit.HOURS;
            }
            case "w": {
                return ChronoUnit.WEEKS;
            }
            case "mo": {
                return ChronoUnit.MONTHS;
            }
            case "y": {
                return ChronoUnit.YEARS;
            }
            default: {
                return ChronoUnit.FOREVER;
            }
        }
    }
    
    public String getDuration(final Timestamp date) {
        final Timestamp now = Timestamp.from(Instant.now());
        if (date == null) {
            return "is permanent";
        }
        if (!date.after(now)) {
            return "has already expired";
        }
        final long msRemaining = date.getTime() - now.getTime();
        long seconds = msRemaining / 1000L;
        long minutes = seconds / 60L;
        if (minutes == 0L) {
            return "will expire in " + seconds + " seconds.";
        }
        long hours = minutes / 60L;
        seconds %= 60L;
        if (hours == 0L) {
            return "will expire in " + minutes + " minutes, " + seconds + " seconds.";
        }
        final long days = hours / 24L;
        minutes %= 60L;
        if (days == 0L) {
            return "will expire in " + hours + " hours, " + minutes + " minutes.";
        }
        hours %= 24L;
        return "will expire in " + days + " days, " + hours + " hours.";
    }
}
