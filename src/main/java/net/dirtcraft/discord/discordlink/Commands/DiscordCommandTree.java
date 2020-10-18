package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.*;

public class DiscordCommandTree implements DiscordCommandExecutor {

    protected final HashMap<String, DiscordCommand> commandMap = new HashMap<>();

    public void register(DiscordCommand command, String... alias){
        for (String name : alias) {
            commandMap.put(name, command);
        }
    }

    public void process(GuildMember member, String args, MessageReceivedEvent event){
        List<String> cmd = new ArrayList<>(Arrays.asList(args.split(" ")));
        execute(member, cmd, event);
    }

    @Override
    public void execute(GuildMember member, List<String> args, MessageReceivedEvent event){
        if (args.size() == 0) return;

        String base = args.remove(0);
        DiscordCommand command = commandMap.get(base);

        if (command != null) command.process(member, args, event);
    }

    public Map<String, DiscordCommand> getCommandMap(){
        Map<String, DiscordCommand> result = new HashMap<>();
        commandMap.forEach((alias, cmd) -> {
            if (result.containsValue(cmd)) return;
            result.put(alias, cmd);
        });
        return result;
    }
}
