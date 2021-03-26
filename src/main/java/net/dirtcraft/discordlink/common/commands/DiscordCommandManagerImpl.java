package net.dirtcraft.discordlink.common.commands;

import net.dirtcraft.discordlink.common.storage.PluginConfiguration;
import net.dirtcraft.discordlink.common.users.MessageSourceImpl;
import net.dirtcraft.discordlink.common.utility.Utility;
import net.dirtcraft.discordlink.api.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.api.users.MessageSource;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class DiscordCommandManagerImpl extends DiscordCommandTree {

    private final HashSet<String> defaultAliases = new HashSet<>(Arrays.asList("", "help"));

    public DiscordCommandManagerImpl() {

    }

    public void process(MessageSourceImpl member, String args){
        try {
            String[] command = args == null || defaultAliases.contains(args)? new String[0] : args.split(" ");
            execute(member, null, new ArrayList<>(Arrays.asList(command)));
        } catch (Exception e){
            String message = e.getMessage() != null? e.getMessage() : "an error occurred while executing the command.";
            Utility.sendCommandError(member, message);
        } finally {
            if (!member.isPrivateMessage()) member.getMessage().delete().queue();
        }
    }

    @Override
    public void defaultResponse(MessageSource member, String command, java.util.List<String> args) throws DiscordCommandException {
        EmbedBuilder embed = Utility.embedBuilder();
        String pre = PluginConfiguration.Main.discordCommand;
        getCommandMap().forEach((alias, cmd)->{
            if (!cmd.hasPermission(member)) return;
            String title = pre + alias + " " + cmd.getUsage();
            embed.addField(title, cmd.getDescription(), false);
            embed.setFooter("Requested By: " + member.getUser().getAsTag(), member.getUser().getAvatarUrl());
        });
        member.sendCommandResponse(embed.build());
    }
}
