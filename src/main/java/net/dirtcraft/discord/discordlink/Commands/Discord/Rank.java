package net.dirtcraft.discord.discordlink.Commands.Discord;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.context.ContextSet;
import me.lucko.luckperms.api.manager.UserManager;
import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Utility;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.entity.living.player.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Rank implements DiscordCommandExecutor {
    private ContextSet contexts = LuckPerms.getApi()
            .getContextManager()
            .getStaticContexts()
            .getContexts();
            //.getAnyValue("server");

    @Override
    public void execute(GuildMember source, String[] args, MessageReceivedEvent event) throws DiscordCommandException {
        Optional<User> optUser = args.length == 1? source.getSpongeUser() : Utility.getSpongeUser(args[1]);
        @NonNull LuckPermsApi api = LuckPerms.getApi();
        UserManager userManager = api.getUserManager();
        CompletableFuture<me.lucko.luckperms.api.User> userFuture = userManager.loadUser(optUser.get().getUniqueId());
        userFuture.whenComplete((user, throwable)->{
            List<String> perms = user.getAllNodes().stream()
                    .filter(Node::isGroupNode)
                    .filter(n->n.getFullContexts().isSatisfiedBy(contexts))
                    .map(n->n.getPermission() + (n.appliesGlobally()? " [G]" : ""))
                    .map(n->n.substring(6))
                    .collect(Collectors.toList());
            GameChat.sendEmbed(optUser.get().getName() + "'s Groups:", String.join("\n", perms));
        });
    }
}
