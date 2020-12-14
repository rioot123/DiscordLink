package net.dirtcraft.discord.discordlink.Utility;

import net.dirtcraft.discord.discordlink.API.*;
import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Permission;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformPlayer;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Sanctions.SanctionUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.PluginManager;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.dirtcraft.discord.discordlink.Storage.PluginConfiguration.Command.sanctions;
import static net.dirtcraft.discord.discordlink.Storage.PluginConfiguration.Command.whiteList;

public class Utility {

    public static final String DETECT_URL_REGEX = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
    public static final String STRIP_CODE_REGEX = "(?i)[§&][0-9a-fklmnor]";

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

    public static void setRoles(PlatformPlayer player){
        CompletableFuture.runAsync(()-> GuildMember.fromPlayerId(player.getUUID())
                .ifPresent(member->setRoles(player, member)));
    }

    public static void setRoles(PlatformPlayer player, GuildMember member) {
        Guild guild = Channels.getGuild();
        if (player.hasPermission(Permission.ROLES_DONOR)) setRoleIfAbsent(guild, member, Roles.DONOR);
        if (!member.hasRole(Roles.DIRTY) && player.hasPermission(Permission.ROLES_MANAGER)) setRoleIfAbsent(guild, member, Roles.DIRTY);
        else if (!member.hasRole(Roles.ADMIN) && player.hasPermission(Permission.ROLES_ADMIN)) setRoleIfAbsent(guild, member, Roles.ADMIN);
        else if (!member.hasRole(Roles.MOD) && player.hasPermission(Permission.ROLES_MODERATOR)) setRoleIfAbsent(guild, member, Roles.MOD);
        else if (!member.hasRole(Roles.HELPER) && player.hasPermission(Permission.ROLES_HELPER)) setRoleIfAbsent(guild, member, Roles.HELPER);
        if (!member.hasRole(Roles.STAFF) && player.hasPermission(Permission.ROLES_STAFF)) setRoleIfAbsent(guild, member, Roles.STAFF);
        if (!member.hasRole(Roles.STAFF)) tryChangeNickname(guild, member, player.getName());

        setRoleIfAbsent(guild, member, Roles.VERIFIED);
    }

    public static void setRoleIfAbsent(Guild guild, GuildMember member, Roles role){
        try {
            Role discordRole = role.getRole();
            if (discordRole == null || member.hasRole(role)) return;
            guild.addRoleToMember(member, discordRole).submit();
        } catch (Exception ignored){}
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
        } catch (Exception ignored){}
    }

    public static void removeRoleIfPresent(Guild guild, GuildMember member, Roles role){
        try {
            Role discordRole = role.getRole();
            if (discordRole == null || !member.hasRole(role)) return;
            guild.removeRoleFromMember(member, discordRole).submit();
        } catch (Exception ignored){}
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
        } catch (Exception ignored){}
    }

    public static void tryChangeNickname(Guild guild, GuildMember member, String name){
        try {
            if (name == null) return;
            guild.modifyNickname(member, name).submit();
        } catch (Exception ignored){ }
    }

    public static CommandResult toConsole(String command, MessageSource sender, Action type) {
        boolean sanction = isSanction(command);
        boolean whitelisted = isWhitelisted(command);
        if (!canUseCommand(sender, sanction, whitelisted)) {
            sendPermissionError(sender);
            return CommandResult.FAILURE;
        } else if (sanction) {
            SanctionUtils.INSTANCE.sanction(sender, command, false);
            return CommandResult.SUCCESS;
        } else if (type.isBungee() || whitelisted || sender.hasRole(Roles.DIRTY) && sender.getChannel().equals(Channels.getDefaultChat())) {
            ConsoleSource console = type.getCommandSource(sender, command);
            toConsole(console, command);
            return CommandResult.SUCCESS;
        } else if (sender.getChannel().equals(Channels.getDefaultChat())) {
            sendPermissionError(sender);
            return CommandResult.FAILURE;
        } else {
            return CommandResult.IGNORED;
        }
    }

    public static void toConsole(ConsoleSource commandSender, String command) {
        PluginManager manager = ProxyServer.getInstance().getPluginManager();
        manager.dispatchCommand(commandSender, command);
    }

    private static boolean canUseCommand(GuildMember sender, boolean sanction, boolean whitelisted){
        return (sender.hasRole(Roles.DIRTY)) || (sender.hasRole(Roles.ADMIN) && sanction || whitelisted) || sender.hasRole(Roles.MOD) && sanction;
    }

    private static boolean isSanction(String command){
        return sanctions.stream().anyMatch(command::startsWith);
    }

    private static boolean isWhitelisted(String command){
        return whiteList.stream().anyMatch(command::startsWith);
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

    public static String formatColourCodes(String s){
        return s.replaceAll("&([0-9a-fA-FrlonmkRLONMK])", "§$1");
    }

    public static String removeColourCodes(String s){
        return s.replaceAll("[§&].", "");
    }

    public static void sendPermissionErrorMessage(Channel chat, MessageReceivedEvent event){
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

    public static String sanitiseMinecraftText(String s){
        return s.replaceAll(STRIP_CODE_REGEX, "")
                .replaceAll("([_*~`>|\\\\])", "\\\\$1")
                .replace("@everyone", "")
                .replace("@here", "")
                .replaceAll("<@\\d+>", "");
    }

    public static void dmExceptionAsync(Throwable e, long... id){
        CompletableFuture.runAsync(()->dmException(e, id));
    }

    private static void dmException(Throwable e, long... id){
        Arrays.stream(id)
                .mapToObj(Utility::getMemberById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Member::getUser)
                .map(User::openPrivateChannel)
                .forEach(dm->dmException(dm, e));
    }

    private static void dmException(RestAction<PrivateChannel> dms, Throwable e){
        String[] ex = ExceptionUtils.getStackTrace(e).split("\\r?\\n");
        sendMessages(s->dms.queue(dm->dm.sendMessage(s).queue()), 1980, ex);
    }

    public static BaseComponent[] format(String s){
        s=s.replaceAll("(?i)[&]([0-9a-fklmnor])", "§$1");
        return TextComponent.fromLegacyText(s);
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

    public static boolean assignStaffRoles(PermissionUtils.RankUpdate update){
        Guild guild = Channels.getGuild();
        GuildMember member = GuildMember.fromPlayerId(update.target).orElse(null);
        if (member == null) return false;
        Role oldRole = getGroupRole(update.removed);
        Role newRole = getGroupRole(update.added);
        Role staffRole = guild.getRoleById(PluginConfiguration.Promotion.staffTag);

        if (newRole != null) guild.addRoleToMember(member, newRole).queue();
        if (oldRole != null) guild.removeRoleFromMember(member, oldRole).queue();
        if (staffRole != null && update.removed == null) guild.addRoleToMember(member, staffRole).queue();
        if (staffRole != null && update.added == null) guild.removeRoleFromMember(member, staffRole).queue();
        return true;
    }

    private static Role getGroupRole(String group){
        Long id = PluginConfiguration.Promotion.roles.getOrDefault(group, null);
        return id == null? null : Channels.getGuild().getRoleById(id);
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

    public enum CommandResult{
        SUCCESS,
        FAILURE,
        IGNORED
    }
}
