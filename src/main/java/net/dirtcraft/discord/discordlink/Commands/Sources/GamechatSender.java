package net.dirtcraft.discord.discordlink.Commands.Sources;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import org.spongepowered.api.text.Text;

public class GamechatSender extends WrappedConsole implements ScheduledSender {
    private GuildMember member;
    private String command;

    public GamechatSender(GuildMember member, String command) {
        this.member = member;
        this.command = command;
    }

    @Override
    public void dispatch(String message) {
        if (message.length() > 1800) return;
        GameChat.sendMessage(
                Utility.embedBuilder().addField("__Command__ \"**/" + command.toLowerCase() + "**\" __Sent__", message, false)
                        .setFooter("Sent By: " + member.getUser().getAsTag(), member.getUser().getAvatarUrl())
                        .build());
    }
}