package net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.Default;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformPlayer;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PexProvider extends PermissionUtils {
    private final PermissionManager api = PermissionsEx.getPermissionManager();
    @Override
    public void printUserGroups(MessageSource source, PlatformUser user) {
        source.sendCommandResponse("This version of luckperms is not supported!");
    }

    @Override
    public void printUserKits(MessageSource source, PlatformUser player) {
        source.sendCommandResponse("This version of luckperms is not supported!");
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
        return Optional.ofNullable(api.getUser(uuid)).map(u->u.has(permission)).orElse(false);
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
        Map<Integer, PermissionGroup> ladder = api.getRankLadder(trackName);
        //if (source != null) source.sendMessage("This version of luckperms is not supported!");
        return Optional.empty();
    }
}