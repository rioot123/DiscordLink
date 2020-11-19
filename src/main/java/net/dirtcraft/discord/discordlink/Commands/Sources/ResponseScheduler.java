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

    public static void submit(DiscordResponder provider, String message) {
        instance.tasks.add(new Message(provider, message));
    }

    private static class Message {
        final DiscordResponder provider;
        final String message;
        private Message(DiscordResponder provider, String message){
            this.message = message;
            this.provider = provider;
        }
    }

    private class Messenger extends TimerTask {
        @Override
        public void run() {
            Multimap<DiscordResponder, String> messages = ArrayListMultimap.create();
            while (!tasks.isEmpty()){
                Message message = tasks.poll();
                messages.put(message.provider, message.message);
            }
            messages.keySet().forEach(provider -> dispatchMessages(provider, messages.get(provider)));
        }

        private void dispatchMessages(DiscordResponder provider, Collection<String> messages){
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
}