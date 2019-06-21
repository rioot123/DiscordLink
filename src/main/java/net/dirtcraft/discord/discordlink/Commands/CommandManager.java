package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.Database.Storage;
import net.dirtcraft.discord.discordlink.DiscordLink;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandManager {

    private final Storage storage;

    public CommandManager(DiscordLink main, Storage storage) {
        this.storage = storage;
        Sponge.getCommandManager().register(main, this.verify(), "verify", "link");
        Sponge.getCommandManager().register(main, this.unverify(), "unverify", "unlink");
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
