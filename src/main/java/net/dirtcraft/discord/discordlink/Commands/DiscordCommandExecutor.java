package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;

import java.util.List;
import java.util.Optional;

public interface DiscordCommandExecutor {
    void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException;

    default Optional<PlatformUser> parseIdentifier(String s){
        if (s.matches("<?@?!?(\\d+)>?")){
            long discordId = Long.parseLong(s.replaceAll("<?@?!?(\\d+)>?", "$1"));
            return GuildMember.fromDiscordId(discordId)
                    .flatMap(GuildMember::getPlayerData);
        } else {
            return PlatformUtils.getPlayerOffline(s);
        }
    }
}