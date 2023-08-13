// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.users.permission.luckperms;

import me.lucko.luckperms.api.caching.PermissionData;
import net.dirtcraft.discordlink.users.permission.subject.PermissionResolver;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.Contexts;
import java.util.SortedSet;
import java.util.List;
import me.lucko.luckperms.api.Track;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import org.spongepowered.api.entity.living.player.Player;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.manager.UserManager;
import org.spongepowered.api.entity.living.player.User;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.context.ContextSet;
import me.lucko.luckperms.api.LuckPermsApi;

public class Api4 extends LuckPermissions
{
    private LuckPermsApi api;
    private ContextSet contexts;
    
    public Api4() {
        this.api = LuckPerms.getApi();
        this.contexts = this.api.getContextManager().getStaticContexts().getContexts();
    }
    
    @Override
    public void printUserGroups(final MessageSource source, final User player) {
        final UserManager userManager = this.api.getUserManager();
        final StringBuilder local = new StringBuilder("__**Local**__\n");
        final StringBuilder remote = new StringBuilder("__**Other Servers**__\n");
        userManager.loadUser(player.getUniqueId()).join().getPermissions().stream().filter(n -> n.getPermission().startsWith("group.")).filter(n -> !n.getPermission().endsWith(".default")).forEach(n -> this.sortToString(n, local, remote, 6));
        if (local.toString().equalsIgnoreCase("__**Local**__\n")) {
            remote.append("None found.\n");
        }
        if (remote.toString().equalsIgnoreCase("__**Other Servers**__\n")) {
            remote.append("None found.");
        }
        else {
            remote.deleteCharAt(remote.length() - 1);
        }
        source.sendCommandResponse(player.getName() + "'s Kits:", String.join("\n", local, remote));
    }
    
    @Override
    public void printUserKits(final MessageSource source, final User player) {
        final UserManager userManager = this.api.getUserManager();
        final StringBuilder local = new StringBuilder("__**Local**__\n");
        final StringBuilder remote = new StringBuilder("__**Other Servers**__\n");
        userManager.loadUser(player.getUniqueId()).join().getPermissions().stream().filter(n -> n.getPermission().startsWith("nucleus.kits.")).forEach(n -> this.sortToString(n, local, remote, 13));
        if (local.toString().equalsIgnoreCase("__**Local**__\n")) {
            remote.append("None found.\n");
        }
        if (remote.toString().equalsIgnoreCase("__**Other Servers**__\n")) {
            remote.append("None found.");
        }
        else {
            remote.deleteCharAt(remote.length() - 1);
        }
        source.sendCommandResponse(player.getName() + "'s Kits:", String.join("\n", local, remote));
    }
    
    public void sortToString(final Node n, final StringBuilder local, final StringBuilder remote, final int trim) {
        if (n.isServerSpecific() && n.getFullContexts().isSatisfiedBy(this.contexts)) {
            local.append(n.getPermission().substring(trim)).append("\n");
        }
        else if (n.appliesGlobally()) {
            local.append(n.getPermission().substring(trim)).append(" *[global]*\n");
        }
        else {
            remote.append(n.getPermission().substring(trim)).append(" *[").append(n.getFullContexts().getAnyValue("server").orElse("unknown")).append("]*\n");
        }
    }
    
    @Override
    public Optional<RankUpdate> modifyRank(@Nullable final Player source, @Nullable final UUID targetUUID, @Nullable final String trackName, final boolean promote) {
        final Optional<me.lucko.luckperms.api.User> target = Optional.ofNullable(targetUUID).map((Function<? super UUID, ?>)this.api.getUserManager()::loadUser).map((Function<? super Object, ? extends me.lucko.luckperms.api.User>)CompletableFuture::join);
        final Optional<Track> track = Optional.ofNullable(trackName).map((Function<? super String, ? extends Track>)this.api.getTrackManager()::getTrack);
        if (source == null || !target.isPresent() || !track.isPresent()) {
            return Optional.empty();
        }
        if (promote) {
            return this.promoteTarget(source, target.get(), track.get());
        }
        return this.demoteTarget(source, target.get(), track.get());
    }
    
    private Optional<RankUpdate> demoteTarget(final Player source, final me.lucko.luckperms.api.User targetUser, final Track track) {
        try {
            final List<String> groups = (List<String>)track.getGroups();
            final SortedSet<? extends Node> targetNodes = (SortedSet<? extends Node>)targetUser.getPermissions();
            String previousGroup = "default";
            Node previousNode = null;
            int i = groups.size();
            while (i > 0) {
                final String group = groups.get(--i);
                if (group.equalsIgnoreCase("default")) {
                    continue;
                }
                final Node node = this.api.buildNode("group." + group).setServer(this.getServerContext()).build();
                if (targetNodes.contains(node)) {
                    previousGroup = group;
                    previousNode = node;
                }
                else {
                    if (previousNode != null && this.hasPermission(source, previousGroup)) {
                        this.setRank(targetUser, node, previousNode);
                        return Optional.of(new RankUpdate(targetUser.getUuid(), group, previousGroup));
                    }
                    continue;
                }
            }
            if (this.hasPermission(source, previousGroup)) {
                this.setRank(targetUser, null, previousNode);
                return Optional.of(new RankUpdate(targetUser.getUuid(), null, previousGroup));
            }
            return Optional.empty();
        }
        catch (Throwable e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    
    private Optional<RankUpdate> promoteTarget(final Player source, final me.lucko.luckperms.api.User targetUser, final Track track) {
        final List<String> groups = (List<String>)track.getGroups();
        final SortedSet<? extends Node> targetNodes = (SortedSet<? extends Node>)targetUser.getPermissions();
        String previousGroup = "default";
        Node previousNode = null;
        int i = groups.size();
        while (i > 0) {
            final String group = groups.get(--i);
            if (group.equalsIgnoreCase("default")) {
                continue;
            }
            final Node node = this.api.buildNode("group." + group).setServer(this.getServerContext()).build();
            if (!targetNodes.contains(node)) {
                previousGroup = group;
                previousNode = node;
            }
            else {
                if (previousNode != null && this.hasPermission(source, previousGroup)) {
                    this.setRank(targetUser, previousNode, node);
                    return Optional.of(new RankUpdate(targetUser.getUuid(), previousGroup, group));
                }
                return Optional.empty();
            }
        }
        if (this.hasPermission(source, previousGroup)) {
            this.setRank(targetUser, previousNode, null);
            return Optional.of(new RankUpdate(targetUser.getUuid(), previousGroup, null));
        }
        return Optional.empty();
    }
    
    private void setRank(final me.lucko.luckperms.api.User target, final Node add, final Node remove) {
        if (remove != null) {
            target.unsetPermission(remove);
        }
        if (add != null) {
            target.setPermission(add);
        }
        this.api.getUserManager().saveUser(target);
    }
    
    private boolean hasPermission(final Player source, final String group) {
        return source.hasPermission("discordlink.promote." + group);
    }
    
    public String getServerContext() {
        return this.contexts.getAnyValue("server").orElse("global");
    }
    
    @Override
    public Optional<String> getPrefix(final UUID uuid) {
        return Optional.ofNullable(this.api.getUserManager().getUser(uuid)).map(u -> u.getCachedData().getMetaData(Contexts.of(this.contexts, Contexts.global().getSettings()))).map((Function<? super Object, ? extends String>)MetaData::getPrefix);
    }
    
    @Override
    public Optional<PermissionResolver> getPermission(final UUID uuid) {
        final me.lucko.luckperms.api.User user = this.api.getUserManager().loadUser(uuid).join();
        if (user == null) {
            return Optional.empty();
        }
        final PermissionData data = user.getCachedData().getPermissionData(Contexts.of(this.contexts, Contexts.global().getSettings()));
        return Optional.of(permission -> data.getPermissionValue(permission).asBoolean());
    }
}
