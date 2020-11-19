package net.dirtcraft.discord.discordlink.Utility.Compatability.Platform;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.annotation.Nonnull;
import java.util.UUID;

public class PlatformPlayer {
    private final ProxiedPlayer player;

    PlatformPlayer(ProxiedPlayer player){
        this.player = player;
    }

    @Nonnull public String getName(){
        return player.getName();
    }

    public UUID getUUID(){
        return player.getUniqueId();
    }

    public ProxiedPlayer getPlayer(){
        return player;
    }

    public boolean isVanished(){
        return false;
    }

    public boolean notVanished(){
        return !isVanished();
    }

    public boolean hasPlayedBefore(){
        return true;
    }

    public boolean hasPermission(String permission){
        return player.hasPermission(permission);
    }

    public void sendMessage(String message){
        player.sendMessage(message);
    }
}