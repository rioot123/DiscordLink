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

public class Remove implements DiscordCommandExecutor
{
    public void execute(final MessageSource source, final String command, final List<String> args) throws DiscordCommandException {
        try {
            PluginConfiguration.Notifier.notify.remove(source.getUser().getIdLong());
            DiscordLink.get().saveConfig();
            source.sendCommandResponse("Command successfully executed", "Removed " + source.getEffectiveName() + " from the notification list.", 30);
        }
        catch (Exception e) {
            throw new DiscordCommandException(e.getMessage());
        }
    }
}
