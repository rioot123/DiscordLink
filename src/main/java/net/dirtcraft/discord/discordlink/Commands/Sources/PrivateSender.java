package net.dirtcraft.discord.discordlink.Commands.Sources;

import net.dirtcraft.discord.discordlink.API.DiscordSource;
import org.spongepowered.api.text.Text;

public class PrivateSender extends WrappedConsole {
    private final DiscordSource member;

    public PrivateSender(DiscordSource member, String command) {
        this.member = member;
        member.sendMessage("Command sent: " + command + "\n");
    }

    @Override
    public void sendMessage(Text message) {
        final String output = message.toPlain();
        if (output.length() > 1950) return;
        member.sendMessage(">>> " + message.toPlain());
    }

    @Override
    public void sendMessages(Iterable<Text> messages) {
        StringBuilder output = new StringBuilder();
        for (Text message : messages) {
            final String messagePlain = message.toPlain();
            if (output.length() + messagePlain.length() > 1800) {
                member.sendMessage(">>> " + output.toString());
                output = new StringBuilder();
            }
            else {
                output.append(messagePlain);
                output.append("\n");
            }
        }
        if (output.length() > 0) member.sendMessage(">>> " + output.toString());
    }

    @Override
    public void sendMessages(Text... messages) {
        StringBuilder output = new StringBuilder();
        for (Text message : messages) {
            final String messagePlain = message.toPlain();
            if (output.length() + messagePlain.length() > 1800) {
                member.sendMessage(">>> " + output.toString());
                output = new StringBuilder();
            }
            else {
                output.append(messagePlain);
                output.append("\n");
            }
        }
        if (output.length() > 0) member.sendMessage(">>> " + output.toString());
    }

}