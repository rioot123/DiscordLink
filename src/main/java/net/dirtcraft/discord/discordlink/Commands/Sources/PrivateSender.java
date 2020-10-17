package net.dirtcraft.discord.discordlink.Commands.Sources;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import org.spongepowered.api.text.Text;

public class PrivateSender extends WrappedConsole implements ScheduledSender {
    private final GuildMember member;

    public PrivateSender(GuildMember member, String command) {
        this.member = member;
        member.sendMessage("Command sent: " + command + "\n");
    }

    @Override
    public void dispatch(String message) {
        if (message.length() > 1950) return;
        member.sendMessage(message);
    }
}