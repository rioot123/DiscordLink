// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.sources;

import java.util.Iterator;
import net.dirtcraft.discordlink.utility.Utility;
import com.google.common.collect.Multimap;
import java.util.Collection;
import com.google.common.collect.ArrayListMultimap;
import java.util.TimerTask;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;

public class ResponseScheduler
{
    public static final ResponseScheduler instance;
    final Queue<Message> tasks;
    
    private ResponseScheduler() {
        this.tasks = new ConcurrentLinkedQueue<Message>();
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new Messenger(), 1000L, 1000L);
    }
    
    public static void submit(final DiscordResponder provider, final String message) {
        ResponseScheduler.instance.tasks.add(new Message(provider, message));
    }
    
    static {
        instance = new ResponseScheduler();
    }
    
    private static class Message
    {
        final DiscordResponder provider;
        final String message;
        
        private Message(final DiscordResponder provider, final String message) {
            this.message = message;
            this.provider = provider;
        }
    }
    
    private class Messenger extends TimerTask
    {
        @Override
        public void run() {
            final Multimap<DiscordResponder, String> messages = (Multimap<DiscordResponder, String>)ArrayListMultimap.create();
            while (!ResponseScheduler.this.tasks.isEmpty()) {
                final Message message = ResponseScheduler.this.tasks.poll();
                messages.put((Object)message.provider, (Object)message.message);
            }
            messages.keySet().forEach(provider -> this.dispatchMessages(provider, messages.get((Object)provider)));
        }
        
        private void dispatchMessages(final DiscordResponder provider, final Collection<String> messages) {
            StringBuilder output = new StringBuilder();
            for (String message : messages) {
                if (provider.sanitise()) {
                    message = Utility.sanitiseMinecraftText(message);
                }
                if (output.length() + message.length() > provider.getCharLimit()) {
                    provider.sendDiscordResponse(output.toString());
                    output = new StringBuilder(message);
                }
                else {
                    output.append((output.length() > 0) ? "\n" : "");
                    output.append(message);
                }
            }
            if (output.length() > 0) {
                provider.sendDiscordResponse(output.toString());
            }
        }
    }
}
