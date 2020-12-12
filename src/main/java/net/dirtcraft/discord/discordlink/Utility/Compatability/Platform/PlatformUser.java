package net.dirtcraft.discord.discordlink.Utility.Compatability.Platform;

import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class PlatformUser {
    private final OfflinePlayer user;

    PlatformUser(OfflinePlayer user){
        this.user = user;
    }

    public Optional<String> getName(){
        return Optional.ofNullable(user.getName());
    }

    public UUID getUUID(){
        return user.getUniqueId();
    }

    public OfflinePlayer getUser(){
        return user;
    }

    public boolean isOnline(){
        return user.isOnline();
    }

    Player getPlayer(){
        return user.getPlayer();
    }

    public boolean hasPermission(String group){
        return PermissionUtils.INSTANCE.hasPermission(user.getUniqueId(), group);
    }
}
