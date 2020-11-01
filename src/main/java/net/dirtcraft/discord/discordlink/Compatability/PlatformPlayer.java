package net.dirtcraft.discord.discordlink.Compatability;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.UUID;

public class PlatformPlayer {
    private final Player player;

    PlatformPlayer(Player player){
        this.player = player;
    }

    @Nonnull public String getName(){
        return player.getName();
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
}
