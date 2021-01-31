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

    /**
     * Returns an online player object specific of the platform
     * Sponge: Player
     * Spigot: Player
     * Forge:  EntityPlayerMP
     * @return Platform specific representation of an online player.
     */
    <T> T getOnlinePlayer();
}
