package net.dirtcraft.discord.discordlink.Utility.Compatability.Sanctions.LiteBans;

import litebans.api.Events;
import net.dirtcraft.discord.discordlink.API.Channel;
import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.Commands.Sources.DiscordResponder;
import net.dirtcraft.discord.discordlink.Commands.Sources.ResponseScheduler;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Sanctions.SanctionUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

public class LiteBans extends SanctionUtils {
    private final Timer timer = new Timer();
    private final Channel sanctions = Channels.getDefaultChat();
    private final DiscordResponder responder = sanctions.getChatResponder();

    public LiteBans() {
        Events.get().register(new Events.Listener() {
            @Override
            public void broadcastSent(@NotNull String message, @Nullable String type) {
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

    private void executeSanction(SanctionCommand sanction, MessageSource source, String command, boolean bypass) {
        final ConsoleSource sender = source.getCommandSource(command);
        final Optional<SanctionCommand> cmd;
        if (sanction != null) {
            PluginManager manager = ProxyServer.getInstance().getPluginManager();
            liteBansCallback(sender, sanction.executorName);
            manager.dispatchCommand(sender, sanction.command);
        } else if (bypass && (cmd = formatSanctionCommand(command, source.getEffectiveName())).isPresent()) {
            PluginManager manager = ProxyServer.getInstance().getPluginManager();
            sender.sendMessage("Failed to find UUID/IGN. Sending as " + cmd.get().executorName);
            liteBansCallback(sender, "Console");
            manager.dispatchCommand(sender, cmd.get().command);
        } else if (bypass) {
            PluginManager manager = ProxyServer.getInstance().getPluginManager();
            sender.sendMessage("Failed to find UUID/IGN. Sending as CONSOLE");
            liteBansCallback(sender, "Console");
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


    private static class LiteBanListener extends Events.Listener {
        final ConsoleSource console;
        final String username;

        private LiteBanListener(ConsoleSource console, String username) {
            this.username = username;
            this.console = console;
        }

        @Override
        public void broadcastSent(@NotNull String message, @Nullable String type) {
            if (!message.matches("(?i)^([&§][0-9a-f])?" + username + ".*")) return;
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
