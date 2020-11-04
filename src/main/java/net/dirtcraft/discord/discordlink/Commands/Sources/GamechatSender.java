package net.dirtcraft.discord.discordlink.Commands.Sources;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Utility.Utility;

public class GamechatSender extends WrappedConsole implements ScheduledSender {
    private MessageSource member;
    private String command;

    public GamechatSender(MessageSource member, String command) {
        this.member = member;
        this.command = command;
    }

    @Override
    public void sendDiscordResponse(String message) {
        if (message.length() > getCharLimit()) return;
        member.getGamechat().sendMessage(
                Utility.embedBuilder().addField("__Command__ \"**/" + command.toLowerCase() + "**\" __Sent__", message, false)
                        .setFooter("Sent By: " + member.getUser().getAsTag(), member.getUser().getAvatarUrl())
                        .build());
    }
}