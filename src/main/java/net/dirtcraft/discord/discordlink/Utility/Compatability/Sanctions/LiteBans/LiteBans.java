package net.dirtcraft.discord.discordlink.Utility.Compatability.Sanctions.LiteBans;

import litebans.api.Entry;
import litebans.api.Events;
import net.dirtcraft.discord.discordlink.API.*;
import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.Commands.Sources.DiscordResponder;
import net.dirtcraft.discord.discordlink.Commands.Sources.ResponseScheduler;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.Database;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Storage.tables.Mutes;
import net.dirtcraft.discord.discordlink.Storage.tables.Verification;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Sanctions.SanctionUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.RegEx;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.dirtcraft.discord.discordlink.Commands.Discord.Mute.Mute.getDuration;

public class LiteBans extends SanctionUtils {
    private Channel sanctions;
    private DiscordResponder responder;
    private Database storage;

    public LiteBans(Channel channel) {
        this.sanctions = channel;
        this.responder = sanctions.getChatResponder();
        this.storage = DiscordLink.getInstance().getStorage();
        Events.get().register(new Events.Listener() {
            @Override
            public void entryAdded(Entry entry){
                String type = entry.getType();
                if (type.equals("mute")) timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Optional.ofNullable(entry.getUuid())
                                .filter(uuid->uuid.matches("(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"))
                                .map(UUID::fromString)
                                .flatMap(storage::getVerificationData)
                                .flatMap(Verification.VerificationData::getGuildMember)
                                .ifPresent(user->muteDiscordPlayer(entry, user));
                    }
                }, 0);
            }
            @Override
            public void entryRemoved(Entry entry){
                String type = entry.getType();
                if (type.equals("mute")) timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Optional.ofNullable(entry.getUuid())
                                .filter(uuid->uuid.matches("(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"))
                                .map(UUID::fromString)
                                .flatMap(storage::getVerificationData)
                                .flatMap(Verification.VerificationData::getGuildMember)
                                .ifPresent(user->unmuteDiscordPlayer(entry, user));
                    }
                }, 0);
            }

            @Override
            public void broadcastSent(@NotNull String message, @Nullable String type) {
                message = Utility.stripColorCodes(message);
                message = formatBanMessage(message);
                ResponseScheduler.submit(responder, "``" + message + "``");
            }
        });
    }

    @Override
    public void sanction(MessageSource source, String command, boolean bypass){
        CompletableFuture
                .supplyAsync(()->formatSanctionCommand(source, command))
                .thenAccept(s->executeSanction(s.orElse(null), source, command, bypass))
                .exceptionally(e->{
                    e.printStackTrace();
                    return null;
                });
    }

    public void muteDiscordPlayer(Entry entry, GuildMember member){
        UUID staff = entry.getExecutorUUID() == null? null: UUID.fromString(entry.getExecutorUUID());
        Timestamp expires = Timestamp.from(Instant.ofEpochMilli(entry.getDateEnd()));
        Optional<Mutes.MuteData> current = storage.hasActiveMute(member.getIdLong());
        if (current.isPresent() && current.get().getSubmitter().isPresent() && current.get().getExpires().map(ex->ex.after(expires)).orElse(true)) return;
        String reason = entry.getReason();
        storage.deactivateMute(staff, member.getIdLong());
        storage.registerMute(staff, member, expires, reason);
        Utility.setRoleIfAbsent(Channels.getGuild(), member, Roles.MUTED);
        String message = "You have been muted by " + entry.getExecutorName() + " in-game for " +
                "```\n" + reason + "\n```" +
                "\nThe mute " + getDuration(expires) + ". It has been applied to your linked discord account as well." +
                "\nFeel free to make an appeal in <#590388043379376158>.";
        member.sendMessage(message);
    }

    public void unmuteDiscordPlayer(Entry entry, GuildMember member){
        UUID staff = entry.getExecutorUUID() == null? null: UUID.fromString(entry.getExecutorUUID());
        Timestamp expires = Timestamp.from(Instant.ofEpochMilli(entry.getDateEnd()));
        Optional<Mutes.MuteData> current = storage.hasActiveMute(member.getIdLong());
        if (current.isPresent() && current.get().getExpires().map(ex->ex.after(expires)).orElse(true)) return;
        storage.deactivateMute(staff, member.getIdLong());
        Utility.removeRoleIfPresent(Channels.getGuild(), member, Roles.MUTED);
    }

    @Override
    public void updateChannel(long id) {
        sanctions = new Channel(id);
        responder = sanctions.getChatResponder();
    }

    private void executeSanction(SanctionCommand sanction, MessageSource source, String command, boolean bypass) {
        final ConsoleSource sender = source.getCommandSource(command);
        final Optional<SanctionCommand> cmd;
        if (sanction != null) {
            PluginManager manager = ProxyServer.getInstance().getPluginManager();
            if (!source.getChannel().equals(sanctions)) liteBansCallback(sender, sanction.executorName);
            manager.dispatchCommand(sender, sanction.command);
        } else if (bypass && (cmd = formatSanctionCommand(command, source.getEffectiveName())).isPresent()) {
            PluginManager manager = ProxyServer.getInstance().getPluginManager();
            sender.sendMessage("Failed to find UUID/IGN. Sending as " + cmd.get().executorName);
            if (!source.getChannel().equals(sanctions)) liteBansCallback(sender, "Console");
            manager.dispatchCommand(sender, cmd.get().command);
        } else if (bypass) {
            PluginManager manager = ProxyServer.getInstance().getPluginManager();
            sender.sendMessage("Failed to find UUID/IGN. Sending as CONSOLE");
            if (!source.getChannel().equals(sanctions)) liteBansCallback(sender, "Console");
            manager.dispatchCommand(sender, command);
        } else {
            sender.sendMessage("Failed to execute \"" + command + "\"\nUUID/IGN not found. (Are you verified?)");
        }
    }

    private void liteBansCallback(ConsoleSource console, String username){
        LiteBanListener liteBanListener = new LiteBanListener(console, username);
        Events.get().register(liteBanListener);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Events.get().unregister(liteBanListener);
            }
        }, 60_000);
    }

    private Optional<SanctionCommand> formatSanctionCommand(MessageSource source, String command){
        PlatformUser user = source.getPlayerData().orElse(null);
        String commandBase = PluginConfiguration.Command.sanctions.stream().filter(command::startsWith).findFirst().orElse(null);
        String args = commandBase == null? null: command.substring(commandBase.length());

        if (commandBase == null || user == null) return Optional.empty();
        else {
            String name = user.getName().orElse(source.getEffectiveName());
            String uuid = user.getUUID().toString();
            String formatted = String.format("%s --sender-uuid=%s --sender=%s %s", commandBase, uuid, name, args);
            return Optional.of(new SanctionCommand(formatted, name.replace(" ", "_")));
        }
    }

    private Optional<SanctionCommand> formatSanctionCommand(String command, String name){
        String commandBase = PluginConfiguration.Command.sanctions.stream().filter(command::startsWith).findFirst().orElse(null);
        String args = commandBase == null? null: command.substring(commandBase.length());

        if (commandBase == null) return Optional.empty();
        else {
            String formatted = String.format("%s --sender=%s %s", commandBase, name.replace(" ", "_"), args);
            return Optional.of(new SanctionCommand(formatted, name));
        }
    }

    private String match(@RegEx String regex, String s){
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);
        if (m.find() && m.groupCount() > 0) return m.group(1);
        else return "";
    }

    private String formatBanMessage(String message){
        String reason = match("(?m)(\\nReason » .*)", message).replaceAll("}$", "");
        String duration = match("(?m)(\\nDuration » .*)", message).replaceAll("}$", "");
        message = message.replaceAll("(?is)\\{hovertext:.*?}", "");
        return message.replaceAll("(?im)\\n» Hover for more info", reason + duration);
    }

    private class LiteBanListener extends Events.Listener {
        final ConsoleSource console;
        final String username;

        private LiteBanListener(ConsoleSource console, String username) {
            this.username = username;
            this.console = console;
        }

        @Override
        public void broadcastSent(@NotNull String message, @Nullable String type) {
            message = Utility.stripColorCodes(message);
            Pattern pattern = Pattern.compile("(?im)^» " + username);
            if (!pattern.matcher(message).find()) return;
            message = formatBanMessage(message);
            Events.get().unregister(this);
            console.sendMessage(message);
        }
    }

    private static class SanctionCommand{
        final private String command;
        final private String executorName;

        private SanctionCommand(String command, String executorName) {
            this.executorName = executorName;
            this.command = command;
        }
    }
}
