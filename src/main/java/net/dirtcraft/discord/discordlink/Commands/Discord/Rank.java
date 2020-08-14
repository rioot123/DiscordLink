package net.dirtcraft.discord.discordlink.Commands.Discord;

import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.context.ContextSet;
import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Utility;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.query.Flag;
import net.luckperms.api.query.OptionKey;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.entity.living.player.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class Rank implements DiscordCommandExecutor {

    public final static Rank INSTANCE = getRank();

    private static Rank getRank(){
        try {
            Class.forName("net.luckperms.api.LuckPerms");
            return new Api5();
        } catch (ClassNotFoundException ignored){}
        try {
            Class.forName("me.lucko.luckperms.api.LuckPermsApi");
            return new Api4();
        } catch (ClassNotFoundException ignored){}
        return new Null();
    }

    public static class Null extends Rank {
        @Override
        public void execute(GuildMember source, String[] args, MessageReceivedEvent event) throws DiscordCommandException {
            GameChat.sendMessage("This version of luckperms is not supported!");
        }
    }

    public static class Api4 extends Rank {
        @NonNull
        private LuckPermsApi api = me.lucko.luckperms.LuckPerms.getApi();
        private ContextSet contexts = api.getContextManager().getStaticContexts().getContexts();

        @Override
        public void execute(GuildMember source, String[] args, MessageReceivedEvent event) throws DiscordCommandException {
            Optional<User> optUser = args.length == 1? source.getSpongeUser() : Utility.getSpongeUser(args[1]);
            me.lucko.luckperms.api.manager.UserManager userManager = api.getUserManager();
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

    public static class Api5 extends Rank {
        private final LuckPerms api = LuckPermsProvider.get();
        private final ImmutableContextSet contexts = api.getContextManager().getStaticContext();
        private final QueryOptions queryOptions = QueryOptions.contextual(contexts).toBuilder()
                .flag(Flag.RESOLVE_INHERITANCE, false)
                .build();

        @Override
        public void execute(GuildMember source, String[] args, MessageReceivedEvent event) throws DiscordCommandException {
            Optional<User> optUser = args.length == 1 ? source.getSpongeUser() : Utility.getSpongeUser(args[1]);
            if (!optUser.isPresent()) {
                String response = args.length == 1? "You are not correctly verified, or have not played on this server." : "Invalid user. Either the user does not exist or they have never played on this server.";
                GameChat.sendMessage(response, 30);
                event.getMessage().delete().queue();
                return;
            }

            UserManager userManager = api.getUserManager();
            CompletableFuture<net.luckperms.api.model.user.User> userFuture = userManager.loadUser(optUser.get().getUniqueId());
            userFuture.whenComplete((user, throwable) -> {
                String perms = user.getInheritedGroups(queryOptions).stream()
                        .map(Group::getName)
                        .collect(Collectors.joining("\n"));
                GameChat.sendEmbed(optUser.get().getName() + "'s Groups:", perms);
            });
        }
    }
}
