package net.dirtcraft.discordlink.commands.sponge.prefix;

import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import net.dirtcraft.discordlink.commands.sources.ConsoleSource;
import net.dirtcraft.discordlink.storage.Permission;
import net.dirtcraft.discordlink.storage.Settings;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Test extends Set {
    @Override
    protected void setPrefix(@Nonnull ConsoleSource src, User target, String prefix) {
        Text name = NucleusAPI.getNicknameService().flatMap(u->u.getNickname(target)).orElse(Text.of(target.getName()));
        src.sendMessage(Text.of(TextSerializers.FORMATTING_CODE.deserialize(prefix), Text.of(" "), name));
    }
}
