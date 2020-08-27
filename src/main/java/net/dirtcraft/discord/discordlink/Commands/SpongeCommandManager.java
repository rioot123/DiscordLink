package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.Commands.Sponge.UnVerify;
import net.dirtcraft.discord.discordlink.Commands.Sponge.Verify;
import net.dirtcraft.discord.discordlink.Database.Storage;
import net.dirtcraft.discord.discordlink.DiscordLink;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class SpongeCommandManager {
    public SpongeCommandManager(DiscordLink discordLink, Storage storage){
        CommandSpec verify = CommandSpec.builder()
                .description(Text.of("Verifies your Discord account"))
                .executor(new Verify(storage))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("code"))))
                .build();

        CommandSpec unverify = CommandSpec.builder()
                .description(Text.of("Unverifies your Discord account"))
                .executor(new UnVerify(storage))
                .build();

        Sponge.getCommandManager().register(discordLink, verify, "verify", "link");
        Sponge.getCommandManager().register(discordLink, unverify, "unverify", "unlink");
    }
}
