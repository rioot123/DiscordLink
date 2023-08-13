// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.users;

import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dirtcraft.spongediscordlib.channels.DiscordChannel;
import net.dv8tion.jda.api.utils.AttachmentOption;
import java.io.File;
import net.dirtcraft.discordlink.utility.Utility;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dirtcraft.discordlink.commands.sources.ConsoleSource;
import net.dirtcraft.spongediscordlib.users.roles.RoleManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dirtcraft.discordlink.users.discord.RoleManagerImpl;
import net.dv8tion.jda.api.entities.Member;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.channels.DiscordChannelImpl;
import net.dv8tion.jda.api.entities.Message;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.discordlink.commands.sources.DiscordResponder;

public class MessageSourceImpl extends GuildMember implements DiscordResponder, MessageSource
{
    private final Message message;
    private final DiscordChannelImpl source;
    
    public MessageSourceImpl(final Database storage, final Member author, final DiscordChannelImpl discordChannel, final RoleManagerImpl roleManager, final MessageReceivedEvent event) {
        super(storage, roleManager, author);
        this.message = event.getMessage();
        this.source = discordChannel;
    }
    
    public Message getMessage() {
        return this.message;
    }
    
    @Override
    public void sendDiscordResponse(final String message) {
        if (message.length() > this.getCharLimit()) {
            return;
        }
        this.sendPrivateMessage("``" + message + "``");
    }
    
    @Override
    public int getCharLimit() {
        return 1996;
    }
    
    @Override
    public boolean sanitise() {
        return false;
    }
    
    @Override
    public DiscordChannelImpl getPrivateChannel() {
        if (this.isPrivateMessage()) {
            return this.source;
        }
        return super.getPrivateChannel();
    }
    
    public DiscordChannelImpl getChannel() {
        return this.source;
    }
    
    public ConsoleSource getCommandSource(final String command) {
        final DiscordResponder responder = this.source.getCommandResponder(this, command);
        return DiscordResponder.getSender(responder);
    }
    
    public boolean isPrivateMessage() {
        return this.source.getChannel().getType() == ChannelType.PRIVATE;
    }
    
    public void sendCommandResponse(final String message) {
        this.source.sendMessage(message);
    }
    
    public void sendCommandResponse(final MessageEmbed message) {
        this.source.sendMessage(message);
    }
    
    public void sendCommandResponse(final String message, final int delay) {
        this.source.sendMessage(message, delay);
    }
    
    public void sendCommandResponse(final MessageEmbed message, final int delay) {
        this.source.sendMessage(message, delay);
    }
    
    public void sendCommandResponse(String header, final String message) {
        header = ((header == null) ? "" : header);
        final MessageEmbed embed = Utility.embedBuilder().addField(header, message, false).setFooter("Requested By: " + this.getUser().getAsTag(), this.getUser().getAvatarUrl()).build();
        this.sendCommandResponse(embed);
    }
    
    public void sendCommandResponse(String header, String message, final int duration) {
        header = ((header == null) ? "" : header);
        message = ((message == null) ? "" : message);
        final MessageEmbed embed = Utility.embedBuilder().addField(header, message, false).setFooter(this.getUser().getAsTag(), this.getUser().getAvatarUrl()).build();
        this.sendCommandResponse(embed, duration);
    }
    
    public void sendPrivateFile(final File file) {
        this.getUser().openPrivateChannel().flatMap(a -> a.sendFile(file, new AttachmentOption[0])).submit();
    }
}
