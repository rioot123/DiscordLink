package net.dirtcraft.discord.discordlink.Utility.Platform;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Optional;
import java.util.UUID;

public class PlatformUser {
    private final UUID user;
    private final String name;

    PlatformUser(UUID user, String name){
        this.user = user;
        this.name = name;
    }

    public Optional<String> getName(){
        return Optional.ofNullable(name);
    }

    public UUID getUUID(){
        return user;
    }

    public boolean isOnline(){
        return ProxyServer.getInstance().getPlayer(user) != null;
    }

    ProxiedPlayer getPlayer(){
        return ProxyServer.getInstance().getPlayer(user);
    }
}