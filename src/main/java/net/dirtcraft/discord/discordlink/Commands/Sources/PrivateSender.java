package net.dirtcraft.discord.discordlink.Commands.Sources;

import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;

public class PrivateSender extends WrappedConsole implements ScheduledSender.Private {
    private final GuildMember member;
    private final String command;

    public PrivateSender(GuildMember member, String command) {
        this.member = member;
        this.command = command;
        member.sendMessage(String.format("Command sent: \"%s\" @ <#%s>\n", command, SpongeDiscordLib.getGamechatChannelID()));
    }

    @Override
    public GuildMember getMember() {
        return member;
    }

    @Override
    public String getCommand() {
        return command;
    }
}