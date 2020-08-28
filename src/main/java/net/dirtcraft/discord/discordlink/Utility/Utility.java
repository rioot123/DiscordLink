package net.dirtcraft.discord.discordlink.Utility;

import com.google.common.collect.Lists;
import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.Sources.GamechatSender;
import net.dirtcraft.discord.discordlink.Commands.Sources.PrivateSender;
import net.dirtcraft.discord.discordlink.Commands.Sources.WrappedConsole;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.spongediscordlib.DiscordUtil;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.awt.*;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public class Utility {

    public static final String STRIP_CODE_REGEX = "[§&]([0-9a-fA-FrlonmkRLONMK])";
    public static final String URL_DETECT_REGEX = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";

    public static EmbedBuilder embedBuilder() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle(PluginConfiguration.Embed.title);
        if (PluginConfiguration.Embed.timestamp) {
            embed.setTimestamp(Instant.now());
        }
        return embed;
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

    public static void toConsole(MessageReceivedEvent event, GuildMember sender, boolean silent) {
        final int prefixLength = silent ? PluginConfiguration.Main.silentConsolePrefix.length() : PluginConfiguration.Main.consolePrefix.length();
        final String command = event.getMessage().getContentRaw().substring(prefixLength);
        final List<String> blacklist = PluginConfiguration.Command.blacklist;
        if (!(sender.hasRole(Roles.DIRTY) || sender.hasRole(Roles.ADMIN) && blacklist.stream().noneMatch(command::startsWith))) {
            sendPermissionErrorMessage(event);
            return;
        }
        final WrappedConsole commandSender = silent ? new PrivateSender(sender, command) : new GamechatSender(sender, command);
        Task.builder()
                .execute(() -> Sponge.getCommandManager().process(commandSender, command))
                .submit(DiscordLink.getInstance());
    }

    public static void sendResponse(MessageReceivedEvent event, String error){
        sendResponse(event, error, 30);
    }

    public static void sendResponse(MessageReceivedEvent event, String error, int delay){
        event.getMessage().delete().queue();
        GameChat.sendMessage("<@" + event.getAuthor().getId() + ">, " + error, delay);
    }

    public static void sendPermissionErrorMessage(MessageReceivedEvent event){
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

    public static Text format(String unformattedString) {
        return TextSerializers.FORMATTING_CODE.deserialize(unformattedString);
    }

    public static Collection<Player> getSpongePlayer(String name){
        Pattern pattern = Pattern.compile("(\\d{8}-?\\d{4}-?\\d{4}-?\\d{4}-?\\d{12})");
        if (name.startsWith("@a")){
            return Sponge.getServer().getOnlinePlayers();
        } else if ((name.length() == 32 || name.length() == 36) && pattern.matcher(name).matches()){
            UUID uuid = UUID.fromString(name);
            Optional<Player> optPlayer = Sponge.getServer().getPlayer(uuid);
            return optPlayer.map(Lists::newArrayList).orElseGet(Lists::newArrayList);
        } else {
            Optional<Player> optPlayer = Sponge.getServer().getPlayer(name);
            return optPlayer.map(Lists::newArrayList).orElseGet(Lists::newArrayList);
        }
    }

    public static Optional<User> getSpongeUser(String nameOrUUID){
        final UserStorageService userStorageService = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        Pattern pattern = Pattern.compile("(\\d{8}-?\\d{4}-?\\d{4}-?\\d{4}-?\\d{12})");
        if ((nameOrUUID.length() == 32 || nameOrUUID.length() == 36) && pattern.matcher(nameOrUUID).matches()){
            UUID uuid = UUID.fromString(nameOrUUID);
            return userStorageService.get(uuid);
        } else {
            return userStorageService.get(nameOrUUID);
        }
    }

    public static String sanitiseMinecraftText(String s){
        return s.replaceAll(STRIP_CODE_REGEX, "")
                .replace("@everyone", "")
                .replace("@here", "")
                .replaceAll("([_*~`>|\\\\])", "\\\\$1")
                .replaceAll("<@\\d+>", "");
    }
}
