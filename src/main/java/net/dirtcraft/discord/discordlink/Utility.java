package net.dirtcraft.discord.discordlink;

import net.dirtcraft.discord.discordlink.Commands.Sources.GamechatSender;
import net.dirtcraft.discord.discordlink.Commands.Sources.PrivateSender;
import net.dirtcraft.discord.discordlink.Commands.Sources.WrappedConsole;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.spongediscordlib.DiscordUtil;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.awt.*;
import java.time.Instant;
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

    public static void messageToUser(User user, String type, String message, MessageEmbed embed) {
        switch (type.toLowerCase()) {
            default:
            case "message":
                user.openPrivateChannel().queue(dm-> dm.sendMessage(message).queue());
                break;
            case "embed":
                user.openPrivateChannel().queue(dm-> dm.sendMessage(embed).queue());
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
        if (SpongeDiscordLib.getServerName().toLowerCase().contains("pixel")) {
            String name = SpongeDiscordLib.getServerName().split(" ")[1];
            String code = SpongeDiscordLib.getServerName().toLowerCase().split(" ")[1];
            switch (code) {
                case "redstone":
                    code = "red";
                    break;
                case "glowstone":
                    code = "glow";
                    break;
                default:
                case "lapiz":
                    break;
            }
            channel.getManager()
                    .setTopic("**Pixelmon " + name + "** — IP: " + code + ".pixelmon.gg")
                    .queue();
            return;
        }
        String code = channel.getName().split("-")[1];

        channel.getManager()
                .setTopic("ModPack: **" + SpongeDiscordLib.getServerName() + "** — IP: " + code + ".dirtcraft.gg")
                .queue();
    }

    public static void setStatus() {
        DiscordUtil.setStatus(Game.GameType.STREAMING, SpongeDiscordLib.getServerName(), "https://www.twitch.tv/dirtcraft/");
    }

    public static void toConsole(MessageReceivedEvent event, boolean silent) {
        if (!consoleCheck(event)) {
            sendPermissionErrorMessage(event);
            return;
        }

        String command = event.getMessage().getContentRaw()
                .substring(PluginConfiguration.Main.consolePrefix.length()); // remove the prefix.

        WrappedConsole commandSender = silent? new GamechatSender(event.getMember(), command) : new PrivateSender(event.getMember(), command);

        Task.builder()
                .execute(() ->
                        Sponge.getCommandManager().process(commandSender, command))
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
                    event.getMessage().getContentRaw().toLowerCase().startsWith(PluginConfiguration.Main.consolePrefix + "permissions") ||
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

    public static void sendResponse(MessageReceivedEvent event, String error){
        sendResponse(event, error, 30);
    }

    public static void sendResponse(MessageReceivedEvent event, String error, int delay){
        event.getMessage().delete().queue();
        Utility.autoRemove(delay, "message", "<@" + event.getAuthor().getId() + ">, " + error, null);
    }

    public static void sendPermissionErrorMessage(MessageReceivedEvent event){
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

    public static Text format(String unformattedString) {
        return TextSerializers.FORMATTING_CODE.deserialize(unformattedString);
    }
}
