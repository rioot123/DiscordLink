// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.users.platform;

import org.spongepowered.api.data.key.Keys;
import java.util.UUID;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import java.util.Optional;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.Player;
import net.dirtcraft.spongediscordlib.users.platform.PlatformPlayer;

public class PlatformPlayerImpl extends PlatformUserImpl implements PlatformPlayer
{
    private final Player player;
    
    PlatformPlayerImpl(final Player player) {
        super((User)player);
        this.player = player;
    }
    
    public String getNameAndPrefix() {
        return this.getPrefix().map(pre -> pre + " " + this.getName()).orElse(this.getName());
    }
    
    public String getName() {
        return this.player.getName();
    }
    
    public Optional<String> getPrefix() {
        if (this.player.getOption("prefix").isPresent()) {
            return (Optional<String>)this.player.getOption("prefix");
        }
        return PermissionProvider.INSTANCE.getPrefix(this.player.getUniqueId());
    }
    
    @Override
    public UUID getUUID() {
        return this.player.getUniqueId();
    }
    
    public boolean isVanished() {
        return this.player.get(Keys.VANISH).orElse(false);
    }
    
    public boolean notVanished() {
        return !this.isVanished();
    }
    
    public boolean hasPlayedBefore() {
        return this.player.hasPlayedBefore();
    }
    
    public boolean hasPermission(final String perm) {
        return this.player.hasPermission(perm);
    }
    
    public <T> T getOnlinePlayer() {
        return (T)this.player;
    }
    
    public Player getPlayer() {
        return this.player;
    }
}
