package net.dirtcraft.discord.discordlink.Commands.Sources;

import net.dirtcraft.discord.discordlink.API.GuildMember;

public class GamechatSender extends WrappedConsole implements ScheduledSender.Public {
    private GuildMember member;
    private String command;

    public GamechatSender(GuildMember member, String command) {
        this.member = member;
        this.command = command;
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