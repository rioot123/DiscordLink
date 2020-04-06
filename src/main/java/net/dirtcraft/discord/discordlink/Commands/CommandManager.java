package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.Discord.*;
import net.dirtcraft.discord.discordlink.Commands.Sponge.UnVerify;
import net.dirtcraft.discord.discordlink.Commands.Sponge.Verify;
import net.dirtcraft.discord.discordlink.Database.Storage;
import net.dirtcraft.discord.discordlink.DiscordLink;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private final Storage storage;
    private final HashMap<String, DiscordCommand> commandMap;

    public CommandManager(DiscordLink main, Storage storage, HashMap<String, DiscordCommand> commandMap) {
        this.storage = storage;
        this.commandMap = commandMap;
        Sponge.getCommandManager().register(main, this.verify(), "verify", "link");
        Sponge.getCommandManager().register(main, this.unverify(), "unverify", "unlink");

        DiscordCommand help = DiscordCommand.builder()
                .setCommandExecutor(new Help())
                .build();

        DiscordCommand list = DiscordCommand.builder()
                .setDescription("Shows a list of all players online.")
                .setCommandExecutor(new PlayerList())
                .build();

        DiscordCommand halt = DiscordCommand.builder()
                .setDescription("Stops the server abruptly.")
                .setCommandExecutor(new StopServer(false))
                .setRequiredRoles(Roles.ADMIN)
                .build();

        DiscordCommand stop = DiscordCommand.builder()
                .setDescription("Stops the server gracefully.")
                .setCommandExecutor(new StopServer(true))
                .setRequiredRoles(Roles.ADMIN)
                .build();

        DiscordCommand unstuck = DiscordCommand.builder()
                .setDescription("Teleports you to spawn if you are verified.")
                .setCommandExecutor(new Unstuck())
                .setRequiredRoles(Roles.VERIFIED)
                .build();

        DiscordCommand seen = DiscordCommand.builder()
                .setDescription("Sends you a DM with a players info.")
                .setCommandExecutor(new SilentSeen())
                .setRequiredRoles(Roles.STAFF)
                .build();

        DiscordCommand username = DiscordCommand.builder()
                .setDescription("Reveals a verified players minecraft username.")
                .setCommandExecutor(new Username())
                .setRequiredRoles(Roles.STAFF)
                .build();

        DiscordCommand discord = DiscordCommand.builder()
                .setDescription("Reveals a verified players discord username.")
                .setCommandExecutor(new Discord())
                .setRequiredRoles(Roles.STAFF)
                .build();

        DiscordCommand ranks = DiscordCommand.builder()
                .setDescription("Reveals a players ranks.")
                .setCommandExecutor(new Rank())
                .setRequiredRoles(Roles.STAFF)
                .build();

        DiscordCommand unverify = DiscordCommand.builder()
                .setDescription("Unverifies your account.")
                .setCommandExecutor(new Unlink())
                .setRequiredRoles(Roles.VERIFIED)
                .build();

        register(help, "help");
        register(list, "list");
        register(stop, "stop");
        register(halt, "halt");
        register(seen, "seen");
        register(unstuck, "unstuck", "spawn");
        register(username, "username");
        register(discord, "discord");
        register(ranks, "ranks");
        register(unverify, "unverify", "unlink");
    }

    public void register(DiscordCommand command, String... alias){
        for (String name : alias) {
            commandMap.put(name, command);
        }
    }

    public Map<String, DiscordCommand> getCommandMap(){
        Map<String, DiscordCommand> result = new HashMap<>();
        commandMap.forEach((alias, cmd) -> {
            if (result.containsValue(cmd)) return;
            result.put(alias, cmd);
        });
        return result;
    }

    public CommandSpec verify() {
        return CommandSpec.builder()
                .description(Text.of("Verifies your Discord account"))
                .executor(new Verify(storage))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("code"))))
                .build();
    }

    public CommandSpec unverify() {
        return CommandSpec.builder()
                .description(Text.of("Unverifies your Discord account"))
                .executor(new UnVerify(storage))
                .build();
    }

}
