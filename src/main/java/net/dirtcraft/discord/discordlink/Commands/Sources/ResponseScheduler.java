package net.dirtcraft.discord.discordlink.Commands.Sources;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import litebans.api.Events;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ResponseScheduler {
    public static final ResponseScheduler instance = new ResponseScheduler();
    final Queue<Message> tasks = new ConcurrentLinkedQueue<>();
    final Timer timer = new Timer();

    private ResponseScheduler() {
        timer.scheduleAtFixedRate(new Messenger(), 1250, 1250);
    }

    public static void submit(ScheduledSender provider, String message) {
        instance.tasks.add(new Message(provider, message));
    }

    private static class Message {
        final ScheduledSender provider;
        final String message;
        private Message(ScheduledSender provider, String message){
            this.message = message;
            this.provider = provider;
        }
    }

    private class Messenger extends TimerTask {
        @Override
        public void run() {
            Multimap<ScheduledSender, String> messages = ArrayListMultimap.create();
            while (!tasks.isEmpty()){
                Message message = tasks.poll();
                messages.put(message.provider, message.message);
            }
            messages.keySet().forEach(provider -> dispatchMessages(provider, messages.get(provider)));
        }

        private void dispatchMessages(ScheduledSender provider, Collection<String> messages){
            StringBuilder output = new StringBuilder();
            for (String message : messages){
                if (provider.sanitise()) message = Utility.sanitizeMinecraftText(message);
                else message = Utility.stripColorCodes(message);
                if (output.length() + message.length() > provider.getCharLimit()){
                    provider.sendDiscordResponse(output.toString());
                    output = new StringBuilder(message);
                } else {
                    output.append(output.length() > 0? "\n" : "");
                    output.append(message);
                }
            }
            if (output.length() > 0) provider.sendDiscordResponse(output.toString());
        }
    }

    public static void liteBansCallback(WrappedConsole console, String username){
        instance.registerLiteBansCallback(console, username);
    }

    private void registerLiteBansCallback(WrappedConsole console, String username){
        LiteBanListener liteBanListener = new LiteBanListener(console, username);
        Events.get().register(liteBanListener);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Events.get().unregister(liteBanListener);
            }
        }, 60_000);
    }


    private static class LiteBanListener extends Events.Listener {
        final WrappedConsole console;
        final String username;

        private LiteBanListener(WrappedConsole console, String username) {
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
}