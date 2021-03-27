package net.dirtcraft.discordlink.forge.platform;

import com.mojang.authlib.GameProfile;
import net.dirtcraft.discordlink.api.users.platform.PlatformPlayer;
import net.dirtcraft.discordlink.api.users.platform.PlatformUser;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerList;

import java.util.Optional;
import java.util.UUID;

public class PlatformUserImpl implements PlatformUser {
    protected final PlatformProvider provider;
    protected final PlayerList list;
    protected final GameProfile user;

    PlatformUserImpl(GameProfile user, PlayerList list, PlatformProvider provider){
        this.provider = provider;
        this.list = list;
        this.user = user;
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public UUID getUUID(){
        return user.getId();
    }

    @Override
    public boolean isOnline(){
        return list.getPlayerByUUID(user.getId()) != null;
    }

    @Override
    public Optional<PlatformPlayer> getPlatformPlayer(){
        ServerPlayerEntity player = list.getPlayerByUUID(getUUID());
        if (player == null) return Optional.empty();
        else return Optional.of(new PlatformPlayerImpl(player, list, provider));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOfflinePlayer(){
        return (T) user;
    }
}