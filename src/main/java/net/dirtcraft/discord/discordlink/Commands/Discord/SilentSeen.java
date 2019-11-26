package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.DiscordSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Commands.Sources.PrivateSender;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;

import java.util.Optional;

public class SilentSeen implements DiscordCommandExecutor {
    @Override
    public void execute(DiscordSource member, String[] args, MessageReceivedEvent event) throws DiscordCommandException {
        String target;
        if (args.length < 2){
            Optional<User> optUser = member.getSpongeUser();
            if (!optUser.isPresent()) throw new DiscordCommandException("You must be verified in order to not specify a player!");
            else target = optUser.get().getName();
        } else {
            target = args[1];
        }
        String command = "seen " + target;
        PrivateSender sender = new PrivateSender(member, command);
        Task.builder()
                .execute(() -> Sponge.getCommandManager().process(sender, command))
                .submit(DiscordLink.getInstance());
    }
}
