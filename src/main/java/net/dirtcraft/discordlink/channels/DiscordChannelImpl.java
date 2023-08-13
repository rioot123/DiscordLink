// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.channels;

import net.dirtcraft.discordlink.commands.sources.DiscordResponder;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.discordlink.users.GuildMember;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Message;
import java.util.concurrent.CompletableFuture;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.JDA;
import net.dirtcraft.spongediscordlib.channels.DiscordChannel;

public class DiscordChannelImpl implements DiscordChannel
{
    private final JDA jda;
    private final long channel;
    private final boolean isPrivate;
    
    DiscordChannelImpl(final JDA jda, final long channel) {
        this.jda = jda;
        this.channel = channel;
        this.isPrivate = false;
    }
    
    DiscordChannelImpl(final JDA jda, final long channel, final boolean isPrivate) {
        this.jda = jda;
        this.channel = channel;
        this.isPrivate = isPrivate;
    }
    
    public MessageChannel getChannel() {
        return (MessageChannel)(this.isPrivate ? this.jda.getPrivateChannelById(this.channel) : this.jda.getTextChannelById(this.channel));
    }
    
    public long getId() {
        return this.channel;
    }
    
    public CompletableFuture<Message> sendMessage(final MessageEmbed embed) {
        return (CompletableFuture<Message>)this.getChannel().sendMessage(embed).submit();
    }
    
    public CompletableFuture<Message> sendMessage(final String message) {
        return (CompletableFuture<Message>)this.getChannel().sendMessage((CharSequence)message).submit();
    }
    
    public CompletableFuture<Message> sendMessage(final String message, final int delay) {
        return this.sendMessage(message).whenComplete((msg, ex) -> msg.delete().queueAfter((long)delay, TimeUnit.SECONDS));
    }
    
    public CompletableFuture<Message> sendMessage(final MessageEmbed message, final int delay) {
        return this.sendMessage(message).whenComplete((msg, ex) -> msg.delete().queueAfter((long)delay, TimeUnit.SECONDS));
    }
    
    public void sendMessage(final DiscordMember source, String header, final String message) {
        if (!(source instanceof GuildMember)) {
            return;
        }
        final GuildMember member = (GuildMember)source;
        header = ((header == null) ? "" : header);
        final MessageEmbed embed = Utility.embedBuilder().addField(header, message, false).setFooter("Requested By: " + member.getUser().getAsTag(), member.getUser().getAvatarUrl()).build();
        this.sendMessage(embed);
    }
    
    public void sendMessage(String header, final String message) {
        header = ((header == null) ? "" : header);
        final MessageEmbed embed = Utility.embedBuilder().addField(header, message, false).build();
        this.sendMessage(embed);
    }
    
    public DiscordResponder getCommandResponder(final GuildMember member, final String command) {
        return new DiscordResponder() {
            @Override
            public void sendDiscordResponse(final String message) {
                if (message.length() > this.getCharLimit()) {
                    return;
                }
                DiscordChannelImpl.this.sendMessage(this.toEmbed(message));
            }
            
            private MessageEmbed toEmbed(final String s) {
                return Utility.embedBuilder().addField("__Command__ \"**/" + command.toLowerCase() + "**\" __Sent__", s, false).setFooter("Sent By: " + member.getUser().getAsTag(), member.getUser().getAvatarUrl()).build();
            }
            
            @Override
            public int getCharLimit() {
                return 1024;
            }
            
            @Override
            public boolean sanitise() {
                return true;
            }
        };
    }
    
    public DiscordResponder getChatResponder() {
        return new DiscordResponder() {
            @Override
            public void sendDiscordResponse(final String message) {
                if (message.length() > this.getCharLimit()) {
                    return;
                }
                DiscordChannelImpl.this.sendMessage(message);
            }
            
            @Override
            public int getCharLimit() {
                return 1996;
            }
            
            @Override
            public boolean sanitise() {
                return false;
            }
        };
    }
}
