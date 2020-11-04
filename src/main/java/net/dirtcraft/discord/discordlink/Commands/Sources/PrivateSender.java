package net.dirtcraft.discord.discordlink.Commands.Sources;

import net.dirtcraft.discord.discordlink.API.MessageSource;

public class PrivateSender extends WrappedConsole implements ScheduledSender {
    private final MessageSource member;

    public PrivateSender(MessageSource member, String command) {
        this.member = member;
        member.sendMessage(String.format("Command sent: \"%s\" @ #%s\n", command, member.getChannelLink()));
    }

    @Override
    public void sendDiscordResponse(String message) {
        if (message.length() > getCharLimit()) return;
        member.sendMessage("``" + message + "``");
    }

    @Override
    public int getCharLimit(){
        return 1996;
    }

    @Override
    public boolean sanitise(){
        return false;
    }
}