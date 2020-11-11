package net.dirtcraft.discord.discordlink.Utility.Permission;

import net.dirtcraft.discord.discordlink.Utility.Permission.Default.DefaultProvider;
import net.dirtcraft.discord.discordlink.Utility.Permission.Luckperms.Api5;
import net.dirtcraft.discord.discordlink.Utility.Platform.PlatformPlayer;
import net.dirtcraft.discord.discordlink.Utility.Platform.PlatformUser;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class PermissionUtils {

    public final static PermissionUtils INSTANCE = getRank();

    public abstract void execute(PlatformUser player);

    public abstract boolean addRank(UUID target, String group);

    public abstract boolean removeRank(UUID target, String group);

    public abstract Optional<RankUpdate> modifyRank(@Nullable PlatformPlayer source, @Nullable UUID targetUUID, @Nullable String trackName, boolean promote);

    private static PermissionUtils getRank(){
        try {
            Class.forName("net.luckperms.api.LuckPerms");
            return new Api5();
        } catch (ClassNotFoundException ignored){}
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
