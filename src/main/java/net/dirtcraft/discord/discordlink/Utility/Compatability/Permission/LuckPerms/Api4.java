package net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.LuckPerms;

import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.context.ContextSet;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static net.dirtcraft.discord.discordlink.Storage.Permission.PROMOTE_PERMISSION_GROUP_PREFIX;

public class Api4 extends PermissionUtils {
    @NonNull
    private LuckPermsApi api = me.lucko.luckperms.LuckPerms.getApi();
    private ContextSet contexts = api.getContextManager().getStaticContexts().getContexts();

    @Override
    public void execute(MessageSource source, User player) {
        me.lucko.luckperms.api.manager.UserManager userManager = api.getUserManager();
        CompletableFuture<me.lucko.luckperms.api.User> userFuture = userManager.loadUser(player.getUniqueId());
        userFuture.whenComplete((user, throwable)->{
            List<String> perms = user.getAllNodes().stream()
                    .filter(Node::isGroupNode)
                    .filter(n->n.getFullContexts().isSatisfiedBy(contexts))
                    .map(n->n.getPermission() + (n.appliesGlobally()? " [G]" : ""))
                    .map(n->n.substring(6))
                    .collect(Collectors.toList());
            source.sendCommandResponse(player.getName() + "'s Groups:", String.join("\n", perms));
        });
    }

    public Optional<RankUpdate> modifyRank(@Nullable Player source, @Nullable UUID targetUUID, @Nullable String trackName, boolean promote){
        return Optional.empty();
    }

    private boolean hasPermission(Player source, String group){
        return source.hasPermission(PROMOTE_PERMISSION_GROUP_PREFIX + group);
    }
}