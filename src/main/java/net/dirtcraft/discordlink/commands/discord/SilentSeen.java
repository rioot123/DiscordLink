// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord;

import org.spongepowered.api.command.source.ConsoleSource;
import net.dirtcraft.discordlink.DiscordLink;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import net.dirtcraft.discordlink.users.GuildMember;
import java.util.Optional;
import java.util.function.Function;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class SilentSeen implements DiscordCommandExecutor
{
    public void execute(final MessageSource source, final String cmd, final List<String> args) throws DiscordCommandException {
        String target;
        if (args.isEmpty()) {
            final Optional<? extends PlatformUser> optUser = (Optional<? extends PlatformUser>)source.getPlayerData();
            if (!optUser.isPresent()) {
                throw new DiscordCommandException("You must be verified in order to not specify a player!");
            }
            target = optUser.flatMap((Function<? super PlatformUser, ? extends Optional<? extends String>>)PlatformUser::getNameIfPresent).get();
        }
        else {
            target = args.get(0);
        }
        final String command = "seen " + target;
        final ConsoleSource sender = (ConsoleSource)((GuildMember)source).getPrivateSource(command);
        Task.builder().execute(() -> Sponge.getCommandManager().process((CommandSource)sender, command)).submit((Object)DiscordLink.get());
    }
}
