package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.DiscordSource;
import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Utility;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.UserManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.ProviderRegistration;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Rank implements DiscordCommandExecutor {
    @Override
    public void execute(DiscordSource source, String[] args, MessageReceivedEvent event) throws DiscordCommandException {
        Optional<User> optUser = args.length == 1? source.getSpongeUser() : Utility.getSpongeUser(args[1]);
        Optional<ProviderRegistration<LuckPerms>> provider = Sponge.getServiceManager().getRegistration(LuckPerms.class);
        System.out.println("x");
        if (!provider.isPresent() || !optUser.isPresent()) return;
        System.out.println("x");
        LuckPerms api = provider.get().getProvider();

        UserManager userManager = api.getUserManager();
        CompletableFuture<net.luckperms.api.model.user.User> userFuture = userManager.loadUser(optUser.get().getUniqueId());
        userFuture.whenComplete((user, throwable)->{
            List<String> perms = user.getDistinctNodes().stream()
                    .filter(n->n.getKey().startsWith("group."))
                    .map(n->n.getKey().substring(6))
                    .collect(Collectors.toList());
            GameChat.sendEmbed(optUser.get().getName() + "'s Groups:", String.join("\n", perms));
        });
    }
}
