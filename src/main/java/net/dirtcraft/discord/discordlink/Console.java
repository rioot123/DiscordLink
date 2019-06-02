package net.dirtcraft.discord.discordlink;

import org.spongepowered.api.Sponge;

public class Console {

    public static void toConsole(String command) {
        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
    }
}
