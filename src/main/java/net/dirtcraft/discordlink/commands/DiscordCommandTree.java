// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands;

import java.util.Map;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommand;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandManager;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public abstract class DiscordCommandTree implements DiscordCommandExecutor, DiscordCommandManager
{
    protected final HashMap<String, DiscordCommandImpl> commandMap;
    protected final HashSet<String> defaults;
    
    public DiscordCommandTree() {
        this.commandMap = new HashMap<String, DiscordCommandImpl>();
        this.defaults = new HashSet<String>(Arrays.asList("help", "?"));
    }
    
    public void register(final DiscordCommand command, final String... alias) {
        if (!(command instanceof DiscordCommandImpl)) {
            return;
        }
        for (final String name : alias) {
            this.commandMap.put(name.toLowerCase(), (DiscordCommandImpl)command);
        }
    }
    
    public void execute(final MessageSource member, final String command, final List<String> args) throws DiscordCommandException {
        if (args.size() == 0 || this.defaults.contains(args.get(0))) {
            this.defaultResponse(member, command, args);
            return;
        }
        final String base = args.get(0);
        final DiscordCommandImpl discordCommand = this.commandMap.get(base.toLowerCase());
        if (discordCommand != null) {
            args.remove(0);
            discordCommand.process(member, base, args);
        }
        else {
            this.defaultResponse(member, command, args);
        }
    }
    
    public Map<String, DiscordCommandImpl> getCommandMap() {
        final Map<String, DiscordCommandImpl> result = new HashMap<String, DiscordCommandImpl>();
        final Map<String, DiscordCommandImpl> map;
        this.commandMap.forEach((alias, cmd) -> {
            if (map.containsValue(cmd)) {
                return;
            }
            else {
                map.put(alias, cmd);
                return;
            }
        });
        return result;
    }
    
    public void defaultResponse(final MessageSource member, final String command, final List<String> args) throws DiscordCommandException {
        throw new DiscordCommandException("Command not found");
    }
}
