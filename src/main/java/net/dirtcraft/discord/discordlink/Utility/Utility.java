package net.dirtcraft.discord.discordlink.Utility;

import net.dirtcraft.discord.discordlink.API.*;
import net.dirtcraft.discord.discordlink.Commands.Sources.WrappedConsole;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration.Command.blacklist;
import static net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration.Command.ignored;

public class Utility {

    public static final String STRIP_CODE_REGEX = "(?i)[§&][0-9a-fklmnor]";
    public static final String DETECT_URL_REGEX = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";

    public static Optional<Member> getMemberById(String id){
        try {
            return Optional.of(GameChat.getGuild().retrieveMemberById(id).complete());
        } catch (Exception e){
            return Optional.empty();
        }
    }

    public static Optional<Member> getMemberById(long id){
        try {
            return Optional.of(GameChat.getGuild().retrieveMemberById(id).complete());
        } catch (Exception e){
            return Optional.empty();
        }
    }

    public static Optional<Member> getMember(User user){
        try {
            return Optional.of(GameChat.getGuild().retrieveMember(user).complete());
        } catch (Exception e){
            return Optional.empty();
        }
    }

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
                .getTextChannelById(PluginConfiguration.Main.GAMECHAT_CHANNEL_ID);
        String code = channel.getName().split("-")[1];

        channel.getManager()
                .setTopic("ModPack: **" + PluginConfiguration.Main.SERVER_NAME + "** — IP: " + code + ".dirtcraft.gg")
                .queue();
    }

    public static void setStatus() {
        SpongeDiscordLib.setStatus(Activity.ActivityType.STREAMING, PluginConfiguration.Main.SERVER_NAME, "https://www.twitch.tv/dirtcraft/");
    }

    public static boolean toConsole(String command, MessageSource sender, Action type) {
        if (ignored.stream().anyMatch(command::startsWith)) return false;
        if (canUseCommand(sender, command)) {
            final WrappedConsole commandSender = type.getSender(sender, command);
            toConsole(commandSender, command);
            return true;
        } else {
            sendPermissionError(sender);
            return false;
        }
    }

    public static void toConsole(WrappedConsole commandSender, String command) {
        Bukkit.getScheduler().callSyncMethod(DiscordLink.getInstance(), () -> Bukkit.dispatchCommand(commandSender, command));
    }

    private static boolean canUseCommand(GuildMember sender, String command){
        return sender.hasRole(Roles.DIRTY) ||
                sender.hasRole(Roles.ADMIN) && blacklist.stream().noneMatch(command::startsWith);
    }

    public static void sendResponse(MessageReceivedEvent event, String error, int delay){
        event.getMessage().delete().queue();
        GameChat.sendMessage("<@" + event.getAuthor().getId() + ">, " + error, delay);
    }

    public static void sendPermissionError(MessageSource event){
        GameChat.sendMessage("<@" + event.getUser().getId() + ">, you do **not** have permission to use this command!", 5);
        logCommand(event, "__Tried Executing Command__");
    }

    public static void sendCommandError(MessageSource event, String msg){
        GameChat.sendMessage("<@" + event.getUser().getId() + ">, " + msg, 5);
        logCommand(event, "__Tried Executing Command__");
    }

    public static void logCommand(MessageSource event, String message){
        DiscordLink.getJDA()
                .getTextChannelsByName("command-log", true).get(0)
                .sendMessage(Utility.embedBuilder()
                        .addField(message, event.getMessage().getContentDisplay(), false)
                        .setFooter(event.getUser().getAsTag(), event.getUser().getAvatarUrl())
                        .build())
                .queue();
    }

    public static String formatColourCodes(String s){
        return s.replaceAll("&([0-9a-fA-FrlonmkRLONMK])", "§$1");
    }

    public static String removeColourCodes(String s){
        return s.replaceAll("[§&].", "");
    }

    public static String sanitiseMinecraftText(String s){
        return s.replaceAll(STRIP_CODE_REGEX, "")
                .replace("@everyone", "")
                .replace("@here", "")
                .replaceAll("([_*~`>|\\\\])", "\\\\$1")
                .replaceAll("<@\\d+>", "");
    }

    public static String stripColorCodes(String s){
        return s.replaceAll(STRIP_CODE_REGEX, "");
    }

    public static void dmExceptionAsync(Exception e, long... id){
        CompletableFuture.runAsync(()->dmException(e, id));
    }

    private static void dmException(Exception e, long... id){
        Arrays.stream(id)
                .mapToObj(Utility::getMemberById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Member::getUser)
                .map(User::openPrivateChannel)
                .forEach(dm->dmException(dm, e));
    }

    private static void dmException(RestAction<PrivateChannel> dms, Exception e){
        String[] ex = ExceptionUtils.getStackTrace(e).split("\\r?\\n");
        sendMessages(s->dms.queue(dm->dm.sendMessage(s).queue()), 1980, ex);
    }

    public static void sendMessages(Consumer<String> destination, int limit, String... messages){
        StringBuilder sb = new StringBuilder();
        for (String s : messages) {
            if (sb.length() + s.length() < limit){
                sb.append(s);
                sb.append("\n");
            } else {
                destination.accept(sb.toString());
                sb = new StringBuilder(s);
            }
        }
        if (sb.length() > 0) destination.accept(sb.toString());
    }
}
