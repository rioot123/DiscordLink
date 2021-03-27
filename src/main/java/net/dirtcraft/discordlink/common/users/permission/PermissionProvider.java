package net.dirtcraft.discordlink.common.users.permission;

import net.dirtcraft.discordlink.api.DiscordApiProvider;
import net.dirtcraft.discordlink.api.users.platform.PlatformPlayer;
import net.dirtcraft.discordlink.api.users.platform.PlatformUser;
import net.dirtcraft.discordlink.common.commands.sources.ConsoleSource;
import net.dirtcraft.discordlink.common.users.permission.dummy.DefaultProvider;
import net.dirtcraft.discordlink.common.users.permission.luckperms.Api5;
import net.dirtcraft.discordlink.common.users.permission.subject.PermissionResolver;
import net.dirtcraft.discordlink.api.users.MessageSource;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class PermissionProvider {

    public static String VERSION;

    public final static PermissionProvider INSTANCE = getRank();

    public abstract void printUserGroups(MessageSource source, PlatformUser player);

    public abstract void printUserKits(MessageSource source, PlatformUser player);

    public abstract void setPlayerPrefix(MessageSource source, PlatformUser target, String prefix);

    public abstract void setPlayerPrefix(ConsoleSource source, PlatformUser target, String prefix);

    public abstract void clearPlayerPrefix(MessageSource source, PlatformUser target);

    public abstract void clearPlayerPrefix(ConsoleSource source, PlatformUser target);

    public abstract Optional<String> getPrefix(UUID uuid);

    public abstract Optional<RankUpdate> modifyRank(@Nullable PlatformPlayer source, @Nullable UUID targetUUID, @Nullable String trackName, boolean promote);

    private static PermissionProvider getRank(){
        try {
            Class.forName("net.luckperms.api.LuckPerms");
            VERSION = "LuckPerms API 5";
            return new Api5(DiscordApiProvider.getApi().get().getPlatformProvider());
        } catch (ClassNotFoundException ignored){}
        VERSION = "None";
        return new DefaultProvider();
    }

    public static class RankUpdate{
        public final String added;
        public final String removed;
        public final UUID target;

        public RankUpdate(UUID target, String added, String removed){
            this.removed = removed != null && removed.equals("null") ? null : removed;
            this.added = added != null && added.equals("null") ? null : added;
            this.target = target;
        }
    }

    public abstract Optional<PermissionResolver> getPermission(UUID uuid);
}
