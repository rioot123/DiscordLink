package net.dirtcraft.discord.discordlink.Utility;

import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.Default.DefaultProvider;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.LuckPerms.Api4;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.LuckPerms.Api5;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class PermissionUtils {

    public final static PermissionUtils INSTANCE = getRank();

    public abstract void execute(User player);

    public abstract Optional<RankUpdate> modifyRank(@Nullable Player source, @Nullable UUID targetUUID, @Nullable String trackName, boolean promote);

    private static PermissionUtils getRank(){
        try {
            Class.forName("net.luckperms.api.LuckPerms");
            return new Api5();
        } catch (ClassNotFoundException ignored){}
        try {
            Class.forName("me.lucko.luckperms.api.LuckPermsApi");
            return new Api4();
        } catch (ClassNotFoundException ignored){}
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
