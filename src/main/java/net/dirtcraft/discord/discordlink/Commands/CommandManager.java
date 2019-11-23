package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.Commands.Discord.DiscordCommand;
import net.dirtcraft.discord.discordlink.Commands.Discord.PlayerList;
import net.dirtcraft.discord.discordlink.Commands.Discord.StopServer;
import net.dirtcraft.discord.discordlink.Commands.Discord.Unstuck;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Database.Storage;
import net.dirtcraft.discord.discordlink.DiscordLink;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.HashMap;

public class CommandManager {

    private final Storage storage;
    private final HashMap<String, DiscordCommand> commandMap;

    public CommandManager(DiscordLink main, Storage storage, HashMap<String, DiscordCommand> commandMap) {
        this.storage = storage;
        this.commandMap = commandMap;
        Sponge.getCommandManager().register(main, this.verify(), "verify", "link");
        Sponge.getCommandManager().register(main, this.unverify(), "unverify", "unlink");

        DiscordCommand list = DiscordCommand.builder()
                .setCommandExecutor(new PlayerList())
                .build();

        DiscordCommand stop = DiscordCommand.builder()
                .setCommandExecutor(new StopServer())
                .setRequiredRoles(Long.parseLong(PluginConfiguration.Roles.dirtyRoleID))
                .build();

        DiscordCommand unstuck = DiscordCommand.builder()
                .setCommandExecutor(new Unstuck())
                .setRequiredRoles(Long.parseLong(PluginConfiguration.Roles.verifiedRoleID))
                .build();

        register(list, "list");
        register(stop, "stop");
        register(unstuck, "unstuck", "spawn");
    }

    public void register(DiscordCommand command, String... alias){
        for (String name : alias) {
            commandMap.put(name, command);
        }
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
