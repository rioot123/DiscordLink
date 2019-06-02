package net.dirtcraft.discord.discordlink;

import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

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

    public static void autoRemove(int delaySeconds, String type, String message, MessageEmbed embed) {
        switch (type.toLowerCase()) {
            case "message":
                DiscordLink
                        .getJDA()
                        .getTextChannelById(PluginConfiguration.Main.channelID)
                        .sendMessage(message)
                        .queue(msg -> {
                            msg.delete().queueAfter(delaySeconds, TimeUnit.SECONDS);
                        });
                break;
            case "embed":
                DiscordLink
                        .getJDA()
                        .getTextChannelById(PluginConfiguration.Main.channelID)
                        .sendMessage(embed)
                        .queue(msg -> {
                            msg.delete().queueAfter(delaySeconds, TimeUnit.SECONDS);
                        });
                break;
            default:
                DiscordLink
                        .getJDA()
                        .getTextChannelById(PluginConfiguration.Main.channelID)
                        .sendMessage(message)
                        .queue(msg -> msg.delete().queueAfter(delaySeconds, TimeUnit.SECONDS));
                break;
        }
    }

    public static void setTopic() {
        TextChannel channel = DiscordLink
                .getJDA()
                .getTextChannelById(PluginConfiguration.Main.channelID);
        String[] code = channel.getName().split("-");

        channel.getManager()
                .setTopic("ModPack: **" + SpongeDiscordLib.getServerName() + "** — IP: " + code[1] + ".dirtcraft.gg")
                .queue();
    }

    public static void listCommand(MessageReceivedEvent event) {
        Member member = event.getMember();

        ArrayList<Player> players = new ArrayList<>(Sponge.getServer().getOnlinePlayers());
        ArrayList<String> playerNames = new ArrayList<>();

        for (Player player : players) {
            playerNames.add(player.getName());
        }

        Collections.sort(playerNames);


        EmbedBuilder embed = Utility.embedBuilder();
        if (players.size() > 1) {
            embed.addField("__**" + players.size() + "** players online__", String.join("\n", playerNames), false);
        } else if (players.size() == 1) {
            embed.addField("__**" + players.size() + "** player online__", String.join("\n", playerNames), false);
        } else {
            embed.addField("__**No** players online__", String.join("\n", playerNames), false);
        }
                embed.setFooter("Requested By: " + member.getUser().getAsTag(), null);

        DiscordLink
                .getJDA()
                .getTextChannelById(PluginConfiguration.Main.channelID)
                .sendMessage(embed.build())
                .queue();
    }

    public static void toConsole(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw()
                .replace(PluginConfiguration.Main.consolePrefix, "")
                .split(" ");

        if (!consoleCheck(event)) {
            Utility.autoRemove(5, "message", "<@" + event.getAuthor().getId() + ">, you do **not** have permission to use this command!", null);
            return;
        }

        String command = String.join(" ", args);
        Task.builder()
                .execute(() ->
                        Sponge.getCommandManager().process(new ConsoleManager(Sponge.getServer().getConsole(), event.getMember(), command), command))
                .submit(DiscordLink.getInstance());
    }

    private static boolean consoleCheck(MessageReceivedEvent event) {
        Role adminRole = event.getGuild().getRoleById("531631265443479562");
        Role ownerRole = event.getGuild().getRoleById("307551061156298762");

        if (event.getMember().getRoles().contains(ownerRole)) {
            return true;
        } else if (event.getMember().getRoles().contains(adminRole)) {
            if (event.getMessage().getContentRaw().startsWith(PluginConfiguration.Main.consolePrefix + "luckperms") ||
                    event.getMessage().getContentRaw().startsWith(PluginConfiguration.Main.consolePrefix + "perm") ||
                    event.getMessage().getContentRaw().startsWith(PluginConfiguration.Main.consolePrefix + "permissions") ||
                    event.getMessage().getContentRaw().startsWith(PluginConfiguration.Main.consolePrefix + "perm") ||
                    event.getMessage().getContentRaw().startsWith(PluginConfiguration.Main.consolePrefix + "lp") ||
                    event.getMessage().getContentRaw().startsWith(PluginConfiguration.Main.consolePrefix + "ban") ||
                    event.getMessage().getContentRaw().startsWith(PluginConfiguration.Main.consolePrefix + "ipban") ||
                    event.getMessage().getContentRaw().startsWith(PluginConfiguration.Main.consolePrefix + "tempban") ||
                    event.getMessage().getContentRaw().startsWith(PluginConfiguration.Main.consolePrefix + "tempmute") ||
                    event.getMessage().getContentRaw().startsWith(PluginConfiguration.Main.consolePrefix + "mute") ||
                    event.getMessage().getContentRaw().startsWith(PluginConfiguration.Main.consolePrefix + "kick")) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public static Text format(String unformattedString) {
        return TextSerializers.FORMATTING_CODE.deserialize(unformattedString);
    }
}
