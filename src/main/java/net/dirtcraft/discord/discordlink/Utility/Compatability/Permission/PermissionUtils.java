package net.dirtcraft.discord.discordlink.Utility.Compatability.Permission;

import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.Default.DefaultProvider;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.Luckperms.Api5;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformPlayer;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class PermissionUtils {

    public static String VERSION;

    public final static PermissionUtils INSTANCE = getRank();

    public abstract void execute(PlatformUser player);

    public abstract boolean addRank(UUID target, String group);

    public abstract boolean removeRank(UUID target, String group);

    public abstract Optional<RankUpdate> modifyRank(@Nullable PlatformPlayer source, @Nullable UUID targetUUID, @Nullable String trackName, boolean promote);

    private static PermissionUtils getRank(){
        try {
            Class.forName("net.luckperms.api.LuckPerms");
            VERSION = "LuckPerms API 5";
            return new Api5();
        } catch (ClassNotFoundException ignored){}
        VERSION = "None";
        return new DefaultProvider();
    }

    public static class RankUpdate{
        public final String removed;
        public final String added;
        public final UUID target;

        public RankUpdate(UUID target, String added, String removed){
            this.removed = removed != null && removed.equals("null") ? null : removed;
            this.added = added != null && added.equals("null") ? null : added;
            this.target = target;
        }
    }
}
