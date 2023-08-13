// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord.notify;

import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class Time implements DiscordCommandExecutor
{
    public void execute(final MessageSource source, final String command, final List<String> args) throws DiscordCommandException {
        if (args.isEmpty()) {
            source.sendCommandResponse("Boot-stall notificator", "notify time is currently " + PluginConfiguration.Notifier.maxStageMinutes + "minutes.", 30);
            return;
        }
        try {
            PluginConfiguration.Notifier.maxStageMinutes = Long.parseLong(args.get(0));
            DiscordLink.get().saveConfig();
            source.sendCommandResponse("Command successfully executed", "notify time has been set to " + PluginConfiguration.Notifier.maxStageMinutes + "minutes.", 30);
        }
        catch (Exception e) {
            throw new DiscordCommandException("Invalid input type!");
        }
    }
}
