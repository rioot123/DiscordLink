package net.dirtcraft.discord.discordlink.Utility.Compatability.Platform;


import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;

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
        return player.get(Keys.VANISH).orElse(false);
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