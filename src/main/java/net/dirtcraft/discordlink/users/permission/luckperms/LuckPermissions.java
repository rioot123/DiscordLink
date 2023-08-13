// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.users.permission.luckperms;

import net.dirtcraft.spongediscordlib.users.MessageSource;
import org.spongepowered.api.command.CommandSource;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.command.source.ConsoleSource;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;

public abstract class LuckPermissions extends PermissionProvider
{
    protected abstract String getServerContext();
    
    @Override
    public void setPlayerPrefix(final ConsoleSource source, final User target, final String prefix) {
        PlatformProvider.toConsole((CommandSource)source, String.format("lp user %s meta setprefix 10000 \"%s\" server=%s", target.getName(), prefix, this.getServerContext()));
    }
    
    @Override
    public void clearPlayerPrefix(final ConsoleSource source, final User target) {
        PlatformProvider.toConsole((CommandSource)source, String.format("lp user %s meta clear prefix server=%s", target.getName(), this.getServerContext()));
    }
    
    @Override
    public void setPlayerPrefix(final MessageSource source, final User target, final String prefix) {
        final String command = String.format("lp user %s meta setprefix 10000 \"%s\" server=%s", target.getName(), prefix, this.getServerContext());
        final ConsoleSource sender = source.getCommandSource(command);
        PlatformProvider.toConsole((CommandSource)sender, command);
    }
    
    @Override
    public void clearPlayerPrefix(final MessageSource source, final User target) {
        final String command = String.format("lp user %s meta clear prefix server=%s", target.getName(), this.getServerContext());
        final ConsoleSource sender = source.getCommandSource(command);
        PlatformProvider.toConsole((CommandSource)sender, command);
    }
}
