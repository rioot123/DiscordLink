package net.dirtcraft.discord.discordlink.Commands.Sources;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.Utility.Utility;

import java.util.function.Consumer;

public interface ScheduledSender {
    void sendDiscordResponse(String message);

    int getCharLimit();

    boolean sanitise();

    static ScheduledSender getSender(Consumer<String> output, int limit, boolean sanitise) {
        return new ScheduledSender() {
            @Override
            public void sendDiscordResponse(String message) {
                output.accept(message);
            }

            @Override
            public int getCharLimit(){
                return limit;
            }

            @Override
            public boolean sanitise(){
                return sanitise;
            }
        };
    }

    interface Private extends ScheduledSender {
        GuildMember getMember();
        String getCommand();

        @Override
        default void sendDiscordResponse(String message) {
            if (message.length() > getCharLimit()) return;
            getMember().sendMessage("``" + message + "``");
        }

        @Override
        default int getCharLimit(){
            return 1996;
        }

        @Override
        default boolean sanitise(){
            return false;
        }
    }

    interface Public extends ScheduledSender {
        GuildMember getMember();
        String getCommand();

        @Override
        default void sendDiscordResponse(String message) {
            if (message.length() > getCharLimit()) return;
            GameChat.sendMessage(
                    Utility.embedBuilder().addField("__Command__ \"**/" + getCommand().toLowerCase() + "**\" __Sent__", message, false)
                            .setFooter("Sent By: " + getMember().getUser().getAsTag(), getMember().getUser().getAvatarUrl())
                            .build());
        }

        default int getCharLimit(){
            return 1024;
        }

        default boolean sanitise(){
            return true;
        }

    }
}
