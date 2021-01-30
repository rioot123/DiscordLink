package net.dirtcraft.discordlink.commands.discord;

import net.dirtcraft.discordlink.users.MessageSource;
import net.dirtcraft.discordlink.users.discord.Roles;
import net.dirtcraft.discordlink.api.commands.DiscordCommandExecutor;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.api.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.users.platform.PlatformUserImpl;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.List;
import java.util.Optional;

public class Unstuck implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String cmd, List<String> args) throws DiscordCommandException {
        Optional<PlatformUserImpl> target = args.isEmpty() || !source.hasRole(Roles.STAFF)? source.getPlayerData() : PlatformProvider.getPlayerOffline(args.get(0));
        String name = target.flatMap(PlatformUserImpl::getNameIfPresent).orElseThrow(()->new DiscordCommandException("Could not locate players UUID."));
        Task.builder()
                .execute(() -> {
                    String command = String.format("spawn other %s", name);
                    Sponge.getCommandManager().process(source.getCommandSource(command), command);
                }).submit(DiscordLink.get());
    }
}
