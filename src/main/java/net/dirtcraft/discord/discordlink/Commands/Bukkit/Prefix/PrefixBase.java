package net.dirtcraft.discord.discordlink.Commands.Bukkit.Prefix;

import net.dirtcraft.discord.discordlink.Commands.Bukkit.ThermosSubCommand;
import net.dirtcraft.discord.discordlink.Utility.Pair;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrefixBase implements CommandExecutor {
    ThermosSubCommand clear = new Clear();
    ThermosSubCommand group = new Group();
    ThermosSubCommand set = new Set();
    ThermosSubCommand test = new Test();
    ThermosSubCommand toggle = new Toggle();
    Map<String, ThermosSubCommand> commandMap = Stream.of(
            new Pair<>("clear", clear),
            new Pair<>("none", clear),
            new Pair<>("c", clear),
            new Pair<>("group", group),
            new Pair<>("g", group),
            new Pair<>("set", set),
            new Pair<>("s", set),
            new Pair<>("test", test),
            new Pair<>("debug", test),
            new Pair<>("d", test),
            new Pair<>("toggle", toggle),
            new Pair<>("arrow", toggle),
            new Pair<>("star", toggle),
            new Pair<>("t", toggle)
    ).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length != 0 && commandMap.containsKey(strings[0])){
            ThermosSubCommand subCommand = commandMap.get(strings[0]);
            List<String> args = new ArrayList<>(Arrays.asList(strings));
            args.remove(0);
            if (subCommand.hasPermission(commandSender)) return subCommand.onCommand(commandSender, args);
        }
        return false;
    }
}
