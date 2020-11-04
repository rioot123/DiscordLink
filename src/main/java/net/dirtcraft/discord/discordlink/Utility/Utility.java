package net.dirtcraft.discord.discordlink.Utility;

import net.dirtcraft.discord.discordlink.API.*;
import net.dirtcraft.discord.discordlink.Commands.Sources.WrappedConsole;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginManager;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.dirtcraft.discord.discordlink.Storage.PluginConfiguration.Command.sanctions;
import static net.dirtcraft.discord.discordlink.Storage.PluginConfiguration.Command.whiteList;

public class Utility {

    public static final String STRIP_CODE_REGEX = "(?i)[§&][0-9a-fklmnor]";

    public static Optional<Member> getMemberById(String id){
        try {
            return Optional.of(GameChats.getGuild().retrieveMemberById(id).complete());
        } catch (Exception e){
            return Optional.empty();
        }
    }

    public static Optional<Member> getMemberById(long id){
        try {
            return Optional.of(GameChats.getGuild().retrieveMemberById(id).complete());
        } catch (Exception e){
            return Optional.empty();
        }
    }

    public static Optional<Member> getMember(User user){
        try {
            return Optional.of(GameChats.getGuild().retrieveMember(user).complete());
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

    public static boolean toConsole(String command, MessageSource sender, Action type) {
        if (!canUseCommand(sender, type)){
            sendCommandError(sender, command);
            return false;
        } else if (isSanction(command)){
            WrappedConsole console = type.getSender(sender, command);
            sender.executeSanction(command, console, false);
            return true;
        } else if (type.isBungee() || isWhitelisted(command)){
            WrappedConsole console = type.getSender(sender, command);
            toConsole(console, command);
            return true;
        } else {
            return false;
        }
    }

    public static void toConsole(WrappedConsole commandSender, String command) {
        PluginManager manager = ProxyServer.getInstance().getPluginManager();
        manager.dispatchCommand(commandSender, command);
    }

    private static boolean canUseCommand(GuildMember sender, Action type){
        return sender.hasRole(Roles.DIRTY) || !type.isBungee() && sender.hasRole(Roles.ADMIN);
    }

    private static boolean isSanction(String command){
        return sanctions.stream().anyMatch(command::startsWith);
    }

    private static boolean isWhitelisted(String command){
        return whiteList.stream().anyMatch(command::startsWith);
    }

    public static void sendPermissionError(MessageSource event){
        event.getGamechat().sendMessage("<@" + event.getUser().getId() + ">, you do **not** have permission to use this command!", 5);
        logCommand(event, "__Tried Executing Command__");
    }

    public static void sendCommandError(MessageSource event, String msg){
        event.getGamechat().sendMessage("<@" + event.getUser().getId() + ">, " + msg, 5);
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

    public static void sendPermissionErrorMessage(GameChat chat, MessageReceivedEvent event){
        event.getMessage().delete().queue();
        chat.sendMessage("<@" + event.getAuthor().getId() + ">, you do **not** have permission to use this command!", 5);
        DiscordLink.getJDA()
                .getTextChannelsByName("command-log", true).get(0)
                .sendMessage(Utility.embedBuilder()
                        .addField("__Tried Executing Command__", event.getMessage().getContentDisplay(), false)
                        .setFooter(event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl())
                        .build())
                .queue();
    }

    public static String sanitizeMinecraftText(String s){
        return s.replaceAll(STRIP_CODE_REGEX, "")
                .replaceAll("([_*~`>|\\\\])", "\\\\$1")
                .replace("@everyone", "")
                .replace("@here", "")
                .replaceAll("<@\\d+>", "");
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

    public static String stripColorCodes(String s){
        return s.replaceAll(STRIP_CODE_REGEX, "");
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
