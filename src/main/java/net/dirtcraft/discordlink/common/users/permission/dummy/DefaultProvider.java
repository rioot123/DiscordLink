package net.dirtcraft.discordlink.common.users.permission.dummy;

import net.dirtcraft.discordlink.api.users.platform.PlatformPlayer;
import net.dirtcraft.discordlink.api.users.platform.PlatformUser;
import net.dirtcraft.discordlink.common.commands.sources.ConsoleSource;
import net.dirtcraft.discordlink.common.users.permission.PermissionProvider;
import net.dirtcraft.discordlink.common.users.permission.subject.PermissionResolver;
import net.dirtcraft.discordlink.api.users.MessageSource;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class DefaultProvider extends PermissionProvider {
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

    }

    @Override
    public void clearPlayerPrefix(ConsoleSource source, PlatformUser target) {

    }

    @Override
    public void setPlayerPrefix(MessageSource source, PlatformUser target, String prefix){
        source.sendCommandResponse("This version of luckperms is not supported!");
    }

    @Override
    public void clearPlayerPrefix(MessageSource source, PlatformUser target){
        source.sendCommandResponse("This version of luckperms is not supported!");
    }

    @Override
    public Optional<String> getPrefix(UUID uuid) {
        return Optional.empty();
    }

    public Optional<RankUpdate> modifyRank(@Nullable PlatformPlayer source, @Nullable UUID targetUUID, @Nullable String trackName, boolean promote) {
        return Optional.empty();
    }

    @Override
    public Optional<PermissionResolver> getPermission(UUID uuid) {
        return Optional.empty();
    }
}
