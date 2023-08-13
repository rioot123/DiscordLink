// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.utility;

import java.util.Random;
import java.util.function.Consumer;
import org.apache.commons.lang3.exception.ExceptionUtils;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.requests.RestAction;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.Arrays;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import net.dirtcraft.discordlink.commands.sources.ConsoleSource;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.discordlink.channels.MessageIntent;
import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.discord.spongediscordlib.DiscordUtil;
import net.dv8tion.jda.api.entities.Activity;
import net.dirtcraft.discordlink.users.GuildMember;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRole;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.discordlink.users.UserManagerImpl;
import java.util.concurrent.CompletableFuture;
import net.dirtcraft.spongediscordlib.users.platform.PlatformPlayer;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import java.time.temporal.TemporalAccessor;
import java.time.Instant;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Member;
import java.util.Optional;
import net.dirtcraft.discordlink.DiscordLink;
import net.dv8tion.jda.api.entities.Guild;

public class Utility
{
    public static final String STRIP_CODE_REGEX = "[§&]([0-9a-fA-FrlonmkRLONMK])";
    public static final String URL_DETECT_REGEX = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
    
    private static Guild getGuild() {
        return DiscordLink.get().getChannelManager().getGuild();
    }
    
    public static Optional<Member> getMemberById(final String id) {
        try {
            return Optional.of(getGuild().retrieveMemberById(id).complete());
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public static Optional<Member> getMemberById(final long id) {
        try {
            return Optional.of(getGuild().retrieveMemberById(id).complete());
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public static Optional<Member> getMember(final User user) {
        try {
            return Optional.of(getGuild().retrieveMember(user).complete());
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public static EmbedBuilder embedBuilder() {
        final EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle(PluginConfiguration.Embed.title);
        if (PluginConfiguration.Embed.timestamp) {
            embed.setTimestamp((TemporalAccessor)Instant.now());
        }
        return embed;
    }
    
    public static void setTopic() {
        final TextChannel channel = DiscordLink.get().getChannelManager().getDefaultChannel();
        if (SpongeDiscordLib.getServerName().toLowerCase().contains("pixel")) {
            final String name = SpongeDiscordLib.getServerName().split(" ")[1];
            final String s;
            String code = s = SpongeDiscordLib.getServerName().toLowerCase().split(" ")[1];
            switch (s) {
                case "redstone": {
                    code = "red";
                    break;
                }
                case "glowstone": {
                    code = "glow";
                    break;
                }
            }
            channel.getManager().setTopic("**Pixelmon " + name + "** \u2014 IP: " + code + ".pixelmon.gg").queue();
            return;
        }
        final String code2 = channel.getName().split("-")[1];
        channel.getManager().setTopic("ModPack: **" + SpongeDiscordLib.getServerName() + "** \u2014 IP: " + code2 + ".dirtcraft.gg").queue();
    }
    
    public static void setRoles(final PlatformPlayer player) {
        final DiscordLink discordLink = DiscordLink.get();
        final UserManagerImpl userManager = discordLink.getUserManager();
        CompletableFuture.runAsync(() -> userManager.getMember(player.getUUID()).ifPresent(member -> setRoles(player, member)));
    }
    
    public static void setRoles(final PlatformPlayer player, final DiscordMember member) {
        if (player.hasPermission("discordlink.roles.donor")) {
            member.setRoleIfAbsent(DiscordRoles.DONOR);
        }
        if (!member.getRoles().contains(DiscordRoles.STAFF.getRole())) {
            member.tryChangeNickname(player.getName());
        }
        member.setRoleIfAbsent(DiscordRoles.VERIFIED);
    }
    
    public static void removeRoleIfPresent(final long id, final DiscordRole role) {
        try {
            final DiscordLink discordLink = DiscordLink.get();
            final UserManagerImpl userManager = discordLink.getUserManager();
            final Guild guild = discordLink.getChannelManager().getGuild();
            final Member discord = (Member)guild.retrieveMemberById(id).complete();
            if (discord == null) {
                return;
            }
            final GuildMember member = userManager.getMember(discord);
            member.removeRoleIfPresent(role);
        }
        catch (Exception ex) {}
    }
    
    public static void setStatus() {
        DiscordUtil.setStatus(Activity.ActivityType.STREAMING, SpongeDiscordLib.getServerName(), "https://www.twitch.tv/dirtcraft/");
    }
    
    public static boolean toConsole(final String command, final MessageSourceImpl sender, final MessageIntent type) {
        if (PluginConfiguration.Command.ignored.stream().anyMatch(e -> command.matches("^\\b" + e + "\\b(.|\n)*?$"))) {
            return false;
        }
        if (canUseCommand(sender, command)) {
            final ConsoleSource commandSender = type.getCommandSource(sender, command);
            toConsole(commandSender, command);
            return true;
        }
        sendPermissionError((MessageSource)sender);
        return false;
    }
    
    public static void toConsole(final ConsoleSource commandSender, final String command) {
        Task.builder().execute(() -> Sponge.getCommandManager().process((CommandSource)commandSender, command)).submit((Object)DiscordLink.get());
    }
    
    private static boolean canUseCommand(final GuildMember sender, final String command) {
        return sender.hasRole(DiscordRoles.DIRTY) || (sender.hasRole(DiscordRoles.ADMIN) && sender.hasInGamePermission("discordlink.console") && PluginConfiguration.Command.blacklist.stream().noneMatch(e -> command.matches("^\\b" + e + "\\b(.|\n)*?$")));
    }
    
    public static void sendPermissionError(final MessageSource event) {
        event.sendCommandResponse("<@" + event.getUser().getId() + ">, you do **not** have permission to use this command!", 5);
        logCommand(event, "__Tried Executing Command__");
    }
    
    public static void sendCommandError(final MessageSource event, final String msg) {
        event.sendCommandResponse("<@" + event.getUser().getId() + ">, " + msg, 5);
        logCommand(event, "__Tried Executing Command__");
    }
    
    public static void logCommand(final MessageSource event, final String message) {
        DiscordLink.get().getJda().getTextChannelsByName("command-log", true).get(0).sendMessage(embedBuilder().addField(message, event.getMessage().getContentDisplay(), false).setFooter(event.getUser().getAsTag(), event.getUser().getAvatarUrl()).build()).queue();
    }
    
    public static Text format(final String unformattedString) {
        return TextSerializers.FORMATTING_CODE.deserialize(unformattedString);
    }
    
    public static String sanitiseMinecraftText(final String s) {
        return s.replaceAll("[§&]([0-9a-fA-FrlonmkRLONMK])", "").replace("@everyone", "").replace("@here", "").replaceAll("([_*~`>|\\\\])", "\\\\$1").replaceAll("<@\\d+>", "");
    }
    
    public static void dmExceptionAsync(final Exception e, final long... id) {
        CompletableFuture.runAsync(() -> dmException(e, id));
    }
    
    private static void dmException(final Exception e, final long... id) {
        Arrays.stream(id).mapToObj((LongFunction<?>)Utility::getMemberById).filter(Optional::isPresent).map((Function<? super Object, ?>)Optional::get).map((Function<? super Object, ?>)Member::getUser).map((Function<? super Object, ?>)User::openPrivateChannel).forEach(dm -> dmException(dm, e));
    }
    
    private static void dmException(final RestAction<PrivateChannel> dms, final Exception e) {
        final String[] ex = ExceptionUtils.getStackTrace((Throwable)e).split("\\r?\\n");
        sendMessages(s -> dms.queue(dm -> dm.sendMessage((CharSequence)s).queue()), 1980, ex);
    }
    
    public static void sendMessages(final Consumer<String> destination, final int limit, final String... messages) {
        StringBuilder sb = new StringBuilder();
        for (final String s : messages) {
            if (sb.length() + s.length() < limit) {
                sb.append(s);
                sb.append("\n");
            }
            else {
                destination.accept(sb.toString());
                sb = new StringBuilder(s);
            }
        }
        if (sb.length() > 0) {
            destination.accept(sb.toString());
        }
    }
    
    public static String getSaltString() {
        final String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        final StringBuilder salt = new StringBuilder();
        final Random rnd = new Random();
        while (salt.length() < 6) {
            final int index = (int)(rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }
    
    public static void trySleep(final long ms) {
        try {
            Thread.sleep(ms);
        }
        catch (Exception ex) {}
    }
}
