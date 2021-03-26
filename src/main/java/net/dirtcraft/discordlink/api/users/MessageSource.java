package net.dirtcraft.discordlink.api.users;

import net.dirtcraft.discordlink.api.channels.DiscordChannel;
import net.dirtcraft.discordlink.common.commands.sources.ConsoleSource;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.File;

public interface MessageSource extends DiscordMember{

    Message getMessage();

    DiscordChannel getChannel();

    ConsoleSource getCommandSource(String command);

    boolean isPrivateMessage();

    void sendCommandResponse(String message);

    void sendCommandResponse(MessageEmbed message);

    void sendCommandResponse(String message, int delay);

    void sendCommandResponse(MessageEmbed message, int delay);

    void sendCommandResponse(String header, String message);

    void sendCommandResponse(String header, String message, int duration);

    void sendPrivateFile(File file);

}
