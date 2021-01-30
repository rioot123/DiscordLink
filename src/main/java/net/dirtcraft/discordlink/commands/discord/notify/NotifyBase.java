package net.dirtcraft.discordlink.commands.discord.notify;

import net.dirtcraft.discordlink.users.MessageSource;
import net.dirtcraft.discordlink.users.discord.Roles;
import net.dirtcraft.discordlink.commands.discord.notify.Add;
import net.dirtcraft.discordlink.commands.discord.notify.List;
import net.dirtcraft.discordlink.commands.discord.notify.Remove;
import net.dirtcraft.discordlink.commands.discord.notify.Time;
import net.dirtcraft.discordlink.commands.DiscordCommandImpl;
import net.dirtcraft.discordlink.commands.DiscordCommandTree;
import net.dirtcraft.discordlink.api.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.discordlink.utility.Utility;
import net.dv8tion.jda.api.EmbedBuilder;

public class NotifyBase extends DiscordCommandTree {
    public NotifyBase(){
        DiscordCommandImpl time = DiscordCommandImpl.builder()
                .setDescription("Sets when the boot failure threshold is reached, In minutes")
                .setPreBootEnabled(true)
                .setRequiredRoles(Roles.ADMIN)
                .setCommandUsage("[<number>]")
                .setCommandExecutor(new Time())
                .build();

        DiscordCommandImpl add = DiscordCommandImpl.builder()
                .setDescription("Starts notifying you when the boot failure threshold is reached.")
                .setPreBootEnabled(true)
                .setRequiredRoles(Roles.ADMIN)
                .setCommandExecutor(new Add())
                .build();

        DiscordCommandImpl list = DiscordCommandImpl.builder()
                .setDescription("List anyone to be notified when the boot failure threshold is reached.")
                .setPreBootEnabled(true)
                .setRequiredRoles(Roles.ADMIN)
                .setCommandExecutor(new List())
                .build();

        DiscordCommandImpl remove = DiscordCommandImpl.builder()
                .setDescription("Stops notifying you when the boot failure threshold is reached.")
                .setPreBootEnabled(true)
                .setRequiredRoles(Roles.VERIFIED)
                .setCommandExecutor(new Remove())
                .build();

        register(time, "time");
        register(list, "list");
        register(add, "add");
        register(remove, "remove");
    }

    @Override
    public void defaultResponse(MessageSource member, String command, java.util.List<String> args) throws DiscordCommandException {
        EmbedBuilder embed = Utility.embedBuilder();
        String pre = PluginConfiguration.Main.discordCommand;
        getCommandMap().forEach((alias, cmd)->{
            if (!cmd.hasPermission(member)) return;
            String header = pre + command + " " + alias + " " + cmd.getUsage();
            embed.addField(header, cmd.getDescription(), false);
            embed.setFooter("Requested By: " + member.getUser().getAsTag(), member.getUser().getAvatarUrl());
        });
        member.sendCommandResponse(embed.build(), 30);
    }
}