// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.users.platform;

import org.spongepowered.api.entity.living.player.Player;
import java.util.function.Function;
import net.dirtcraft.spongediscordlib.users.platform.PlatformPlayer;
import java.util.UUID;
import java.util.Optional;
import org.spongepowered.api.entity.living.player.User;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;

public class PlatformUserImpl implements PlatformUser
{
    private final User user;
    
    PlatformUserImpl(final User user) {
        this.user = user;
    }
    
    public Optional<String> getNameIfPresent() {
        return Optional.of(this.user.getName());
    }
    
    public UUID getUUID() {
        return this.user.getUniqueId();
    }
    
    public boolean isOnline() {
        return this.user.isOnline();
    }
    
    public Optional<PlatformPlayer> getPlatformPlayer() {
        return this.user.getPlayer().map(PlatformPlayerImpl::new);
    }
    
    public <T> T getOfflinePlayer() {
        return (T)this.user;
    }
    
    public User getUser() {
        return this.user;
    }
    
    public Optional<Player> getPlayerIfOnline() {
        return (Optional<Player>)this.user.getPlayer();
    }
}
