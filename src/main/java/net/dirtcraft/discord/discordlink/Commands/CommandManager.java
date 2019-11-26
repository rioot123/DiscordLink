package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.API.DiscordRoles;
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
                .setCommandExecutor(new PlayerList())
                .build();

        DiscordCommand stop = DiscordCommand.builder()
                .setCommandExecutor(new StopServer())
                .setRequiredRoles(DiscordRoles.DIRTY)
                .build();

        DiscordCommand unstuck = DiscordCommand.builder()
                .setCommandExecutor(new Unstuck())
                .setRequiredRoles(DiscordRoles.VERIFIED)
                .build();

        DiscordCommand seen = DiscordCommand.builder()
                .setCommandExecutor(new SilentSeen())
                .setRequiredRoles(DiscordRoles.STAFF)
                .build();

        register(help, "help");
        register(list, "list");
        register(stop, "stop", "halt");
        register(seen, "seen");
        register(unstuck, "unstuck", "spawn");
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
