// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord;

import org.spongepowered.api.command.source.ConsoleSource;
import net.dirtcraft.discordlink.DiscordLink;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRole;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class IngameCommand implements DiscordCommandExecutor
{
    private final String command;
    private final boolean args;
    private final DiscordRole argsReq;
    private int required;
    
    public IngameCommand(final String command) {
        this.command = command;
        this.args = false;
        this.argsReq = DiscordRoles.NONE;
        this.required = 0;
    }
    
    public IngameCommand(final String command, final DiscordRole role) {
        this.command = command;
        this.args = true;
        this.argsReq = role;
        this.required = 0;
        final Pattern pattern = Pattern.compile("\\{arg}");
        final Matcher matcher = pattern.matcher(command);
        while (matcher.find()) {
            ++this.required;
        }
    }
    
    public void execute(final MessageSource source, final String command, final List<String> args) throws DiscordCommandException {
        if (this.required > args.size()) {
            throw new DiscordCommandException("Expected " + this.required + " arguments, but got " + args.size());
        }
        final String runCommand = this.parseCommand(source, this.command, args);
        final ConsoleSource sender = source.getCommandSource(runCommand);
        Task.builder().execute(() -> Sponge.getCommandManager().process((CommandSource)sender, runCommand)).submit((Object)DiscordLink.get());
    }
    
    private boolean canUseArgs(final MessageSource source) {
        return (this.args && this.argsReq == DiscordRoles.NONE) || source.hasRole(this.argsReq);
    }
    
    private String parseCommand(final MessageSource source, String command, final List<String> args) throws DiscordCommandException {
        if (this.canUseArgs(source)) {
            command = this.parseArguments(command, args);
        }
        return command;
    }
    
    private String parseArguments(String template, final List<String> arguments) {
        while (template.contains("{arg}") && !arguments.isEmpty()) {
            template = template.replaceFirst("\\{arg}", arguments.remove(0));
        }
        return arguments.isEmpty() ? template : (template + " " + String.join(" ", arguments));
    }
}
