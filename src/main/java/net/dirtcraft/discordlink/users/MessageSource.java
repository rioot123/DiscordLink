package net.dirtcraft.discordlink.users;


import net.dirtcraft.discordlink.commands.sources.ConsoleSource;
import net.dirtcraft.discordlink.commands.sources.DiscordResponder;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.discordlink.channels.DiscordChannelImpl;
import net.dirtcraft.discordlink.channels.ChannelManagerImpl;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;

public class MessageSource extends GuildMember implements DiscordResponder {
    private final Message message;
    private final DiscordChannelImpl source;

    public MessageSource(Database storage, Member author, DiscordChannelImpl discordChannel, MessageReceivedEvent event){
        super(storage, author);
        this.message = event.getMessage();
        this.source = discordChannel;
    }

    public Message getMessage() {
        return message;
    }

    @Override
    public void sendDiscordResponse(String message) {
        if (message.length() > getCharLimit()) return;
        sendMessage("``" + message + "``");
    }

    @Override
    public int getCharLimit(){
        return 1996;
    }

    @Override
    public boolean sanitise(){
        return false;
    }

    public DiscordChannelImpl getChannel(){
        return source;
    }

    public ConsoleSource getCommandSource(String command){
        DiscordResponder responder = source.getCommandResponder(this, command);
        return DiscordResponder.getSender(responder);
    }

    public boolean isPrivateMessage(){
        return source.getChannel().getType() == ChannelType.PRIVATE;
    }

    public void sendCommandResponse(String message){
        source.sendMessage(message);
    }

    public void sendCommandResponse(MessageEmbed message){
        source.sendMessage(message);
    }

    public void sendCommandResponse(String message, int delay){
        source.sendMessage(message, delay);
    }

    public void sendCommandResponse(MessageEmbed message, int delay){
        source.sendMessage(message, delay);
    }

    public void sendCommandResponse(String header, String message) {
        header = header == null? "" : header;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                .setFooter("Requested By: " + getUser().getAsTag(), getUser().getAvatarUrl())
                .build();
        sendCommandResponse(embed);
    }

    public void sendCommandResponse(String header, String message, int duration) {
        header = header == null? "" : header;
        message = message == null? "" : message;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                .setFooter(getUser().getAsTag(), getUser().getAvatarUrl())
                .build();
        sendCommandResponse(embed, duration);
    }

    public void sendPrivateMessage(MessageEmbed message){
        getUser().openPrivateChannel().queue(dm -> dm.sendMessage(message).queue());
    }

    public void sendPrivateFile(File file){
        getUser().openPrivateChannel()
                .flatMap(a->a.sendFile(file))
                .submit();

    }
}
