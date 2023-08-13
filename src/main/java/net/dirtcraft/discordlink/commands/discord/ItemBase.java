// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord;

import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.discordlink.utility.Utility;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommand;
import net.dirtcraft.discordlink.commands.discord.item.ItemRemove;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.discordlink.commands.discord.item.ItemList;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRole;
import net.dirtcraft.discordlink.commands.DiscordCommandImpl;
import net.dirtcraft.discordlink.commands.DiscordCommandTree;

public class ItemBase extends DiscordCommandTree
{
    DiscordCommandImpl list;
    
    public ItemBase() {
        this.list = DiscordCommandImpl.builder().setDescription("Lists a players items via DM.").setCommandUsage("<Username>").setRequiredRoles(DiscordRoles.MOD).setCommandExecutor((DiscordCommandExecutor)new ItemList()).build();
        final DiscordCommandImpl remove = DiscordCommandImpl.builder().setDescription("Removes a specific slot from a player.").setCommandUsage("<Username> <ID>").setRequiredRoles(DiscordRoles.MOD).setCommandExecutor((DiscordCommandExecutor)new ItemRemove()).build();
        this.register((DiscordCommand)this.list, "list");
        this.register((DiscordCommand)remove, "remove");
    }
    
    @Override
    public void defaultResponse(final MessageSource member, final String command, final List<String> args) throws DiscordCommandException {
        if (!args.isEmpty() && !this.defaults.contains(args.get(0))) {
            this.list.process(member, command, args);
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
