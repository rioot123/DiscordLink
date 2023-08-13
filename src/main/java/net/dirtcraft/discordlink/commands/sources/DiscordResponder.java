// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.sources;

import org.spongepowered.api.text.Text;
import java.util.function.Consumer;

public interface DiscordResponder
{
    void sendDiscordResponse(final String p0);
    
    int getCharLimit();
    
    boolean sanitise();
    
    default DiscordResponder getSender(final Consumer<String> output, final int limit, final boolean sanitise) {
        return new DiscordResponder() {
            @Override
            public void sendDiscordResponse(final String message) {
                output.accept(message);
            }
            
            @Override
            public int getCharLimit() {
                return limit;
            }
            
            @Override
            public boolean sanitise() {
                return sanitise;
            }
        };
    }
    
    default ConsoleSender getSender(final DiscordResponder console) {
        return (ConsoleSender)((console instanceof ConsoleSender) ? console : new ConsoleSender(console));
    }
    
    public static class ConsoleSender extends ConsoleSource implements DiscordResponder
    {
        final DiscordResponder scheduledSender;
        
        protected ConsoleSender(final DiscordResponder scheduledSender) {
            this.scheduledSender = scheduledSender;
        }
        
        public void sendMessage(final Text message) {
            ResponseScheduler.submit(this, message.toPlain());
        }
        
        @Override
        public void sendDiscordResponse(final String message) {
            this.scheduledSender.sendDiscordResponse(message);
        }
        
        @Override
        public int getCharLimit() {
            return this.scheduledSender.getCharLimit();
        }
        
        @Override
        public boolean sanitise() {
            return this.scheduledSender.sanitise();
        }
    }
}
