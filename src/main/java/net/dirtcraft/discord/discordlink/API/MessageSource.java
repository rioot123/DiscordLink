
package net.dirtcraft.discord.discordlink.API;


import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MessageSource extends GuildMember {
    private final Message message;
    private final GameChat channel;
    public MessageSource(MessageReceivedEvent event){
        super(GameChats.getGuild().retrieveMember(event.getAuthor()).complete());
        this.message = event.getMessage();
        this.channel = GameChats.getChannel(event.getChannel()).orElse(null);
    }

    public Message getMessage() {
        return message;
    }

    public GameChat getGamechat() {
        return channel;
    }

    public boolean wasInGamechat(){
        return channel != null;
    }

    public String getChannelLink(){
        return wasInGamechat()? String.format("<#%s>", channel.channel): "Bot DM";
    }
}