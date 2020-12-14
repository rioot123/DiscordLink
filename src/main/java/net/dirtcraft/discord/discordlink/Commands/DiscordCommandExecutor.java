package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.List;
import java.util.Optional;

public interface DiscordCommandExecutor {
    void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException;

    default Optional<GuildMember> parseDiscord(String s){
        if (s.matches("<?@?!?(\\d+)>?")){
            long discordId = Long.parseLong(s.replaceAll("<?@?!?(\\d+)>?", "$1"));
            return Optional.ofNullable(Channels.getGuild())
                    .map(g->g.retrieveMemberById(discordId))
                    .map(RestAction::complete)
                    .map(GuildMember::new);
        } else {
            return Optional.empty();
        }
    }

    default Optional<GuildMember> parseDiscord(List<String> args){
        if (args.isEmpty()) return Optional.empty();
        String s = args.get(0);
        if (s.matches("<?@?!?(\\d+)>?")){
            long discordId = Long.parseLong(s.replaceAll("<?@?!?(\\d+)>?", "$1"));
            Optional<GuildMember> member =  Optional.ofNullable(Channels.getGuild())
                    .map(g->g.retrieveMemberById(discordId))
                    .map(RestAction::complete)
                    .map(GuildMember::new);
            member.ifPresent(x->args.remove(0));
            return member;
        } else {
            return Optional.empty();
        }
    }
}
