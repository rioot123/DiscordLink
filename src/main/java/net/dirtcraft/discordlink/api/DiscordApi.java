package net.dirtcraft.discordlink.api;

import net.dirtcraft.discordlink.api.channels.ChannelManager;
import net.dirtcraft.discordlink.api.commands.DiscordCommandManager;
import net.dirtcraft.discordlink.api.users.UserManager;
import net.dv8tion.jda.api.JDA;

public interface DiscordApi {
    static JDA getJDA(){
        if (DiscordApiProvider.provider == null) return null;
        return DiscordApiProvider.provider.getJdaInstance();
    }

    JDA getJdaInstance();

    UserManager getUserManager();

    ChannelManager getChannelManager();

    DiscordCommandManager getCommandManager();

    boolean isLoaded();
}
