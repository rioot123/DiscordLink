package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;

public class StopServer implements DiscordCommandExecutor {
    @Override
    public void execute(Member member, String[] command, MessageReceivedEvent event) {
        Role ownerRole = event.getGuild().getRoleById(PluginConfiguration.Roles.ownerRoleID);
        Role dirtyRole = event.getGuild().getRoleById(PluginConfiguration.Roles.dirtyRoleID);
        List<Role> roles = event.getMember().getRoles();
        if (roles.contains(ownerRole) || roles.contains(dirtyRole)) {
            FMLCommonHandler.instance().exitJava(-1, true);
            Utility.sendResponse(event, "Emergency shutdown has been executed. Please wait.", 15);
        } else {
            Utility.sendPermissionErrorMessage(event);
        }
    }
}
