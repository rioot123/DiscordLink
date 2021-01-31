package net.dirtcraft.discordlink.api.users;

import net.dirtcraft.discordlink.api.users.platform.PlatformPlayer;
import net.dirtcraft.discordlink.api.users.platform.PlatformUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserManager {
    Optional<? extends DiscordMember> getMember(long id);

    Optional<? extends DiscordMember> getMember(UUID player);

    Optional<? extends DiscordMember> getMember(String s);

    Optional<? extends PlatformUser> getUser(String s);

    Optional<? extends PlatformUser> getUser(UUID uuid);

    List<? extends PlatformPlayer> getPlayers();
}
