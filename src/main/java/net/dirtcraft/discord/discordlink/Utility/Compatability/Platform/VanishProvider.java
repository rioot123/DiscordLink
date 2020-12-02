package net.dirtcraft.discord.discordlink.Utility.Compatability.Platform;

import org.bukkit.Bukkit;

public interface VanishProvider {
    boolean isVanished(PlatformPlayer platformPlayer);

    class Essentials implements VanishProvider{
        com.earth2me.essentials.Essentials ess;
        public Essentials() throws Exception{
             ess = (com.earth2me.essentials.Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
             if (ess == null) throw new Exception("Could not load essentials. Player vanish values will not be considered.");
        }

        public boolean isVanished(PlatformPlayer platformPlayer){
            return ess.getUser(platformPlayer.getPlayer()).isVanished();
        }
    }

    class Null implements VanishProvider{
        public boolean isVanished(PlatformPlayer platformPlayer){
            return false;
        }
    }
}
