// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord.notify;

import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommand;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRole;
import net.dirtcraft.discordlink.commands.DiscordCommandImpl;
import net.dirtcraft.discordlink.commands.DiscordCommandTree;

public class NotifyBase extends DiscordCommandTree
{
    public NotifyBase() {
        final DiscordCommandImpl time = DiscordCommandImpl.builder().setDescription("Sets when the boot failure threshold is reached, In minutes").setPreBootEnabled(true).setRequiredRoles(DiscordRoles.ADMIN).setCommandUsage("[<number>]").setCommandExecutor((DiscordCommandExecutor)new Time()).build();
        final DiscordCommandImpl add = DiscordCommandImpl.builder().setDescription("Starts notifying you when the boot failure threshold is reached.").setPreBootEnabled(true).setRequiredRoles(DiscordRoles.ADMIN).setCommandExecutor((DiscordCommandExecutor)new Add()).build();
        final DiscordCommandImpl list = DiscordCommandImpl.builder().setDescription("List anyone to be notified when the boot failure threshold is reached.").setPreBootEnabled(true).setRequiredRoles(DiscordRoles.ADMIN).setCommandExecutor((DiscordCommandExecutor)new List()).build();
        final DiscordCommandImpl remove = DiscordCommandImpl.builder().setDescription("Stops notifying you when the boot failure threshold is reached.").setPreBootEnabled(true).setRequiredRoles(DiscordRoles.VERIFIED).setCommandExecutor((DiscordCommandExecutor)new Remove()).build();
        this.register((DiscordCommand)time, "time");
        this.register((DiscordCommand)list, "list");
        this.register((DiscordCommand)add, "add");
        this.register((DiscordCommand)remove, "remove");
    }
    
    @Override
    public void defaultResponse(final MessageSource member, final String command, final java.util.List<String> args) throws DiscordCommandException {
        final EmbedBuilder embed = Utility.embedBuilder();
        final String pre = PluginConfiguration.Main.discordCommand;
        final String str;
        String header;
        final EmbedBuilder embedBuilder;
        this.getCommandMap().forEach((alias, cmd) -> {
            if (!cmd.hasPermission((DiscordMember)member)) {
                return;
            }
            else {
                header = str + command + " " + alias + " " + cmd.getUsage();
                embedBuilder.addField(header, cmd.getDescription(), false);
                embedBuilder.setFooter("Requested By: " + member.getUser().getAsTag(), member.getUser().getAvatarUrl());
                return;
            }
        });
        member.sendCommandResponse(embed.build(), 30);
    }
}
