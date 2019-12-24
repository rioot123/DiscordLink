package net.dirtcraft.discord.discordlink.Commands;

import com.google.common.collect.Lists;
import net.dirtcraft.discord.discordlink.API.DiscordRoles;
import net.dirtcraft.discord.discordlink.API.DiscordSource;
import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordPermissionException;
import net.dirtcraft.discord.discordlink.Utility;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class DiscordCommand {
    private final List<DiscordRoles> allowedRoles;
    private final String description;
    private final DiscordCommandExecutor executor;

    private DiscordCommand(List<DiscordRoles> allowed, DiscordCommandExecutor executor, String description){
        this.allowedRoles = allowed;
        this.executor = executor;
        this.description = description;
    }

    private void sendPermissionError(MessageReceivedEvent event){
        event.getMessage().delete().queue();
        GameChat.sendMessage("<@" + event.getAuthor().getId() + ">, you do **not** have permission to use this command!", 5);
        DiscordLink.getJDA()
                .getTextChannelsByName("command-log", true).get(0)
                .sendMessage(Utility.embedBuilder()
                        .addField("__Tried Executing Command__", event.getMessage().getContentDisplay(), false)
                        .setFooter(event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl())
                        .build())
                .queue();

    }

    private void sendCommandError(MessageReceivedEvent event, String msg){
        event.getMessage().delete().queue();
        GameChat.sendMessage("<@" + event.getAuthor().getId() + ">, " + msg, 5);
        DiscordLink.getJDA()
                .getTextChannelsByName("command-log", true).get(0)
                .sendMessage(Utility.embedBuilder()
                        .addField("__Tried Executing Command__", event.getMessage().getContentDisplay(), false)
                        .setFooter(event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl())
                        .build())
                .queue();
    }

    public boolean hasPermission(DiscordSource member){
        return allowedRoles.stream().allMatch(member::hasPermission);
    }

    public static Builder builder(){
        return new Builder();
    }

    public final void process(DiscordSource member, String[] command, MessageReceivedEvent event) {
        if (!allowedRoles.stream().allMatch(member::hasPermission)) {
            sendPermissionError(event);
            return;
        }
        try {
            executor.execute(member, command, event);
        } catch (DiscordPermissionException e) {
            sendPermissionError(event);
        } catch (Exception e) {
            sendCommandError(event, e.getMessage() != null ? e.getMessage() : "an error occurred while executing the command.");
        }


    }

    public static class Builder{
        private List<DiscordRoles> allowedRoles;
        private DiscordCommandExecutor executor;
        private String description;

        private Builder(){}

        public final Builder setRequiredRoles(DiscordRoles... roles){
            allowedRoles = Lists.newArrayList(roles);
            return this;
        }

        public final Builder setCommandExecutor(DiscordCommandExecutor executor){
            this.executor = executor;
            return this;
        }

        public final Builder setDescription(String description){
            this.description = description;
            return this;
        }

        public DiscordCommand build(){
            return new DiscordCommand(
                    allowedRoles != null ? allowedRoles : new ArrayList<>(),
                    executor     != null ? executor     : (member, command, event)->{},
                    description  != null ? description  : ""
            );
        }

    }
}
