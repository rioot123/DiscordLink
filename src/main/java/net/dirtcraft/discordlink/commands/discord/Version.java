// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord;

import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class Version implements DiscordCommandExecutor
{
    public void execute(final MessageSource source, final String command, final List<String> args) throws DiscordCommandException {
        String version = "Discord-Link: 2.0.1";
        version = version + "\nPermission System: " + PermissionProvider.VERSION;
        version = version + "\nPlatform: " + PlatformProvider.VERSION;
        source.sendCommandResponse("Version Data", version);
    }
}
