package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.DiscordSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;

public class StopServer implements DiscordCommandExecutor {
    private final Boolean gracefulExit;

    public StopServer(boolean graceful){
        this.gracefulExit = graceful;
    }
    public StopServer(){
        this.gracefulExit = null;
    }

    @Override
    public void execute(DiscordSource source, String[] args, MessageReceivedEvent event) {
        Role ownerRole = event.getGuild().getRoleById(PluginConfiguration.Roles.ownerRoleID);
        Role dirtyRole = event.getGuild().getRoleById(PluginConfiguration.Roles.dirtyRoleID);
        boolean gracefulExit = this.gracefulExit != null ? this.gracefulExit : args.length >= 2 && args[1].equals("-s");
        List<Role> roles = event.getMember().getRoles();
        if (roles.contains(ownerRole) || roles.contains(dirtyRole)) {
            Utility.sendResponse(event, "Attempting to reboot the server.", 15);
            FMLCommonHandler.instance().exitJava(-1, !gracefulExit);
        } else {
            Utility.sendPermissionErrorMessage(event);
        }
    }
}
