package net.dirtcraft.discordlink.common.utility;

import net.dirtcraft.discordlink.forge.DiscordLink;
import net.dirtcraft.discordlink.common.channels.MessageIntent;
import net.dirtcraft.discordlink.common.commands.sources.ConsoleSource;
import net.dirtcraft.discordlink.common.storage.Permission;
import net.dirtcraft.discordlink.common.storage.PluginConfiguration;
import net.dirtcraft.discordlink.common.users.GuildMember;
import net.dirtcraft.discordlink.common.users.MessageSourceImpl;
import net.dirtcraft.discordlink.common.users.UserManagerImpl;
import net.dirtcraft.discordlink.api.users.DiscordMember;
import net.dirtcraft.discordlink.api.users.MessageSource;
import net.dirtcraft.discordlink.api.users.platform.PlatformPlayer;
import net.dirtcraft.discordlink.api.users.roles.DiscordRole;
import net.dirtcraft.discordlink.api.users.roles.DiscordRoles;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Utility {

    private static JDA jda = DiscordLink.get().getJda();
    public static final String STRIP_CODE_REGEX = "[§&]([0-9a-fA-FrlonmkRLONMK])";
    public static final String URL_DETECT_REGEX = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";

    private static Guild getGuild(){
        return DiscordLink.get()
                .getChannelManager()
                .getGuild();
    }

    public static Optional<Member> getMemberById(String id){
        try {
            return Optional.of(getGuild().retrieveMemberById(id).complete());
        } catch (Exception e){
            return Optional.empty();
        }
    }

    public static Optional<Member> getMemberById(long id){
        try {
            return Optional.of(getGuild().retrieveMemberById(id).complete());
        } catch (Exception e){
            return Optional.empty();
        }
    }

    public static Optional<Member> getMember(User user){
        try {
            return Optional.of(getGuild().retrieveMember(user).complete());
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
        String svrName = PluginConfiguration.Main.SERVER_NAME;
        TextChannel channel = DiscordLink.get().getChannelManager().getDefaultChannel();
        if (svrName.toLowerCase().contains("pixel")) {
            String name = svrName.split(" ")[1];
            String code = svrName.toLowerCase().split(" ")[1];
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
                .setTopic("ModPack: **" + svrName + "** — IP: " + code + ".dirtcraft.gg")
                .queue();
    }

    public static void setRoles(PlatformPlayer player){
        DiscordLink discordLink = DiscordLink.get();
        UserManagerImpl userManager = discordLink.getUserManager();
        CompletableFuture.runAsync(()-> userManager.getMember(player.getUUID())
                .ifPresent(member->setRoles(player, member)));
    }

    public static void setRoles(PlatformPlayer player, DiscordMember member) {
        if (player.hasPermission(Permission.ROLES_DONOR)) member.setRoleIfAbsent(DiscordRoles.DONOR);
        if (!member.getRoles().contains(DiscordRoles.STAFF.getRole())) member.tryChangeNickname(player.getName());
        member.setRoleIfAbsent(DiscordRoles.VERIFIED);
    }

    public static void removeRoleIfPresent(long id, DiscordRole role){
        try {
            DiscordLink discordLink = DiscordLink.get();
            UserManagerImpl userManager = discordLink.getUserManager();
            Guild guild = discordLink.getChannelManager().getGuild();
            Member discord = guild.retrieveMemberById(id).complete();
            if (discord == null) return;
            GuildMember member = userManager.getMember(discord);
            member.removeRoleIfPresent(role);
        } catch (Exception ignored){}
    }

    public static void setStatus() {
        setStatus(Activity.ActivityType.STREAMING, PluginConfiguration.Main.SERVER_NAME, "https://www.twitch.tv/dirtcraft/");
    }

    public static boolean toConsole(String command, MessageSourceImpl sender, MessageIntent type) {
        if (PluginConfiguration.Command.ignored.stream().anyMatch(e->command.matches("^\\b" + e + "\\b(.|\n)*?$"))) return false;
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
        //todo platform-specific implementation
        //Task.builder()
        //        .execute(() -> Sponge.getCommandManager().process(commandSender, command))
        //        .submit(DiscordLink.get());
    }

    private static boolean canUseCommand(GuildMember sender, String command){
        return sender.hasRole(DiscordRoles.DIRTY) ||
               sender.hasRole(DiscordRoles.ADMIN) && sender.hasInGamePermission(Permission.CONSOLE) &&
               PluginConfiguration.Command.blacklist.stream().noneMatch(e->command.matches("^\\b" + e + "\\b(.|\n)*?$"));
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
        DiscordLink.get().getJda()
                .getTextChannelsByName("command-log", true).get(0)
                .sendMessage(Utility.embedBuilder()
                        .addField(message, event.getMessage().getContentDisplay(), false)
                        .setFooter(event.getUser().getAsTag(), event.getUser().getAvatarUrl())
                        .build())
                .queue();
    }

    public static String sanitiseMinecraftText(String s){
        return s.replaceAll(STRIP_CODE_REGEX, "")
                .replace("@everyone", "")
                .replace("@here", "")
                .replaceAll("([_*~`>|\\\\])", "\\\\$1")
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

    public static void trySleep(long ms){
        try{
            Thread.sleep(ms);
        } catch (Exception ignored){

        }
    }

    public static void setStatus(Activity.ActivityType type, String name, String url) {
        if (url != null) {
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(type, name, url));
        } else {
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(type, name));
        }
    }
}
