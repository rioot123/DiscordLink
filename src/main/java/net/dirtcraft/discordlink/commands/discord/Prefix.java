// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord;

import java.util.ListIterator;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.Sponge;
import java.util.Optional;
import java.util.Map;
import net.dirtcraft.discordlink.storage.Settings;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import org.spongepowered.api.service.permission.Subject;
import java.util.function.Function;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import org.spongepowered.api.entity.living.player.User;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class Prefix implements DiscordCommandExecutor
{
    public void execute(final MessageSource source, final String command, final List<String> args) throws DiscordCommandException {
        final User target = source.getPlayerData().map(PlatformUser::getOfflinePlayer).map(u -> this.getTarget((Subject)u, args).orElse(u)).orElseThrow(() -> new DiscordCommandException("No player present for Discord User."));
        final String arrow = this.getChevron(target, args);
        final String color = this.getColor(args);
        if (args.isEmpty()) {
            throw new DiscordCommandException("You must specify a prefix");
        }
        if (args.size() == 1 && args.get(0).equalsIgnoreCase("none")) {
            PermissionProvider.INSTANCE.clearPlayerPrefix(source, target);
        }
        final Object o;
        final String rankPrefix = Settings.STAFF_PREFIXES.entrySet().stream().filter(p -> target.hasPermission((String)p.getKey())).map((Function<? super Object, ?>)Map.Entry::getValue).findFirst().map(s -> String.format("%s[%s%s]", o, s, o)).orElse(color);
        final String title = String.join(" ", args);
        final String prefix = String.format("%s %s[%s%s]&r", arrow, rankPrefix, title, color).replaceAll("\\?\"", "");
        PermissionProvider.INSTANCE.setPlayerPrefix(source, target, prefix);
    }
    
    private Optional<User> getTarget(final Subject source, final List<String> args) {
        if (args.isEmpty() || !args.get(0).matches("(?i)^[a-z0-9_]{3,16}$")) {
            return Optional.empty();
        }
        final Optional<User> target = Sponge.getServiceManager().provide((Class)UserStorageService.class).flatMap(uss -> uss.get((String)args.get(0)));
        final String s;
        target.ifPresent(t -> s = args.remove(0));
        return source.hasPermission("discordlink.prefix.others") ? target : Optional.empty();
    }
    
    private String getChevron(final User user, final List<String> args) {
        final String carat = (!this.ignoreDonor(args) && user.hasPermission("discordlink.roles.donor")) ? "&l\u272f" : "&l»";
        final ListIterator<String> argsIterator = args.listIterator();
        String chevronColour = "&a";
        while (argsIterator.hasNext()) {
            final String arg = argsIterator.next();
            if (arg.matches("(?i)--?a(rrow)?=([§&].)*$")) {
                chevronColour = arg.replaceAll("[^=]+=", "");
                argsIterator.remove();
                break;
            }
        }
        return chevronColour + carat;
    }
    
    private String getColor(final List<String> args) {
        String defaultColour = "&7";
        final ListIterator<String> argsIterator = args.listIterator();
        while (argsIterator.hasNext()) {
            final String arg = argsIterator.next();
            if (arg.matches("(?i)^--?c(olou?r)?=([§&].)*$")) {
                defaultColour = arg.replaceAll("[^=]+=", "");
                argsIterator.remove();
                break;
            }
        }
        return defaultColour;
    }
    
    private boolean ignoreDonor(final List<String> args) {
        final ListIterator<String> argsIterator = args.listIterator();
        while (argsIterator.hasNext()) {
            final String arg = argsIterator.next();
            if (arg.matches("(?i)^--?i(gnore)?$")) {
                argsIterator.remove();
                return true;
            }
        }
        return false;
    }
}
