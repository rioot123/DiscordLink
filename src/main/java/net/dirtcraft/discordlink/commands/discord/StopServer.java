// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord;

import java.util.concurrent.CompletableFuture;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.discordlink.DiscordLink;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class StopServer implements DiscordCommandExecutor
{
    private final boolean gracefulExit;
    
    public StopServer(final boolean graceful) {
        this.gracefulExit = graceful;
    }
    
    public void execute(final MessageSource source, final String command, final List<String> args) {
        try {
            if (!source.isPrivateMessage()) {
                source.getMessage().delete().queue(s -> {}, e -> {});
            }
            else {
                source.sendCommandResponse("Forced Reboot Scheduled.", "Attempting to reboot the server.");
            }
            DiscordLink.get().getChannelManager().getGameChat().sendMessage((DiscordMember)source, "Forced Reboot Scheduled.", "Attempting to reboot the server.");
            Thread.sleep(555L);
        }
        catch (Throwable t) {}
        finally {
            CompletableFuture.runAsync(() -> FMLCommonHandler.instance().exitJava(-1, !this.gracefulExit));
        }
    }
}
