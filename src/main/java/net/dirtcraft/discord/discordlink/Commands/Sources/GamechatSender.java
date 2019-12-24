package net.dirtcraft.discord.discordlink.Commands.Sources;

import net.dirtcraft.discord.discordlink.API.DiscordSource;
import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.Utility;
import org.spongepowered.api.text.Text;

public class GamechatSender extends WrappedConsole {
    private DiscordSource member;
    private String command;

    public GamechatSender(DiscordSource member, String command) {
        this.member = member;
        this.command = command;
    }

    @Override
    public void sendMessage(Text message) {
        String plain = message.toPlain();
        if ("".equals(plain) || plain.trim().isEmpty()) return;
        GameChat.sendMessage(
                Utility.embedBuilder().addField("__Command__ \"**/" + command.toLowerCase() + "**\" __Sent__", plain, false)
                        .setFooter("Sent By: " + member.getUser().getAsTag(), member.getUser().getAvatarUrl())
                        .build());
    }

    @Override
    public void sendMessages(Iterable<Text> messages) {
        Text output = null;
        for (Text message : messages) {
            if(output == null) output = message;
            else output = output.concat(Text.of("\n")).concat(message);
        }
        if (output != null) this.sendMessage(output);
    }

    @Override
    public void sendMessages(Text... messages) {
        Text output = null;
        for (Text message : messages) {
            if(output == null) output = message;
            else output = output.concat(Text.of("\n")).concat(message);
        }
        if (output != null) this.sendMessage(output);
    }

}