package net.dirtcraft.discord.discordlink.Commands.Discord.Mute;

import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Utility.Utility;

import java.util.List;

public class Unmute implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        GuildMember target = parseDiscord(args).orElseThrow(()->new DiscordCommandException("Discord user not specified."));
        Utility.removeRoleIfPresent(target.getIdLong(), Roles.MUTED);
        DiscordLink.getInstance().getStorage().deactivateMute(source.getIdLong(), target.getIdLong());
    }
}