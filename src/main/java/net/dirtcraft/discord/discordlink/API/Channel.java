package net.dirtcraft.discord.discordlink.API;

import net.dirtcraft.discord.discordlink.Commands.Sources.DiscordResponder;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Channel implements DiscordResponder {
    private final long channel;
    private final boolean isPrivate;

    public Channel(long channel){
        this.channel = channel;
        this.isPrivate = false;
    }

    public Channel(long channel, boolean isPrivate){
        this.channel = channel;
        this.isPrivate = isPrivate;
    }

    public MessageChannel getChannel(){
        final JDA jda = DiscordLink.getJDA();
        if (channel < 1) return null;
        return isPrivate? jda.getPrivateChannelById(channel) : jda.getTextChannelById(channel);
    }

    public long getId(){
        return channel;
    }

    public CompletableFuture<Message> sendMessage(MessageEmbed embed) {
        MessageChannel channel = getChannel();
        if (channel instanceof TextChannel) return channel.sendMessage(embed).submit();
        else return CompletableFuture.supplyAsync(()->null);
    }

    public CompletableFuture<Message> sendMessage(String message) {
        MessageChannel channel = getChannel();
        if (channel instanceof TextChannel) return channel.sendMessage(message).submit();
        else return CompletableFuture.supplyAsync(()->null);
    }

    public CompletableFuture<Message> sendMessage(String message, int delay){
        return sendMessage(message).whenComplete((msg, ex)-> msg.delete().queueAfter(delay, TimeUnit.SECONDS));
    }

    public CompletableFuture<Message> sendMessage(MessageEmbed message, int delay){
        return sendMessage(message).whenComplete((msg, ex)-> msg.delete().queueAfter(delay, TimeUnit.SECONDS));
    }

    public void sendMessage(MessageSource source, String header, String message) {
        header = header == null? "" : header;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                .setFooter("Requested By: " + source.getUser().getAsTag(), source.getUser().getAvatarUrl())
                .build();
        sendMessage(embed);
    }

    public void sendMessage(String header, String message) {
        header = header == null? "" : header;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                .build();
        sendMessage(embed);
    }

    public void sendLog(String header, String message) {
        header = header == null? "" : header;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                .setFooter("modpack: " + SpongeDiscordLib.getServerName())
                .build();
        sendMessage(embed);
    }

    public DiscordResponder getChatResponder(){
        return this;
    }

    public boolean isValid(){
        return channel > 0;
    }

    public void setName(String name){
        GuildChannel channel = Channels.getGuild().getGuildChannelById(this.channel);
        if (channel != null) channel.getManager()
                .setName(name)
                .submit();
    }

    @Override
    public boolean equals(Object other){
        return other instanceof Channel &&
                ((Channel) other).getId() == getId();
    }

    @Override
    public void sendDiscordResponse(String message) {
        if (message.length() > getCharLimit()) return;
        sendMessage(message);
    }

    @Override
    public int getCharLimit(){
        return 1996;
    }

    @Override
    public boolean sanitise(){
        return false;
    }

    public DiscordResponder getCommandResponder(MessageSource member, String command){
        return new DiscordResponder() {
            @Override
            public void sendDiscordResponse(String message) {
                if (message.length() > getCharLimit()) return;
                sendMessage(toEmbed(message));
            }

            private MessageEmbed toEmbed(String s){
                return Utility.embedBuilder().addField("__Command__ \"**/" + command.toLowerCase() + "**\" __Sent__", s, false)
                        .setFooter("Sent By: " + member.getUser().getAsTag(), member.getUser().getAvatarUrl())
                        .build();
            }

            public int getCharLimit(){
                return 1024;
            }

            public boolean sanitise(){
                return true;
            }
        };
    }
}
