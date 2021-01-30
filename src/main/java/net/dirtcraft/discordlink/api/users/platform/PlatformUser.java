package net.dirtcraft.discordlink.api.users.platform;


import net.dirtcraft.discordlink.users.platform.PlatformPlayerImpl;

import java.util.Optional;
import java.util.UUID;

public interface PlatformUser {

    Optional<String> getNameIfPresent();

    UUID getUUID();

    boolean isOnline();

    Optional<PlatformPlayerImpl> getPlatformPlayer();

    @SuppressWarnings("unchecked")
    <T> T getOfflinePlayer();
}
