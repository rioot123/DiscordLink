// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord.mute;

import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.discordlink.utility.Utility;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommand;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRole;
import net.dirtcraft.discordlink.commands.DiscordCommandImpl;
import net.dirtcraft.discordlink.commands.DiscordCommandTree;

public class MuteBase extends DiscordCommandTree
{
    DiscordCommandImpl add;
    
    public MuteBase() {
        this.add = DiscordCommandImpl.builder().setDescription("Mutes a player!").setCommandUsage("<@Discord> [duration] [reason]").setRequiredRoles(DiscordRoles.MOD).setCommandExecutor((DiscordCommandExecutor)new Mute()).build();
        final DiscordCommandImpl remove = DiscordCommandImpl.builder().setDescription("Removes a players mute").setCommandUsage("<@Discord>").setRequiredRoles(DiscordRoles.MOD).setCommandExecutor((DiscordCommandExecutor)new Unmute()).build();
        final DiscordCommandImpl info = DiscordCommandImpl.builder().setDescription("Shows the details of a mute").setCommandUsage("<@Discord>").setRequiredRoles(DiscordRoles.STAFF).setCommandExecutor((DiscordCommandExecutor)new MuteInfo()).build();
        this.register((DiscordCommand)this.add, "add");
        this.register((DiscordCommand)remove, "remove");
        this.register((DiscordCommand)info, "info");
    }
    
    @Override
    public void defaultResponse(final MessageSource member, final String command, final List<String> args) throws DiscordCommandException {
        if (!args.isEmpty() && !this.defaults.contains(args.get(0))) {
            this.add.process(member, command, args);
            return;
        }
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
