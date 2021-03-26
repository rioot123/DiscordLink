package net.dirtcraft.discordlink.api;

import net.dirtcraft.discordlink.api.channels.ChannelManager;
import net.dirtcraft.discordlink.api.commands.DiscordCommandManager;
import net.dirtcraft.discordlink.api.users.UserManager;
import net.dirtcraft.discordlink.api.users.roles.RoleManager;
import net.dv8tion.jda.api.JDA;

public interface DiscordApi {
    JDA getJda();

    UserManager getUserManager();

    ChannelManager getChannelManager();

    DiscordCommandManager getCommandManager();

    RoleManager getRoleManager();

    boolean isLoaded();
}
