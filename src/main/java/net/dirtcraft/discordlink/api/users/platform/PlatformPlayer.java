package net.dirtcraft.discordlink.api.users.platform;


import java.util.Optional;

public interface PlatformPlayer {

    String getNameAndPrefix();

    String getName();

    Optional<String> getPrefix();

    boolean isVanished();

    boolean notVanished();

    boolean hasPlayedBefore();

    boolean hasPermission(String perm);

    <T> T getOnlinePlayer();
}
