package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.entities.Member;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Unmute implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        if (args.isEmpty() || !args.get(0).matches("<?@?!?(\\d+)>?")) throw new DiscordCommandException("You must specify a discord user!");
        Pattern pattern = Pattern.compile("<?@?!?(\\d+)>?");
        Matcher matcher = pattern.matcher(args.get(0));
        long id = Long.parseLong(matcher.group(0));
        Member member = Channels.getGuild().retrieveMemberById(id).complete();

        if (member == null) throw new DiscordCommandException("Invalid member specified!");
        GuildMember guildMember = new GuildMember(member);
        Utility.removeRoleIfPresent(Channels.getGuild(), guildMember, Roles.MUTED);
    }
}
