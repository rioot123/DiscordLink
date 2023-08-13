// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.users.permission.luckperms;

import net.luckperms.api.cacheddata.CachedPermissionData;
import net.dirtcraft.discordlink.users.permission.subject.PermissionResolver;
import net.luckperms.api.model.data.NodeMap;
import java.util.List;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.Node;
import net.luckperms.api.track.Track;
import java.util.function.Function;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import org.spongepowered.api.entity.living.player.Player;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.node.NodeType;
import java.util.concurrent.CompletableFuture;
import net.luckperms.api.model.user.UserManager;
import org.spongepowered.api.entity.living.player.User;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.LuckPerms;

public class Api5 extends LuckPermissions
{
    private final LuckPerms api;
    private final ImmutableContextSet contexts;
    
    public Api5() {
        this.api = LuckPermsProvider.get();
        this.contexts = this.api.getContextManager().getStaticContext();
    }
    
    @Override
    public void printUserGroups(final MessageSource source, final User player) {
        final UserManager userManager = this.api.getUserManager();
        final CompletableFuture<net.luckperms.api.model.user.User> userFuture = (CompletableFuture<net.luckperms.api.model.user.User>)userManager.loadUser(player.getUniqueId());
        final String local;
        final String remote;
        final String local2;
        final String remote2;
        final String perms;
        userFuture.whenComplete((user, throwable) -> {
            local = this.getGroups(user, this.contexts, false);
            remote = this.getGroups(user, this.contexts, true);
            local2 = (local.equals("") ? "None found." : local);
            remote2 = (remote.equals("") ? "None found." : remote);
            perms = "__**Local**__\n" + local2 + "\n\n__**Other Servers**__\n" + remote2;
            source.sendCommandResponse(player.getName() + "'s Groups:", perms);
        });
    }
    
    @Override
    public void printUserKits(final MessageSource source, final User player) {
        final UserManager userManager = this.api.getUserManager();
        final CompletableFuture<net.luckperms.api.model.user.User> userFuture = (CompletableFuture<net.luckperms.api.model.user.User>)userManager.loadUser(player.getUniqueId());
        final String local;
        final String remote;
        final String local2;
        final String remote2;
        final String perms;
        userFuture.whenComplete((user, throwable) -> {
            local = this.getKits(user, this.contexts, false);
            remote = this.getKits(user, this.contexts, true);
            local2 = (local.equals("") ? "None found." : local);
            remote2 = (remote.equals("") ? "None found." : remote);
            perms = "__**Local**__\n" + local2 + "\n\n__**Other Servers**__\n" + remote2;
            source.sendCommandResponse(player.getName() + "'s Kits:", perms);
        });
    }
    
    private String getGroups(final net.luckperms.api.model.user.User user, final ImmutableContextSet contexts, final boolean negate) {
        String string;
        return (String)user.getDistinctNodes().parallelStream().filter(node -> node.getType() == NodeType.INHERITANCE && !node.isNegated()).filter(node -> node.getContexts().isSatisfiedBy((ContextSet)contexts) ^ negate).map(n -> "**" + n.getKey().substring(6) + "**" + n.getContexts().getAnyValue("server").map(s -> {
            if (negate) {
                string = " *[" + s + "]*";
            }
            else {
                string = "";
            }
            return string;
        }).orElse(" *[global]*")).collect(Collectors.joining("\n"));
    }
    
    private String getKits(final net.luckperms.api.model.user.User user, final ImmutableContextSet contexts, final boolean negate) {
        String string;
        return (String)user.getDistinctNodes().parallelStream().filter(node -> node.getType() == NodeType.PERMISSION && !node.isNegated()).filter(node -> node.getKey().startsWith("nucleus.kits.")).filter(node -> node.getContexts().isSatisfiedBy((ContextSet)contexts) ^ negate).map(n -> "**" + n.getKey().substring(13) + "**" + n.getContexts().getAnyValue("server").map(s -> {
            if (negate) {
                string = " *[" + s + "]*";
            }
            else {
                string = "";
            }
            return string;
        }).orElse(" *[global]*")).collect(Collectors.joining("\n"));
    }
    
    @Override
    public Optional<RankUpdate> modifyRank(@Nullable final Player source, @Nullable final UUID targetUUID, @Nullable final String trackName, final boolean promote) {
        final Optional<net.luckperms.api.model.user.User> target = Optional.ofNullable(targetUUID).map((Function<? super UUID, ?>)this.api.getUserManager()::loadUser).map((Function<? super Object, ? extends net.luckperms.api.model.user.User>)CompletableFuture::join);
        final Optional<Track> track = Optional.ofNullable(trackName).map((Function<? super String, ? extends Track>)this.api.getTrackManager()::getTrack);
        if (source == null || !target.isPresent() || !track.isPresent()) {
            return Optional.empty();
        }
        if (promote) {
            return this.promoteTarget(source, target.get(), track.get());
        }
        return this.demoteTarget(source, target.get(), track.get());
    }
    
    private Optional<RankUpdate> demoteTarget(final Player source, final net.luckperms.api.model.user.User targetUser, final Track track) {
        final List<String> groups = (List<String>)track.getGroups();
        final NodeMap targetNodes = targetUser.data();
        String previousGroup = "default";
        Node previousNode = null;
        int i = groups.size();
        while (i > 0) {
            final String group = groups.get(--i);
            if (group.equalsIgnoreCase("default")) {
                continue;
            }
            final Node node = (Node)Node.builder("group." + group).context((ContextSet)this.contexts).build();
            if (targetNodes.contains(node, NodeEqualityPredicate.EXACT).asBoolean()) {
                previousGroup = group;
                previousNode = node;
            }
            else {
                if (previousNode != null && this.hasPermission(source, previousGroup)) {
                    this.setRank(targetUser, node, previousNode);
                    return Optional.of(new RankUpdate(targetUser.getUniqueId(), group, previousGroup));
                }
                continue;
            }
        }
        if (this.hasPermission(source, previousGroup)) {
            this.setRank(targetUser, null, previousNode);
            return Optional.of(new RankUpdate(targetUser.getUniqueId(), null, previousGroup));
        }
        return Optional.empty();
    }
    
    private Optional<RankUpdate> promoteTarget(final Player source, final net.luckperms.api.model.user.User targetUser, final Track track) {
        final List<String> groups = (List<String>)track.getGroups();
        final NodeMap targetNodes = targetUser.data();
        String previousGroup = "default";
        Node previousNode = null;
        int i = groups.size();
        while (i > 0) {
            final String group = groups.get(--i);
            if (group.equalsIgnoreCase("default")) {
                continue;
            }
            final Node node = (Node)Node.builder("group." + group).context((ContextSet)this.contexts).build();
            if (!targetNodes.contains(node, NodeEqualityPredicate.EXACT).asBoolean()) {
                previousGroup = group;
                previousNode = node;
            }
            else {
                if (previousNode != null && this.hasPermission(source, previousGroup)) {
                    this.setRank(targetUser, previousNode, node);
                    return Optional.of(new RankUpdate(targetUser.getUniqueId(), previousGroup, group));
                }
                return Optional.empty();
            }
        }
        if (this.hasPermission(source, previousGroup)) {
            this.setRank(targetUser, previousNode, null);
            return Optional.of(new RankUpdate(targetUser.getUniqueId(), previousGroup, null));
        }
        return Optional.empty();
    }
    
    private void setRank(final net.luckperms.api.model.user.User target, final Node add, final Node remove) {
        if (remove != null) {
            target.data().remove(remove);
        }
        if (add != null) {
            target.data().add(add);
        }
        LuckPermsProvider.get().getUserManager().saveUser(target);
    }
    
    private boolean hasPermission(final Player source, final String group) {
        return group == null || source.hasPermission("discordlink.promote." + group);
    }
    
    public String getServerContext() {
        return this.contexts.getAnyValue("server").orElse("global");
    }
    
    @Override
    public Optional<String> getPrefix(final UUID uuid) {
        return Optional.ofNullable(this.api.getUserManager().getUser(uuid)).map(u -> u.getCachedData().getMetaData().getPrefix());
    }
    
    @Override
    public Optional<PermissionResolver> getPermission(final UUID uuid) {
        final net.luckperms.api.model.user.User user = this.api.getUserManager().loadUser(uuid).join();
        if (user == null) {
            return Optional.empty();
        }
        final CachedPermissionData data = user.getCachedData().getPermissionData();
        return Optional.of(permission -> data.checkPermission(permission).asBoolean());
    }
}
