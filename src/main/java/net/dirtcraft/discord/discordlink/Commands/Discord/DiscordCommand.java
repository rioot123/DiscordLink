package net.dirtcraft.discord.discordlink.Commands.Discord;

import com.google.common.collect.Lists;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Utility;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class DiscordCommand {
    private final List<Long> allowedRoles;
    private final DiscordCommandExecutor executor;

    DiscordCommand(List<Long> allowed, DiscordCommandExecutor executor){
        this.allowedRoles = allowed;
        this.executor = executor;
    }

    public final void process(Member member, String[] command, MessageReceivedEvent event){
        if (allowedRoles.isEmpty() || member.getRoles().stream().anyMatch(role -> allowedRoles.contains(role.getIdLong()))) executor.execute(member, command, event);
        else {
            event.getMessage().delete().queue();
            Utility.autoRemove(5, "message", "<@" + event.getAuthor().getId() + ">, you do **not** have permission to use this command!", null);
            DiscordLink.getJDA()
                    .getTextChannelsByName("command-log", true).get(0)
                    .sendMessage(Utility.embedBuilder()
                            .addField("__Tried Executing Command__", event.getMessage().getContentDisplay(), false)
                            .setFooter(event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl())
                            .build())
                    .queue();
        }
    }

    public static class Builder{
        private List<Long> allowedRoles;
        private DiscordCommandExecutor executor;

        public final Builder setRequiredRoles(Role... roles){
            allowedRoles = new ArrayList<>();
            for (Role role : roles) allowedRoles.add(role.getIdLong());
            return this;
        }

        public final Builder setRequiredRoles(Long... roles){
            allowedRoles = Lists.newArrayList(roles);
            return this;
        }

        public final Builder setCommandExecutor(DiscordCommandExecutor executor){
            this.executor = executor;
            return this;
        }

        public DiscordCommand build(){
            return new DiscordCommand(
                    allowedRoles != null ? allowedRoles : new ArrayList<>(),
                    executor != null? executor : (member, command, event)->{}
            );
        }

    }
}
