// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.sponge.prefix;

import javax.annotation.Nonnull;
import java.util.Optional;
import org.spongepowered.api.command.source.ConsoleSource;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.spec.CommandExecutor;

public class Toggle implements CommandExecutor
{
    public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException {
        final User target = args.getOne("Target").orElseThrow(() -> new CommandException((Text)Text.of("§cYou must specify a target.")));
        if (target != src && !src.hasPermission("discordlink.prefix.others")) {
            throw new CommandException((Text)Text.of("§cYou do not have permission to set other players prefixes."));
        }
        if (!target.hasPermission("discordlink.roles.donor")) {
            throw new CommandException((Text)Text.of("Target is not a donor"));
        }
        final Optional<String> prefix = (Optional<String>)target.getOption("prefix");
        final User user;
        prefix.ifPresent(pre -> {
            if (pre.matches("^(?i)([§&][0-9a-frlonm]){1,5}».*$")) {
                pre = pre.replaceFirst("»", "\u272f");
            }
            else {
                pre = pre.replaceFirst("\u272f", "»");
            }
            PermissionProvider.INSTANCE.setPlayerPrefix((ConsoleSource)this.getSource(src), user, pre);
            return;
        });
        return CommandResult.success();
    }
    
    private net.dirtcraft.discordlink.commands.sources.ConsoleSource getSource(final CommandSource source) {
        return new net.dirtcraft.discordlink.commands.sources.ConsoleSource() {
            public void sendMessage(@Nonnull final Text message) {
                source.sendMessage(message);
            }
        };
    }
}
