package net.dirtcraft.discordlink.api.users.platform;


import net.dirtcraft.discordlink.users.platform.PlatformPlayerImpl;

import java.util.Optional;
import java.util.UUID;

public interface PlatformUser {

    Optional<String> getNameIfPresent();

    UUID getUUID();

    boolean isOnline();

    Optional<PlatformPlayerImpl> getPlatformPlayer();


    /**
     * Returns an offline player object specific of the platform
     * Sponge: User
     * Spigot: OfflinePlayer
     * Forge:  GameProfile
     * @return Platform specific representation of an offline player.
     */
    <T> T getOfflinePlayer();
}
