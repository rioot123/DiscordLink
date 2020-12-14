package net.dirtcraft.discord.discordlink.Commands.Discord.Mute;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public class MuteInfo implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {

    }

    public String getDuration(Timestamp date){
        Timestamp now = Timestamp.from(Instant.now());
        if (date == null) return "Is permanent";
        else if (!date.after(now)) return "Has already expired";
        long msRemaining = date.getTime() - now.getTime();

        long seconds = msRemaining / 1000;
        long minutes = seconds / 60;

        if (minutes == 0) return "Will expire in " + seconds + " seconds.";
        long hours = minutes / 60;
        seconds = seconds % 60;

        if (hours == 0) return "Will expire in " + minutes + " minutes, " + seconds + " seconds.";
        long days = hours / 24;
        minutes = minutes % 60;

        if (days == 0) return "Will expire in " + hours + " hours, " + minutes + " minutes.";
        hours = hours % 24;
        return "Will expire in " + days + " days, " + hours + " hours.";
    }
}