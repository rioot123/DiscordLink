package net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.Default;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformPlayer;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DefaultProvider extends PermissionUtils {
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
    public boolean hasPermission(UUID uuid, String permission) {
        return false;
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
        //if (source != null) source.sendMessage("This version of luckperms is not supported!");
        return Optional.empty();
    }

    @Override
    public Map<String, String> getUserGroupPrefixMap(PlatformUser user) {
        return new HashMap<>();
    }

    public Optional<String> getGroupPrefix(String name){
        return Optional.empty();
    }

    public boolean isInGroup(PlatformUser user, String group){
        return false;
    }

    public boolean groupHasPermission(String group, String perm){
        return false;
    }
}