package net.dirtcraft.discord.discordlink.API;

import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class GameChat {

    public static void messageToChannel(MessageEmbed embed) {
        DiscordLink
                .getJDA()
                .getTextChannelById(SpongeDiscordLib.getGamechatChannelID())
                .sendMessage(embed)
                .queue();
    }


    public static void messageToChannel(String message) {
        DiscordLink
                .getJDA()
                .getTextChannelById(SpongeDiscordLib.getGamechatChannelID())
                .sendMessage(message)
                .queue();
    }
}
