package net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.Default;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformPlayer;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import ru.tehkode.permissions.PermissionEntity;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.dirtcraft.discord.discordlink.Storage.Permission.PROMOTE_PERMISSION_GROUP_PREFIX;

public class PexProvider extends PermissionUtils {
    private final PermissionManager api = PermissionsEx.getPermissionManager();
    @Override
    public void printUserGroups(MessageSource source, PlatformUser user) {
        if (api.getUser(user.getUUID()) == null || !user.getName().isPresent()) return;
        List<PermissionGroup> perms = api.getUser(user.getUUID()).getOwnParents();
        String groups = perms.stream()
                .map(PermissionEntity::getIdentifier)
                .collect(Collectors.joining("\n"));
        if (groups.isEmpty()) groups = "None found.";
        source.sendCommandResponse(user.getName().orElse("subject") + "'s Kits:", groups);
    }

    @Override
    public void printUserKits(MessageSource source, PlatformUser user) {
        if (api.getUser(user.getUUID()) == null || !user.getName().isPresent()) return;
        List<String> perms = api.getUser(user.getUUID()).getOwnPermissions(null);
        String kits = perms.stream()
                .filter(n->n.startsWith("essentials.kits"))
                .map(n->n.substring(16))
                .collect(Collectors.joining("\n"));
        if (kits.isEmpty()) kits = "None found.";
        source.sendCommandResponse(user.getName().orElse("subject") + "'s Kits:", kits);
    }

    @Override
    public void setPlayerPrefix(ConsoleSource source, PlatformUser target, String prefix) {
        api.getUser(target.getUUID()).setPrefix(prefix, null);
    }

    @Override
    public void clearPlayerPrefix(ConsoleSource source, PlatformUser target) {
        api.getUser(target.getUUID()).setPrefix(null, null);
    }

    @Override
    public boolean hasPermission(UUID uuid, String permission) {
        return Optional.ofNullable(api.getUser(uuid))
                .map(u->u.has(permission))
                .orElse(false);
    }

    @Override
    public void setPlayerPrefix(MessageSource source, PlatformUser target, String prefix){
        api.getUser(target.getUUID()).setPrefix(prefix, null);
    }

    @Override
    public void clearPlayerPrefix(MessageSource source, PlatformUser target){
        api.getUser(target.getUUID()).setPrefix(null, null);
    }

    @Override
    public Optional<String> getPrefix(UUID uuid) {
        return Optional.of(uuid).map(api::getUser).map(u->u.getPrefix(null));
    }

    public Optional<RankUpdate> modifyRank(@Nullable PlatformPlayer source, @Nullable UUID targetUUID, @Nullable String trackName, boolean promote) {
        if (source == null || targetUUID == null || trackName == null) return Optional.empty();
        Map<Integer, PermissionGroup> ladder = api.getRankLadder(trackName);
        PermissionUser target = api.getUser(targetUUID);
        int pos = target.getRank(trackName);
        if (promote) return promoteTarget(source, targetUUID, ladder, pos);
        else return demoteTarget(source, targetUUID, ladder, pos);
    }

    public Optional<RankUpdate> promoteTarget(PlatformPlayer source, UUID target, Map<Integer, PermissionGroup> ladder, int position){
        PermissionUser user = api.getUser(target);
        PermissionGroup current = ladder.get(position);
        PermissionGroup next = ladder.get(position + 1);
        if (current == null || next == null || user == null || hasPermission(source, next)) return Optional.empty();
        user.addGroup(next);
        user.removeGroup(current);
        user.save();

        return Optional.of(new RankUpdate(target, next.getIdentifier(), current.getIdentifier()));
    }

    public Optional<RankUpdate> demoteTarget(PlatformPlayer source, UUID target, Map<Integer, PermissionGroup> ladder, int position){
        PermissionUser user = api.getUser(target);
        PermissionGroup current = ladder.get(position);
        PermissionGroup previous = ladder.get(position - 1);
        if (current == null || previous == null || user == null || hasPermission(source, previous)) return Optional.empty();
        user.addGroup(previous);
        user.removeGroup(current);
        user.save();

        return Optional.of(new RankUpdate(target, previous.getIdentifier(), current.getIdentifier()));
    }

    private boolean hasPermission(PlatformPlayer source, PermissionGroup group){
        if (group == null) return true;
        String id = group.getIdentifier();
        return source.hasPermission(PROMOTE_PERMISSION_GROUP_PREFIX + id);
    }
}