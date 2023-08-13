// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.users.permission;

import net.dirtcraft.discordlink.users.permission.subject.PermissionResolver;
import net.dirtcraft.discordlink.users.permission.dummy.DefaultProvider;
import net.dirtcraft.discordlink.users.permission.luckperms.Api4;
import net.dirtcraft.discordlink.users.permission.luckperms.Api5;
import javax.annotation.Nullable;
import org.spongepowered.api.entity.living.player.Player;
import java.util.Optional;
import java.util.UUID;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.User;
import net.dirtcraft.spongediscordlib.users.MessageSource;

public abstract class PermissionProvider
{
    public static String VERSION;
    public static final PermissionProvider INSTANCE;
    
    public abstract void printUserGroups(final MessageSource p0, final User p1);
    
    public abstract void printUserKits(final MessageSource p0, final User p1);
    
    public abstract void setPlayerPrefix(final MessageSource p0, final User p1, final String p2);
    
    public abstract void setPlayerPrefix(final ConsoleSource p0, final User p1, final String p2);
    
    public abstract void clearPlayerPrefix(final MessageSource p0, final User p1);
    
    public abstract void clearPlayerPrefix(final ConsoleSource p0, final User p1);
    
    public abstract Optional<String> getPrefix(final UUID p0);
    
    public abstract Optional<RankUpdate> modifyRank(@Nullable final Player p0, @Nullable final UUID p1, @Nullable final String p2, final boolean p3);
    
    private static PermissionProvider getRank() {
        try {
            Class.forName("net.luckperms.api.LuckPerms");
            PermissionProvider.VERSION = "LuckPerms API 5";
            return new Api5();
        }
        catch (ClassNotFoundException ex) {
            try {
                Class.forName("me.lucko.luckperms.api.LuckPermsApi");
                PermissionProvider.VERSION = "LuckPerms API 4";
                return new Api4();
            }
            catch (ClassNotFoundException ex2) {
                PermissionProvider.VERSION = "None";
                return new DefaultProvider();
            }
        }
    }
    
    public abstract Optional<PermissionResolver> getPermission(final UUID p0);
    
    static {
        INSTANCE = getRank();
    }
    
    public static class RankUpdate
    {
        public final String added;
        public final String removed;
        public final UUID target;
        
        public RankUpdate(final UUID target, final String added, final String removed) {
            this.removed = ((removed != null && removed.equals("null")) ? null : removed);
            this.added = ((added != null && added.equals("null")) ? null : added);
            this.target = target;
        }
    }
}
