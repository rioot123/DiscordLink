package net.dirtcraft.discord.discordlink;

import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.awt.*;
import java.time.Instant;

public class Utility {

    public static EmbedBuilder embedBuilder() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle(PluginConfiguration.Embed.title);
        if (PluginConfiguration.Embed.timestamp) {
            embed.setTimestamp(Instant.now());
        }
        return embed;
    }

    public static void chatToDiscord(String prefix, String playerName, String message) {
        DiscordLink
                .getJDA()
                .getTextChannelById(PluginConfiguration.Main.channelID)
                .sendMessage(
                        PluginConfiguration.Format.serverToDiscord
                        .replace("{prefix}", prefix)
                        .replace("{username}", playerName)
                        .replace("{message}", message))
                .queue();
    }

    public static void messageToChannel(String type, String message, MessageEmbed embed) {
        switch (type.toLowerCase()) {
            case "message":
                DiscordLink
                    .getJDA()
                    .getTextChannelById(PluginConfiguration.Main.channelID)
                    .sendMessage(message)
                    .queue();
                break;
            case "embed":
                DiscordLink
                        .getJDA()
                        .getTextChannelById(PluginConfiguration.Main.channelID)
                        .sendMessage(embed)
                        .queue();
                break;
            default:
                DiscordLink
                        .getJDA()
                        .getTextChannelById(PluginConfiguration.Main.channelID)
                        .sendMessage(message)
                        .queue();
                break;
        }
    }

    public static void setTopic() {
        TextChannel channel = DiscordLink
                .getJDA()
                .getTextChannelById(PluginConfiguration.Main.channelID);
        String[] code = channel.getName().split("-");

        channel.getManager()
                .setTopic("ModPack: **" + SpongeDiscordLib.getServerName() + "** — IP: `" + code[1] + ".dirtcraft.gg`")
                .queue();
    }

    public static Text format(String unformattedString) {
        return TextSerializers.FORMATTING_CODE.deserialize(unformattedString);
    }

}
