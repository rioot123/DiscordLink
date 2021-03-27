package net.dirtcraft.discordlink.forge.platform;


import net.dirtcraft.discordlink.common.users.permission.PermissionProvider;
import net.dirtcraft.discordlink.api.users.platform.PlatformPlayer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerList;

import java.util.Optional;
import java.util.UUID;

public final class PlatformPlayerImpl extends PlatformUserImpl implements PlatformPlayer {
    private final ServerPlayerEntity player;

    PlatformPlayerImpl(ServerPlayerEntity player, PlayerList list, PlatformProvider provider){
        super(player.getGameProfile(), list, provider);
        this.player = player;
    }

    @Override
    public String getNameAndPrefix(){
        return getPrefix()
                .map(pre->pre + " " + getName())
                .orElse(getName());
    }

    @Override
    public Optional<String> getPrefix(){
        return Optional.empty();
    }

    @Override
    public boolean isVanished(){
        return false;
    }

    @Override
    public boolean notVanished(){
        return true;
    }

    @Override
    public boolean hasPlayedBefore(){
        return true;
    }

    @Override
    public boolean hasPermission(String perm){
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOnlinePlayer(){
        return (T) player;
    }
}