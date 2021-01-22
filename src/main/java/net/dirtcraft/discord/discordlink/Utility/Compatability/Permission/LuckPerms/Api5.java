package net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.LuckPerms;


import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformPlayer;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.track.Track;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static net.dirtcraft.discord.discordlink.Storage.Permission.PROMOTE_PERMISSION_GROUP_PREFIX;

public class Api5 extends LuckPermissions {
    private final LuckPerms api = LuckPermsProvider.get();
    private final ImmutableContextSet contexts = api.getContextManager().getStaticContext();

    @Override
    public void printUserGroups(MessageSource source, PlatformUser player) {
        UserManager userManager = api.getUserManager();
        CompletableFuture<net.luckperms.api.model.user.User> userFuture = userManager.loadUser(player.getUUID());
        userFuture.whenComplete((user, throwable) -> {
            String local = getGroups(user, contexts, false);
            String remote = getGroups(user, contexts, true);
            local = local.equals("") ? "None found." : local;
            remote = remote.equals("") ? "None found." : remote;
            String perms = "__**Local**__\n" + local + "\n\n__**Other Servers**__\n" + remote;
            source.sendCommandResponse(player.getName() + "'s Groups:", perms);
        });
    }

    @Override
    public void printUserKits(MessageSource source, PlatformUser player) {
        UserManager userManager = api.getUserManager();
        CompletableFuture<net.luckperms.api.model.user.User> userFuture = userManager.loadUser(player.getUUID());
        userFuture.whenComplete((user, throwable) -> {
            String local = getKits(user, contexts, false);
            String remote = getKits(user, contexts, true);
            local = local.equals("") ? "None found." : local;
            remote = remote.equals("") ? "None found." : remote;
            String perms = "__**Local**__\n" + local + "\n\n__**Other Servers**__\n" + remote;
            source.sendCommandResponse(player.getName() + "'s Kits:", perms);
        });
    }

    @Override
    public boolean hasPermission(UUID uuid, String permission) {
        User  user = api.getUserManager().getUser(uuid);
        return user != null && user.getCachedData()
                .getPermissionData()
                .checkPermission(permission)
                .asBoolean();
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

    private String getKits(net.luckperms.api.model.user.User user, ImmutableContextSet contexts, boolean negate){
        return user.getDistinctNodes().parallelStream()
                .filter(node->node.getType() == NodeType.PERMISSION && !node.isNegated())
                .filter(node->node.getKey().startsWith("nucleus.kits."))
                .filter(node -> node.getContexts().isSatisfiedBy(contexts) ^ negate)
                .map(n -> "**" + n.getKey().substring(13) + "**" + n.getContexts()
                        .getAnyValue("server")
                        .map(s->negate?" *["+s+"]*":"")
                        .orElse(" *[global]*"))
                .collect(Collectors.joining("\n"));
    }

    public Optional<PermissionUtils.RankUpdate> modifyRank(@Nullable PlatformPlayer source, @Nullable UUID targetUUID, @Nullable String trackName, boolean promote){
        Optional<net.luckperms.api.model.user.User> target = Optional.ofNullable(targetUUID)
                .map(api.getUserManager()::loadUser)
                .map(CompletableFuture::join);
        Optional<Track> track = Optional.ofNullable(trackName)
                .map(api.getTrackManager()::getTrack);

        if (source == null || !target.isPresent() || !track.isPresent()) return Optional.empty();
        else if (promote) return promoteTarget(source, target.get(), track.get());
        else return demoteTarget(source, target.get(), track.get());
    }

    private Optional<PermissionUtils.RankUpdate> demoteTarget(PlatformPlayer source, net.luckperms.api.model.user.User targetUser, Track track) {
        List<String> groups = track.getGroups();
        NodeMap targetNodes = targetUser.data();
        String previousGroup = "default";
        Node previousNode = null;
        for (int i = groups.size(); i > 0; ) {
            final String group = groups.get(--i);
            if (group.equalsIgnoreCase("default")) continue;
            final Node node = Node.builder("group." + group).context(contexts).build();
            if (targetNodes.contains(node, NodeEqualityPredicate.EXACT).asBoolean()) {
                previousGroup = group;
                previousNode = node;
            } else if (previousNode != null && hasPermission(source, previousGroup)) {
                setRank(targetUser, node, previousNode);
                return Optional.of(new PermissionUtils.RankUpdate(targetUser.getUniqueId(), group, previousGroup));
            }
        }
        if (hasPermission(source, previousGroup)) {
            setRank(targetUser, null, previousNode);
            return Optional.of(new PermissionUtils.RankUpdate(targetUser.getUniqueId(), null, previousGroup));
        } else return Optional.empty();
    }

    private Optional<PermissionUtils.RankUpdate> promoteTarget(PlatformPlayer source, net.luckperms.api.model.user.User targetUser, Track track) {
        List<String> groups = track.getGroups();
        NodeMap targetNodes = targetUser.data();
        String previousGroup = "default";
        Node previousNode = null;
        for (int i = groups.size(); i > 0; ) {
            final String group = groups.get(--i);
            if (group.equalsIgnoreCase("default")) continue;
            final Node node = Node.builder("group." + group).context(contexts).build();
            if (!targetNodes.contains(node, NodeEqualityPredicate.EXACT).asBoolean()) {
                previousGroup = group;
                previousNode = node;
            } else if (previousNode != null && hasPermission(source, previousGroup)) {
                setRank(targetUser, previousNode, node);
                return Optional.of(new PermissionUtils.RankUpdate(targetUser.getUniqueId(), previousGroup, group));
            } else return Optional.empty();
        }
        if (hasPermission(source, previousGroup)) {
            setRank(targetUser, previousNode, null);
            return Optional.of(new PermissionUtils.RankUpdate(targetUser.getUniqueId(), previousGroup, null));
        } else return Optional.empty();
    }

    private void setRank(net.luckperms.api.model.user.User target, Node add, Node remove){
        if (remove != null) target.data().remove(remove);
        if (add != null) target.data().add(add);
        LuckPermsProvider.get().getUserManager().saveUser(target);
    }

    private boolean hasPermission(PlatformPlayer source, String group){
        if (group == null) return true;
        return source.hasPermission(PROMOTE_PERMISSION_GROUP_PREFIX + group);
    }

    public String getServerContext(){
        return contexts.getAnyValue("server").orElse("global");
    }

    public Optional<String> getPrefix(UUID uuid){
        return Optional.ofNullable(api.getUserManager().getUser(uuid))
                .map(u-> u.getCachedData().getMetaData().getPrefix());
    }
}
