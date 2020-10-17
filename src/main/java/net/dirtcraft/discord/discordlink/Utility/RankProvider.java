package net.dirtcraft.discord.discordlink.Utility;

import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.context.ContextSet;
import net.dirtcraft.discord.discordlink.API.GameChat;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.entity.living.player.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class RankProvider {

    public final static RankProvider INSTANCE = getRank();

    public abstract void execute(User player);

    private static RankProvider getRank(){
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

    public static class Null extends RankProvider {
        @Override
        public void execute(User user) {
            GameChat.sendMessage("This version of luckperms is not supported!");
        }
    }

    public static class Api4 extends RankProvider {
        @NonNull
        private LuckPermsApi api = me.lucko.luckperms.LuckPerms.getApi();
        private ContextSet contexts = api.getContextManager().getStaticContexts().getContexts();

        @Override
        public void execute(User player) {
            me.lucko.luckperms.api.manager.UserManager userManager = api.getUserManager();
            CompletableFuture<me.lucko.luckperms.api.User> userFuture = userManager.loadUser(player.getUniqueId());
            userFuture.whenComplete((user, throwable)->{
                List<String> perms = user.getAllNodes().stream()
                        .filter(Node::isGroupNode)
                        .filter(n->n.getFullContexts().isSatisfiedBy(contexts))
                        .map(n->n.getPermission() + (n.appliesGlobally()? " [G]" : ""))
                        .map(n->n.substring(6))
                        .collect(Collectors.toList());
                GameChat.sendEmbed(player.getName() + "'s Groups:", String.join("\n", perms));
            });
        }
    }

    public static class Api5 extends RankProvider {
        private final LuckPerms api = LuckPermsProvider.get();
        private final ImmutableContextSet contexts = api.getContextManager().getStaticContext();

        @Override
        public void execute(User player) {
            UserManager userManager = api.getUserManager();
            CompletableFuture<net.luckperms.api.model.user.User> userFuture = userManager.loadUser(player.getUniqueId());
            userFuture.whenComplete((user, throwable) -> {
                String local = getGroups(user, contexts, false);
                String remote = getGroups(user, contexts, true);
                String perms = "__**Local**__\n" + local + "\n\n__**Other Servers**__\n" + remote;
                GameChat.sendEmbed(player.getName() + "'s Groups:", perms);
            });
        }

        private String getGroups(net.luckperms.api.model.user.User user, ImmutableContextSet contexts, boolean negate){
            return user.getDistinctNodes().parallelStream()
                    .filter(node->node.getType() == NodeType.INHERITANCE && !node.isNegated())
                    .filter(node -> node.getContexts().isSatisfiedBy(contexts) ^ negate)
                    .map(n -> "**" + n.getKey().substring(6) + "**" + n.getContexts()
                            .getAnyValue("server")
                            .map(s->negate?" *["+s+"]*":"")
                            .orElse(" *[global]*"))
                    .collect(Collectors.joining("\n"));
        }
    }
}
