// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.sponge.prefix;

import org.spongepowered.api.service.permission.SubjectCollection;
import javax.annotation.Nonnull;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.service.pagination.PaginationList;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;
import java.util.Optional;
import net.dirtcraft.discordlink.utility.Pair;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;
import org.spongepowered.api.command.source.ConsoleSource;
import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import net.dirtcraft.discordlink.storage.Settings;
import java.util.Map;
import java.util.function.Function;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.spec.CommandExecutor;

public class Group implements CommandExecutor
{
    public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException {
        final User target = args.getOne("Target").orElseThrow(() -> new CommandException((Text)Text.of("§cYou must specify a target.")));
        final String group = args.getOne("Group").orElse(null);
        if (group == null) {
            this.showGroups(src, target);
            return CommandResult.success();
        }
        if (!target.hasPermission("group." + group)) {
            throw new CommandException((Text)Text.of("§cYou must specify a group you possess."));
        }
        final Subject subject = Sponge.getServiceManager().provide((Class)PermissionService.class).map(PermissionService::getGroupSubjects).flatMap(gs -> gs.getSubject(group)).orElseThrow(() -> new CommandException((Text)Text.of("§cInvalid Group.")));
        final String prefix = subject.getOption("prefix").filter(s -> !s.equalsIgnoreCase("")).orElseThrow(() -> new CommandException((Text)Text.of("§cGroup specified has no prefix.")));
        final Map.Entry<String, String> indicatorSet = Settings.STAFF_PREFIXES.entrySet().stream().filter(p -> target.hasPermission((String)p.getKey())).findFirst().orElse(null);
        if (indicatorSet != null && !subject.hasPermission((String)indicatorSet.getKey())) {
            String indicator = prefix.replaceAll("(?i)^.*?(([§&][0-9a-frlonm])+) *\\[.*", "$1");
            if (indicator.equalsIgnoreCase(prefix)) {
                indicator = "&f";
            }
            indicator = String.format("%s[%s%s]", indicator, indicatorSet.getValue(), indicator);
            final List<String> bits = new ArrayList<String>(Arrays.asList(prefix.split(" ")));
            final String carat = bits.isEmpty() ? "" : (bits.remove(0) + " ");
            final String rest = String.join(" ", bits);
            PermissionProvider.INSTANCE.setPlayerPrefix((ConsoleSource)this.getSource(src), target, carat + indicator + rest);
        }
        else {
            PermissionProvider.INSTANCE.setPlayerPrefix((ConsoleSource)this.getSource(src), target, prefix);
        }
        return CommandResult.success();
    }
    
    private void showGroups(final CommandSource src, final User target) {
        final AtomicBoolean flip = new AtomicBoolean(false);
        final AtomicBoolean atomicBoolean;
        final boolean flipper;
        final char color;
        final Text.Builder builder;
        final List<Text> response = ((PermissionService)Sponge.getServiceManager().provideUnchecked((Class)PermissionService.class)).getGroupSubjects().getLoadedSubjects().stream().map(g -> new Pair(g.getIdentifier(), g.getOption("prefix"))).filter(g -> g.getValue().isPresent()).filter(g -> target.hasPermission("group." + g.getKey())).map(g -> {
            flipper = atomicBoolean.get();
            atomicBoolean.set(!flipper);
            color = (flipper ? '3' : 'b');
            builder = TextSerializers.FORMATTING_CODE.deserialize("&" + color + g.getKey()).toBuilder();
            builder.onHover((HoverAction)TextActions.showText(TextSerializers.FORMATTING_CODE.deserialize((String)((Optional)g.getValue()).get())));
            builder.onClick((ClickAction)TextActions.runCommand("/prefix group " + target.getName() + " " + g.getKey()));
            return builder.build();
        }).collect((Collector<? super Object, ?, List<Text>>)Collectors.toList());
        PaginationList.builder().contents((Iterable)response).title(TextSerializers.FORMATTING_CODE.deserialize("&eAvailable Groups")).padding(TextSerializers.FORMATTING_CODE.deserialize("&6=")).linesPerPage(16).sendTo((MessageReceiver)src);
    }
    
    private net.dirtcraft.discordlink.commands.sources.ConsoleSource getSource(final CommandSource source) {
        return new net.dirtcraft.discordlink.commands.sources.ConsoleSource() {
            public void sendMessage(@Nonnull final Text message) {
                source.sendMessage(message);
            }
        };
    }
}
