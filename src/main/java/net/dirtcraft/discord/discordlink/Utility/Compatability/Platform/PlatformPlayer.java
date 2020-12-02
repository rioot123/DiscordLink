package net.dirtcraft.discord.discordlink.Utility.Compatability.Platform;

import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public class PlatformPlayer {
    private final Player player;

    PlatformPlayer(Player player){
        this.player = player;
    }

    public String getNameAndPrefix(){
        return getPrefix()
                .map(pre->pre + " " + getName())
                .orElse(getName());
    }

    @Nonnull public String getName(){
        return player.getName();
    }

    public Optional<String> getPrefix(){
        return PermissionUtils.INSTANCE.getPrefix(player.getUniqueId());
    }

    public UUID getUUID(){
        return player.getUniqueId();
    }

    public Player getPlayer(){
        return player;
    }

    public boolean isVanished(){
        return PlatformUtils.vanishProvider.isVanished(this);
    }

    public boolean notVanished(){
        return !isVanished();
    }

    public boolean hasPlayedBefore(){
        return player.hasPlayedBefore();
    }

    public boolean hasPermission(String group){
        return player.hasPermission(group);
    }
}
