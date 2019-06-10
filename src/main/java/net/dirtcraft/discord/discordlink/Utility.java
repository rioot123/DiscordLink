package net.dirtcraft.discord.discordlink;

import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import io.github.nucleuspowered.nucleus.api.service.NucleusAFKService;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.spongediscordlib.DiscordUtil;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
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
                .getTextChannelById(SpongeDiscordLib.getGamechatChannelID())
                .sendMessage(
                        PluginConfiguration.Format.serverToDiscord
                                .replace("{prefix}", prefix)
                                .replace("{username}", playerName)
                                .replace("{message}", message))
                .queue();
    }

    public static void messageToChannel(String type, String message, MessageEmbed embed) {
        switch (type.toLowerCase()) {
            default:
            case "message":
                DiscordLink
                        .getJDA()
                        .getTextChannelById(SpongeDiscordLib.getGamechatChannelID())
                        .sendMessage(message)
                        .queue();
            break;
            case "embed":
                DiscordLink
                        .getJDA()
                        .getTextChannelById(SpongeDiscordLib.getGamechatChannelID())
                        .sendMessage(embed)
                        .queue();
                break;
        }
    }

    public static void autoRemove(int delaySeconds, String type, String message, MessageEmbed embed) {
        switch (type.toLowerCase()) {
            default:
            case "message":
                DiscordLink
                        .getJDA()
                        .getTextChannelById(SpongeDiscordLib.getGamechatChannelID())
                        .sendMessage(message)
                        .queue(msg -> {
                            msg.delete().queueAfter(delaySeconds, TimeUnit.SECONDS);
                        });
                break;
            case "embed":
                DiscordLink
                        .getJDA()
                        .getTextChannelById(SpongeDiscordLib.getGamechatChannelID())
                        .sendMessage(embed)
                        .queue(msg -> {
                            msg.delete().queueAfter(delaySeconds, TimeUnit.SECONDS);
                        });
                break;
        }
    }

    public static void setTopic() {
        TextChannel channel = DiscordLink
                .getJDA()
                .getTextChannelById(SpongeDiscordLib.getGamechatChannelID());
        String[] code = channel.getName().split("-");

        channel.getManager()
                .setTopic("ModPack: **" + SpongeDiscordLib.getServerName() + "** — IP: " + code[1] + ".dirtcraft.gg")
                .queue();
    }

    public static void setStatus() {
        DiscordUtil.setStatus(Game.GameType.STREAMING, SpongeDiscordLib.getServerName(), "https://www.twitch.tv/dirtcraft/");
    }

    public static void listCommand(MessageReceivedEvent event) {
        Member member = event.getMember();

        Collection<Player> players = Sponge.getServer().getOnlinePlayers();

        ArrayList<String> playerNames = new ArrayList<>();
        players.forEach(online -> {
            if (NucleusAPI.getAFKService().isPresent()) {
                if (NucleusAPI.getAFKService().get().isAFK(online)) {
                    playerNames.add(online.getName() + " " + "—" + " " + "**AFK**");
                } else {
                    playerNames.add(online.getName());
                }
            } else {
                playerNames.add(online.getName());
            }
        });

        playerNames.sort(String::compareToIgnoreCase);

        EmbedBuilder embed = Utility.embedBuilder();
        if (players.size() > 1) {
            embed.addField("__**" + players.size() + "** players online__", String.join("\n", playerNames), false);
        } else if (players.size() == 1) {
            embed.addField("__**" + players.size() + "** player online__", String.join("\n", playerNames), false);
        } else {
            embed.setDescription("There are no players playing **" + SpongeDiscordLib.getServerName() + "**!");
        }
                embed.setFooter("Requested By: " + member.getUser().getAsTag(), event.getAuthor().getAvatarUrl());

        DiscordLink
                .getJDA()
                .getTextChannelById(SpongeDiscordLib.getGamechatChannelID())
                .sendMessage(embed.build())
                .queue();
    }

    public static void toConsole(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw()
                .replace(PluginConfiguration.Main.consolePrefix, "")
                .split(" ");

        if (!consoleCheck(event)) {
            event.getMessage().delete().queue();
            Utility.autoRemove(5, "message", "<@" + event.getAuthor().getId() + ">, you do **not** have permission to use this command!", null);
            DiscordLink.getJDA()
                    .getTextChannelsByName("command-log", true).get(0)
                    .sendMessage(Utility.embedBuilder()
                            .addField("__Tried Executing Command__", event.getMessage().getContentDisplay(), false)
                            .setFooter(event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl())
                            .build())
                    .queue();
            return;
        }

        String command = String.join(" ", args);
        Task.builder()
                .execute(() ->
                        Sponge.getCommandManager().process(new ConsoleManager(Sponge.getServer().getConsole(), event.getMember(), command), command))
                .submit(DiscordLink.getInstance());
    }

    private static boolean consoleCheck(MessageReceivedEvent event) {
        Role adminRole = event.getGuild().getRoleById(PluginConfiguration.Roles.adminRoleID);
        Role ownerRole = event.getGuild().getRoleById(PluginConfiguration.Roles.ownerRoleID);

        if (event.getMember().getRoles().contains(ownerRole)) {
            return true;
        } else if (event.getMember().getRoles().contains(adminRole)) {
            if (event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "luckperms") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "perm") ||
                    event.getMessage().getContentRaw().toLowerCase().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "permissions") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "perm") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "lp") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "execute") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "ban") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "ipban") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "tempban") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "nameban") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "nameunban") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "tempmute") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "mute") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "kick") ||
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "whitelist")) {
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
