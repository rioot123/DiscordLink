package net.dirtcraft.discord.discordlink.Commands.Sponge;

import net.dirtcraft.discord.discordlink.Commands.Sources.ConsoleSource;
import net.dirtcraft.discord.discordlink.Storage.Permission;
import net.dirtcraft.discord.discordlink.Storage.Settings;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Permission.PermissionUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class Prefix implements CommandExecutor {
    private final Set<String> forbidden = new HashSet<>(Arrays.asList("staff", "helper", "mod", "moderator", "admin", "manager", "owner"));

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) throws CommandException {
        CompletableFuture.runAsync(()->{
            final User target = args.<User>getOne("Target").orElse(null);
            final String title = args.<String>getOne("Prefix").orElse(null);
            final String caratColor = args.<String>getOne("ArrowColor").orElse("&a&l");
            final String bracketColor = args.<String>getOne("BracketColor").orElse("&7");
            final boolean ignoreDonor = args.<Boolean>getOne("i").orElse(false);
            if (title == null) {
                src.sendMessage(Text.of("§cYou must specify a prefix."));
                return;
            } else if (title.equalsIgnoreCase("none")){
                PermissionUtils.INSTANCE.clearPlayerPrefix(getSource(src), target);
                src.sendMessage(Text.of("§cPrefix cleared."));
                return;
            } else if (target == null) {
                src.sendMessage(Text.of("§cYou must specify a target."));
                return;
            } else if (target != src && !src.hasPermission(Permission.PREFIX_OTHERS)) {
                src.sendMessage(Text.of("§cYou do not have permission to set other players prefixes."));
                return;
            } else if (title.length() > 20 && !src.hasPermission(Permission.PREFIX_LONG)){
                src.sendMessage(Text.of("§cThe prefix specified is too long."));
                return;
            } else if (isNotAllowed(title) && src.hasPermission(Permission.ROLES_STAFF)){
                src.sendMessage(Text.of("§cThat prefix is not allowed."));
                return;
            } else if (!caratColor.equalsIgnoreCase("&a&l") && !src.hasPermission(Permission.PREFIX_ARROW)){
                src.sendMessage(Text.of("§cYou do not have permission to change the arrow colour."));
                return;
            } else if (!isValidColor(caratColor)){
                src.sendMessage(Text.of("§cInvalid arrow color."));
                return;
            } else if (!bracketColor.equalsIgnoreCase("&7") && !src.hasPermission(Permission.PREFIX_BRACKETS)){
                src.sendMessage(Text.of("§cYou do not have permission to change the bracket colour."));
                return;
            } else if (!isValidColor(bracketColor)){
                src.sendMessage(Text.of("§cInvalid bracket color."));
                return;
            }

            String chevron = getChevron(target, caratColor, ignoreDonor);
            String rankPrefix = Settings.STAFF_PREFIX.entrySet().stream()
                    .filter(p->target.hasPermission(p.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .map(s->String.format("%s[%s%s]", bracketColor, s, bracketColor))
                    .orElse(bracketColor);
            String prefix = String.format("%s %s[%s%s]&r", chevron, rankPrefix, title, bracketColor)
                    .replaceAll("\\?\"", "");

            PermissionUtils.INSTANCE.setPlayerPrefix(getSource(src), target, prefix);
        });

        return CommandResult.success();
    }

    private String getChevron(User user, String colour, boolean ignoreDonor){
        final String carat = !ignoreDonor && user.hasPermission(Permission.ROLES_DONOR) ? "&l✯" : "&l»";
        return colour + carat;
    }

    private boolean isValidColor(String input){
        return input.matches("(?i)([§&][0-9a-frlonm]){1,5}");
    }

    private boolean isNotAllowed(String title){
        String raw = title.replaceAll("(?i)([§&][0-9a-frlonm])", "").toLowerCase();
        return forbidden.contains(raw);
    }

    private ConsoleSource getSource(CommandSource source){
        return new ConsoleSource(){
            @Override
            public void sendMessage(@Nonnull Text message) {
                source.sendMessage(message);
            }
        };
    }
}