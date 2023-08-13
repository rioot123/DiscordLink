// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.sponge.prefix;

import java.util.Map;
import java.util.Optional;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.discordlink.storage.Settings;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import net.dirtcraft.discordlink.commands.sources.ConsoleSource;
import java.util.concurrent.CompletableFuture;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import javax.annotation.Nonnull;
import org.spongepowered.api.command.CommandSource;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.spongepowered.api.command.spec.CommandExecutor;

public class Set implements CommandExecutor
{
    private final java.util.Set<String> forbidden;
    
    public Set() {
        this.forbidden = new HashSet<String>(Arrays.asList("staff", "helper", "mod", "moderator", "admin", "manager", "owner"));
    }
    
    @Nonnull
    public CommandResult execute(@Nonnull final CommandSource src, @Nonnull final CommandContext args) throws CommandException {
        User target;
        String prefix;
        CompletableFuture.runAsync(() -> {
            try {
                target = args.getOne("Target").orElseThrow(() -> new CommandException((Text)Text.of("§cYou must specify a target.")));
                if (target != src && !src.hasPermission("discordlink.prefix.others")) {
                    throw new CommandException((Text)Text.of("§cYou do not have permission to set other players prefixes."));
                }
                else {
                    prefix = this.getPrefixUsingArgs(src, target, args);
                    this.setPrefix(this.getSource(src), target, prefix);
                }
            }
            catch (CommandException e) {
                if (e.getText() != null) {
                    src.sendMessage(e.getText());
                }
                else {
                    src.sendMessage((Text)Text.of("§cAn error has occurred during execution."));
                }
            }
            return;
        });
        return CommandResult.success();
    }
    
    protected void setPrefix(@Nonnull final ConsoleSource src, final User target, final String prefix) {
        PermissionProvider.INSTANCE.setPlayerPrefix((org.spongepowered.api.command.source.ConsoleSource)src, target, prefix);
    }
    
    private String getPrefixUsingArgs(@Nonnull final CommandSource src, final User target, @Nonnull final CommandContext args) throws CommandException {
        final String title = args.getOne("Prefix").orElseThrow(() -> new CommandException((Text)Text.of("§cYou must specify a prefix.")));
        final Optional<Boolean> staff = args.getOne("s").filter(x -> src.hasPermission("discordlink.prefix.indicator"));
        final Optional<String> caratColor = args.getOne("ArrowColor").filter(x -> src.hasPermission("discordlink.prefix.arrow"));
        final Optional<String> optBracketColor = args.getOne("BracketColor").filter(x -> src.hasPermission("discordlink.prefix.brackets"));
        if (title.length() > 20 && !src.hasPermission("discordlink.prefix.long")) {
            throw new CommandException((Text)Text.of("§cThe prefix specified is too long."));
        }
        if (this.isNotAllowed(title) && !src.hasPermission("discordlink.roles.staff")) {
            throw new CommandException((Text)Text.of("§cThat prefix is not allowed."));
        }
        if (optBracketColor.isPresent() && this.isNonColor(optBracketColor.get())) {
            throw new CommandException((Text)Text.of("§cInvalid bracket color."));
        }
        if (caratColor.isPresent() && this.isNonColor(caratColor.get())) {
            throw new CommandException((Text)Text.of("§cInvalid arrow color."));
        }
        final String bracketColor = optBracketColor.orElse("&7");
        final String chevron = this.getChevron(target, caratColor.orElse("&a&l"));
        final Object o;
        final String rankPrefix = staff.isPresent() ? bracketColor : Settings.STAFF_PREFIXES.entrySet().stream().filter(p -> target.hasPermission((String)p.getKey())).findFirst().map(s -> String.format("%s[%s%s]", o, s.getValue(), o)).orElse(bracketColor);
        if (PluginConfiguration.Misc.omitChatArrow) {
            return String.format("%s[%s%s]&r", rankPrefix, title, bracketColor).replaceAll("\\?\"", "");
        }
        return String.format("%s %s[%s%s]&r", chevron, rankPrefix, title, bracketColor).replaceAll("\\?\"", "");
    }
    
    private String getChevron(final User user, final String colour) {
        final String carat = user.hasPermission("discordlink.roles.donor") ? "&l\u272f" : "&l»";
        return colour + carat;
    }
    
    private boolean isNonColor(final String input) {
        return !input.matches("(?i)([§&][0-9a-frlonm]){1,5}");
    }
    
    private boolean isNotAllowed(final String title) {
        final String raw = title.replaceAll("(?i)([§&][0-9a-frlonm])", "").toLowerCase();
        return this.forbidden.contains(raw);
    }
    
    private ConsoleSource getSource(final CommandSource source) {
        return new ConsoleSource() {
            public void sendMessage(@Nonnull final Text message) {
                source.sendMessage(message);
            }
        };
    }
}
