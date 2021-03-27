package net.dirtcraft.discordlink.common.users.permission.luckperms;

import net.dirtcraft.discordlink.api.users.platform.PlatformUser;
import net.dirtcraft.discordlink.common.commands.sources.ConsoleSource;
import net.dirtcraft.discordlink.common.users.permission.PermissionProvider;
import net.dirtcraft.discordlink.forge.platform.PlatformProvider;
import net.dirtcraft.discordlink.api.users.MessageSource;

public abstract class LuckPermissions extends PermissionProvider {

    public LuckPermissions(PlatformProvider provider){
        this.platformProvider = provider;
    }

    protected abstract String getServerContext();
    protected PlatformProvider platformProvider;

    @Override
    public void setPlayerPrefix(ConsoleSource source, PlatformUser target, String prefix){
        platformProvider.toConsole(source, String.format("lp user %s meta setprefix 10000 \"%s\" server=%s", target.getName(), prefix, getServerContext()));
    }

    @Override
    public void clearPlayerPrefix(ConsoleSource source, PlatformUser target){
        platformProvider.toConsole(source, String.format("lp user %s meta clear prefix server=%s", target.getName(), getServerContext()));
    }

    @Override
    public void setPlayerPrefix(MessageSource source, PlatformUser target, String prefix){
        String command = String.format("lp user %s meta setprefix 10000 \"%s\" server=%s", target.getName(), prefix, getServerContext());
        ConsoleSource sender = source.getCommandSource(command);
        platformProvider.toConsole(sender, command);
    }

    @Override
    public void clearPlayerPrefix(MessageSource source, PlatformUser target){
        String command = String.format("lp user %s meta clear prefix server=%s", target.getName(), getServerContext());
        ConsoleSource sender = source.getCommandSource(command);
        platformProvider.toConsole(sender, command);
    }
}
