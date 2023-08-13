// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands;

import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.discordlink.utility.Utility;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import java.util.ArrayList;
import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.spongediscordlib.commands.DiscordCommand;
import net.dirtcraft.discordlink.commands.discord.Logs;
import net.dirtcraft.discordlink.commands.discord.ItemBase;
import net.dirtcraft.discordlink.commands.discord.Version;
import net.dirtcraft.discordlink.commands.discord.Prefix;
import net.dirtcraft.discordlink.commands.discord.notify.NotifyBase;
import net.dirtcraft.discordlink.commands.discord.Unlink;
import net.dirtcraft.discordlink.commands.discord.Kits;
import net.dirtcraft.discordlink.commands.discord.Ranks;
import net.dirtcraft.discordlink.commands.discord.Discord;
import net.dirtcraft.discordlink.commands.discord.Username;
import net.dirtcraft.discordlink.commands.discord.SilentSeen;
import net.dirtcraft.discordlink.commands.discord.Unstuck;
import net.dirtcraft.discordlink.commands.discord.IngameCommand;
import net.dirtcraft.discordlink.commands.discord.StopServer;
import net.dirtcraft.discordlink.commands.discord.PlayerList;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRole;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.discordlink.commands.discord.mute.MuteBase;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;

public class DiscordCommandManagerImpl extends DiscordCommandTree
{
    private final HashSet<String> defaultAliases;
    
    public DiscordCommandManagerImpl() {
        this.defaultAliases = new HashSet<String>(Arrays.asList("", "help"));
        final DiscordCommandImpl mute = DiscordCommandImpl.builder().setDescription("Command base for mute manipulation").setCommandExecutor((DiscordCommandExecutor)new MuteBase()).setRequiredRoles(DiscordRoles.STAFF).build();
        final DiscordCommandImpl list = DiscordCommandImpl.builder().setDescription("Shows a list of all players online.").setCommandExecutor((DiscordCommandExecutor)new PlayerList()).build();
        final DiscordCommandImpl halt = DiscordCommandImpl.builder().setDescription("Stops the server abruptly.").setCommandExecutor((DiscordCommandExecutor)new StopServer(false)).setPreBootEnabled(true).setRequiredRoles(DiscordRoles.ADMIN).build();
        final DiscordCommandImpl stop = DiscordCommandImpl.builder().setDescription("Stops the server gracefully.").setCommandExecutor((DiscordCommandExecutor)new StopServer(true)).setPreBootEnabled(true).setRequiredRoles(DiscordRoles.ADMIN).build();
        final DiscordCommandImpl reboot = DiscordCommandImpl.builder().setDescription("Reboot the server in X minutes.").setCommandUsage("<Minutes>").setCommandExecutor((DiscordCommandExecutor)new IngameCommand("restart start {arg} -m", DiscordRoles.NONE)).setPreBootEnabled(false).setRequiredRoles(DiscordRoles.STAFF).build();
        final DiscordCommandImpl unstuck = DiscordCommandImpl.builder().setDescription("Teleports you to spawn if you are verified.").setCommandExecutor((DiscordCommandExecutor)new Unstuck()).setRequiredRoles(DiscordRoles.VERIFIED).build();
        final DiscordCommandImpl seen = DiscordCommandImpl.builder().setDescription("Sends you a DM with a players info.").setCommandExecutor((DiscordCommandExecutor)new SilentSeen()).setCommandUsage("<Player>").setRequiredRoles(DiscordRoles.STAFF).build();
        final DiscordCommandImpl username = DiscordCommandImpl.builder().setDescription("Reveals a verified players minecraft username.").setCommandExecutor((DiscordCommandExecutor)new Username()).setCommandUsage("<@Discord>").setRequiredRoles(DiscordRoles.STAFF).build();
        final DiscordCommandImpl discord = DiscordCommandImpl.builder().setDescription("Reveals a verified players discord username.").setCommandExecutor((DiscordCommandExecutor)new Discord()).setCommandUsage("<Player>").setRequiredRoles(DiscordRoles.STAFF).build();
        final DiscordCommandImpl ranks = DiscordCommandImpl.builder().setDescription("Reveals a players ranks.").setCommandExecutor((DiscordCommandExecutor)new Ranks()).setRequiredRoles(DiscordRoles.VERIFIED).build();
        final DiscordCommandImpl kits = DiscordCommandImpl.builder().setDescription("Reveals a players kits.").setCommandExecutor((DiscordCommandExecutor)new Kits()).setRequiredRoles(DiscordRoles.VERIFIED).build();
        final DiscordCommandImpl sync = DiscordCommandImpl.builder().setDescription("Runs LP Sync to re-sync the perms").setCommandExecutor((DiscordCommandExecutor)new IngameCommand("lp sync")).setRequiredRoles(DiscordRoles.STAFF).build();
        final DiscordCommandImpl unverify = DiscordCommandImpl.builder().setDescription("Unverifies your account.").setCommandExecutor((DiscordCommandExecutor)new Unlink()).build();
        final DiscordCommandImpl notify = DiscordCommandImpl.builder().setDescription("Shows server-boot notifier commands").setPreBootEnabled(true).setCommandExecutor((DiscordCommandExecutor)new NotifyBase()).build();
        final DiscordCommandImpl prefix = DiscordCommandImpl.builder().setDescription("Sets prefixes").setCommandUsage("<title>").setRequiredRoles(DiscordRoles.STAFF).setCommandExecutor((DiscordCommandExecutor)new Prefix()).build();
        final DiscordCommandImpl version = DiscordCommandImpl.builder().setDescription("Shows the current version").setRequiredRoles(DiscordRoles.MOD).setCommandExecutor((DiscordCommandExecutor)new Version()).build();
        final DiscordCommandImpl inv = DiscordCommandImpl.builder().setDescription("Debug").setRequiredRoles(DiscordRoles.MOD).setCommandExecutor((DiscordCommandExecutor)new ItemBase()).build();
        final DiscordCommandImpl logs = DiscordCommandImpl.builder().setDescription("Shows latest logs").setRequiredRoles(DiscordRoles.MOD).setCommandExecutor((DiscordCommandExecutor)new Logs()).setPreBootEnabled(true).build();
        this.register((DiscordCommand)mute, "mute");
        this.register((DiscordCommand)list, "list", "players");
        this.register((DiscordCommand)stop, "stop");
        this.register((DiscordCommand)halt, "halt");
        this.register((DiscordCommand)reboot, "reboot", "restart");
        this.register((DiscordCommand)seen, "seen");
        this.register((DiscordCommand)unstuck, "unstuck", "spawn");
        this.register((DiscordCommand)username, "username");
        this.register((DiscordCommand)discord, "discord");
        this.register((DiscordCommand)ranks, "ranks", "groups", "parents");
        this.register((DiscordCommand)sync, "sync");
        this.register((DiscordCommand)unverify, "unverify", "unlink");
        this.register((DiscordCommand)notify, "notify");
        this.register((DiscordCommand)prefix, "prefix");
        this.register((DiscordCommand)kits, "kits");
        this.register((DiscordCommand)version, "version", "info");
        this.register((DiscordCommand)logs, "logs");
        this.register((DiscordCommand)inv, "inv");
    }
    
    public void process(final MessageSourceImpl member, final String args) {
        try {
            final String[] command = (args == null || this.defaultAliases.contains(args)) ? new String[0] : args.split(" ");
            this.execute((MessageSource)member, null, new ArrayList<String>(Arrays.asList(command)));
        }
        catch (Exception e) {
            final String message = (e.getMessage() != null) ? e.getMessage() : "an error occurred while executing the command.";
            Utility.sendCommandError((MessageSource)member, message);
        }
        finally {
            if (!member.isPrivateMessage()) {
                member.getMessage().delete().queue();
            }
        }
    }
    
    @Override
    public void defaultResponse(final MessageSource member, final String command, final List<String> args) throws DiscordCommandException {
        final EmbedBuilder embed = Utility.embedBuilder();
        final String pre = PluginConfiguration.Main.discordCommand;
        final String str;
        String title;
        final EmbedBuilder embedBuilder;
        this.getCommandMap().forEach((alias, cmd) -> {
            if (!cmd.hasPermission((DiscordMember)member)) {
                return;
            }
            else {
                title = str + alias + " " + cmd.getUsage();
                embedBuilder.addField(title, cmd.getDescription(), false);
                embedBuilder.setFooter("Requested By: " + member.getUser().getAsTag(), member.getUser().getAvatarUrl());
                return;
            }
        });
        member.sendCommandResponse(embed.build());
    }
}
