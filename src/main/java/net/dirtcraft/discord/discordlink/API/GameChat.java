package net.dirtcraft.discord.discordlink.API;

import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class GameChat {

    final long channel;
    private final long guild;

    GameChat(long channel){
        this.channel = channel;
        this.guild = getChannel().getGuild().getIdLong();
    }


    public TextChannel getChannel(){
        return DiscordLink.getJDA().getTextChannelById(channel);
    }
    public Guild getGuild(){
        return DiscordLink.getJDA().getGuildById(guild);
    }
    public void sendMessage(MessageEmbed embed) {
        getChannel().sendMessage(embed).queue();
    }


    public void sendMessage(String message) {
        getChannel().sendMessage(message).queue();
    }

    public void sendMessage(String message, int duration) {
        getChannel().sendMessage(message).queue(msg -> msg.delete().queueAfter(duration, TimeUnit.SECONDS));
    }

    public void sendMessage(MessageEmbed embed, int duration) {
        getChannel().sendMessage(embed).queue(msg -> msg.delete().queueAfter(duration, TimeUnit.SECONDS));

    }

    public void sendEmbed(String header, String message, int duration) {
        header = header == null? "" : header;
        message = message == null? "" : message;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                //.setFooter(event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl())
                .build();
        getChannel().sendMessage(embed).queue(msg -> msg.delete().queueAfter(duration, TimeUnit.SECONDS));
    }

    public void sendEmbed(String header, String message) {
        header = header == null? "" : header;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                //.setFooter(event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl())
                .build();
        getChannel().sendMessage(embed).queue();
    }
}
