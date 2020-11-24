package net.dirtcraft.discord.discordlink.Commands.Bungee.Settings;

import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Sanctions.SanctionUtils;
import net.dirtcraft.discord.discordlink.Utility.Pair;
import net.md_5.bungee.api.CommandSender;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChannelSettings implements SubCommand {
    Map<String, SubCommand> fields = Stream.of(
            new Pair<String, SubCommand>("litebans-log", this::litebans),
            new Pair<String, SubCommand>("hub-gamechat", this::gamechatHub),
            new Pair<String, SubCommand>("gamechat-category", this::gamechatCategory),
            new Pair<String, SubCommand>("server-log", this::serverLog),
            new Pair<String, SubCommand>("command-log", this::commandLog),
            new Pair<String, SubCommand>("player-count", this::playerCount)
    ).collect(Collectors.toMap(Pair::getKey, Pair::getValue));

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            sender.sendMessage("<" + String.join("|", fields.keySet()) + ">");
            return;
        }
        SubCommand subCommand = fields.get(args.remove(0));
        if (subCommand == null) sender.sendMessage("<" + String.join("|", fields.keySet()) + ">");
        else subCommand.execute(sender, args);
    }

    public void litebans(CommandSender sender, List<String> args) {
        if (args.isEmpty()) sender.sendMessage(String.valueOf(PluginConfiguration.Channels.litebansChannel));
        else if (!args.get(0).matches("\\d+")) sender.sendMessage("invalid number!");
        else {
            PluginConfiguration.Channels.litebansChannel = Long.parseLong(args.get(0));
            SanctionUtils.INSTANCE.updateChannel(PluginConfiguration.Channels.litebansChannel);
            DiscordLink.getInstance().saveConfig();
        }
    }

    public void gamechatHub(CommandSender sender, List<String> args) {
        if (args.isEmpty()) sender.sendMessage(String.valueOf(PluginConfiguration.Channels.gamechatChannel));
        else if (!args.get(0).matches("\\d+")) sender.sendMessage("invalid number!");
        else {
            PluginConfiguration.Channels.gamechatChannel = Long.parseLong(args.get(0));
            DiscordLink.getInstance().saveConfig();
            Channels.update();
        }
    }

    public void gamechatCategory(CommandSender sender, List<String> args) {
        if (args.isEmpty()) sender.sendMessage(String.valueOf(PluginConfiguration.Channels.gamechatCategory));
        else if (!args.get(0).matches("\\d+")) sender.sendMessage("invalid number!");
        else {
            PluginConfiguration.Channels.gamechatCategory = Long.parseLong(args.get(0));
            DiscordLink.getInstance().saveConfig();
            Channels.update();
        }
    }

    public void serverLog(CommandSender sender, List<String> args) {
        if (args.isEmpty()) sender.sendMessage(String.valueOf(PluginConfiguration.Channels.serverLogChannel));
        else if (!args.get(0).matches("\\d+")) sender.sendMessage("invalid number!");
        else {
            PluginConfiguration.Channels.serverLogChannel = Long.parseLong(args.get(0));
            DiscordLink.getInstance().saveConfig();
            Channels.update();
        }
    }

    public void commandLog(CommandSender sender, List<String> args) {
        if (args.isEmpty()) sender.sendMessage(String.valueOf(PluginConfiguration.Channels.commandLogChannel));
        else if (!args.get(0).matches("\\d+")) sender.sendMessage("invalid number!");
        else {
            PluginConfiguration.Channels.commandLogChannel = Long.parseLong(args.get(0));
            DiscordLink.getInstance().saveConfig();
            Channels.update();
        }
    }

    public void playerCount(CommandSender sender, List<String> args) {
        if (args.isEmpty()) sender.sendMessage(String.valueOf(PluginConfiguration.Channels.playerCountChannel));
        else if (!args.get(0).matches("\\d+")) sender.sendMessage("invalid number!");
        else {
            PluginConfiguration.Channels.playerCountChannel = Long.parseLong(args.get(0));
            DiscordLink.getInstance().saveConfig();
            Channels.update();
        }
    }
}
