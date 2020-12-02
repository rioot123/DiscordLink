package net.dirtcraft.discord.discordlink.Utility.Compatability.Permission;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.Default.DefaultProvider;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.Default.PexProvider;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformPlayer;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class PermissionUtils {

    public static String VERSION;

    public final static PermissionUtils INSTANCE = getRank();

    public abstract void printUserGroups(MessageSource source, PlatformUser player);

    public abstract void printUserKits(MessageSource source, PlatformUser player);

    public abstract void setPlayerPrefix(MessageSource source, PlatformUser target, String prefix);

    public abstract void setPlayerPrefix(ConsoleSource source, PlatformUser target, String prefix);

    public abstract void clearPlayerPrefix(MessageSource source, PlatformUser target);

    public abstract void clearPlayerPrefix(ConsoleSource source, PlatformUser target);

    public abstract boolean hasPermission(UUID uuid, String permission);

    public abstract Optional<String> getPrefix(UUID uuid);

    public abstract Optional<RankUpdate> modifyRank(@Nullable PlatformPlayer source, @Nullable UUID targetUUID, @Nullable String trackName, boolean promote);

    private static PermissionUtils getRank(){
        try{
            Class.forName("ru.tehkode.permissions.PermissionManager");
            VERSION = "Pex";
            return new PexProvider();
        } catch (Exception ignored){}
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
}