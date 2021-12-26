package net.dirtcraft.discord.discordlink.Utility;

import net.dirtcraft.discord.discordlink.API.*;
import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Permission;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformPlayer;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.dirtcraft.discord.discordlink.Storage.PluginConfiguration.Command.blacklist;
import static net.dirtcraft.discord.discordlink.Storage.PluginConfiguration.Command.ignored;

public class Utility {

    public static final String STRIP_CODE_REGEX = "(?i)[§&][0-9a-fklmnor]";
    public static final String DETECT_URL_REGEX = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";

    public static Optional<Member> getMemberById(String id){
        try {
            return Optional.of(Channels.getGuild().retrieveMemberById(id).complete());
        } catch (Exception e){
            return Optional.empty();
        }
    }

    public static Optional<Member> getMemberById(long id){
        try {
            return Optional.of(Channels.getGuild().retrieveMemberById(id).complete());
        } catch (Exception e){
            return Optional.empty();
        }
    }

    public static Optional<Member> getMember(User user){
        try {
            return Optional.of(Channels.getGuild().retrieveMember(user).complete());
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
                .getTextChannelById(PluginConfiguration.Main.defaultChannelID);

        if (channel == null) {
            System.out.println("[Discord-Link] Bad configuration! Please verify if the gamechat channel id is valid.");
            return;
        }

        String code = channel.getName().split("-")[1];

        channel.getManager()
                .setTopic("ModPack: **" + PluginConfiguration.Main.SERVER_NAME + "** — IP: " + code + ".dirtcraft.gg")
                .queue();
    }

    public static void setRoles(PlatformPlayer player){
        CompletableFuture.runAsync(()-> GuildMember.fromPlayerId(player.getUUID())
                .ifPresent(member->setRoles(player, member)));
    }

    public static void setRoles(PlatformPlayer player, GuildMember member) {
        Guild guild = Channels.getGuild();
        if (player.hasPermission(Permission.ROLES_DONOR)) setRoleIfAbsent(guild, member, Roles.DONOR);
        if (!member.getRoles().contains(Roles.STAFF.getRole())) tryChangeNickname(guild, member, player.getName());
        setRoleIfAbsent(guild, member, Roles.VERIFIED);
    }

    public static void setRoleIfAbsent(Guild guild, GuildMember member, Roles role){
        try {
            Role discordRole = role.getRole();
            if (discordRole == null || member.hasRole(role)) return;
            guild.addRoleToMember(member, discordRole).submit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void setRoleIfAbsent(long id, Roles role){
        try {
            Guild guild = Channels.getGuild();
            Member discord = guild.retrieveMemberById(id).complete();
            if (discord == null) return;
            GuildMember member = new GuildMember(discord);
            Role discordRole = role.getRole();
            if (discordRole == null || member.hasRole(role)) return;
            guild.addRoleToMember(member, discordRole).submit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeRoleIfPresent(Guild guild, GuildMember member, Roles role){
        try {
            Role discordRole = role.getRole();
            if (discordRole == null || !member.hasRole(role)) return;
            guild.removeRoleFromMember(member, discordRole).submit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void removeRoleIfPresent(long id, Roles role){
        try {
            Guild guild = Channels.getGuild();
            Member discord = guild.retrieveMemberById(id).complete();
            if (discord == null) return;
            GuildMember member = new GuildMember(discord);
            Role discordRole = role.getRole();
            if (discordRole == null || !member.hasRole(role)) return;
            guild.removeRoleFromMember(member, discordRole).submit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void tryChangeNickname(Guild guild, GuildMember member, String name){
        try {
            if (name == null) return;
            guild.modifyNickname(member, name).submit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setStatus() {
        SpongeDiscordLib.setStatus(Activity.ActivityType.STREAMING, PluginConfiguration.Main.SERVER_NAME, "https://www.twitch.tv/dirtcraft/");
    }

    public static boolean toConsole(String command, MessageSource sender, Action type) {
        if (ignored.stream().anyMatch(e->command.matches("^\\b" + e + "\\b(.|\n)*?$"))) return false;
        if (canUseCommand(sender, command)) {
            final ConsoleSource commandSender = type.getCommandSource(sender, command);
            toConsole(commandSender, command);
            return true;
        } else {
            sendPermissionError(sender);
            return false;
        }
    }

    public static void toConsole(ConsoleSource commandSender, String command) {
        Bukkit.getScheduler().callSyncMethod(DiscordLink.getInstance(), () -> Bukkit.dispatchCommand(commandSender, command));
    }

    private static boolean canUseCommand(GuildMember sender, String command){
        return sender.hasRole(Roles.DIRTY) ||
               sender.hasRole(Roles.ADMIN) &&
               blacklist.stream().noneMatch(e->command.matches("^\\b" + e + "\\b(.|\n)*?$"));
    }

    public static void sendPermissionError(MessageSource event){
        event.sendCommandResponse("<@" + event.getUser().getId() + ">, you do **not** have permission to use this command!", 5);
        logCommand(event, "__Tried Executing Command__");
    }

    public static void sendCommandError(MessageSource event, String msg){
        event.sendCommandResponse("<@" + event.getUser().getId() + ">, " + msg, 5);
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

    public static BaseComponent[] format(String unformattedString) {
        unformattedString = unformattedString.replaceAll("&([0-9a-fklmnor])", "§$1");
        return TextComponent.fromLegacyText(unformattedString);
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

    public static String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }
}
