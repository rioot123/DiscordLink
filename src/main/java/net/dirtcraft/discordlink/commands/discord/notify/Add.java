// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord.notify;

import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class Add implements DiscordCommandExecutor
{
    public void execute(final MessageSource source, final String command, final List<String> args) throws DiscordCommandException {
        if (PluginConfiguration.Notifier.notify.contains(source.getIdLong())) {
            throw new DiscordCommandException("User already present!");
        }
        try {
            PluginConfiguration.Notifier.notify.add(source.getIdLong());
            DiscordLink.get().saveConfig();
            source.sendCommandResponse("Command successfully executed", "Added " + source.getEffectiveName() + " to the notification list.", 30);
        }
        catch (Exception e) {
            throw new DiscordCommandException(e.getMessage());
        }
    }
}
