package net.dirtcraft.discord.discordlink.Commands.Sources;

import net.dirtcraft.discord.discordlink.Utility;
import net.dv8tion.jda.core.entities.Member;
import org.spongepowered.api.text.Text;

public class GamechatSender extends WrappedConsole {
    private Member member;
    private String command;

    public GamechatSender(Member member, String command) {
        this.member = member;
        this.command = command;
    }

    @Override
    public void sendMessage(Text message) {
        String plain = message.toPlain();
        if ("".equals(plain) || plain.trim().isEmpty()) return;
        Utility.messageToChannel("embed", null,
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