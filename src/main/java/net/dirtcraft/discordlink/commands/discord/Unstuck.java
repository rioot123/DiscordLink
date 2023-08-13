// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord;

import net.dirtcraft.discordlink.DiscordLink;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import java.util.Optional;
import java.util.function.Function;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class Unstuck implements DiscordCommandExecutor
{
    public void execute(final MessageSource source, final String cmd, final List<String> args) throws DiscordCommandException {
        final Optional<? extends PlatformUser> target = (args.isEmpty() || !source.hasRole(DiscordRoles.STAFF)) ? source.getPlayerData() : PlatformProvider.getPlayerOffline(args.get(0));
        final String name = target.flatMap((Function<? super PlatformUser, ? extends Optional<? extends String>>)PlatformUser::getNameIfPresent).orElseThrow(() -> new DiscordCommandException("Could not locate players UUID."));
        final String command;
        Task.builder().execute(() -> {
            command = String.format("spawn other %s", name);
            Sponge.getCommandManager().process((CommandSource)source.getCommandSource(command), command);
        }).submit((Object)DiscordLink.get());
    }
}
