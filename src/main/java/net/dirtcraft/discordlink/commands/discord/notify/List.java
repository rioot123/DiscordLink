// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord.notify;

import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.Member;
import java.util.Optional;
import java.util.function.Function;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class List implements DiscordCommandExecutor
{
    public void execute(final MessageSource source, final String command, final java.util.List<String> args) throws DiscordCommandException {
        try {
            final String staff = PluginConfiguration.Notifier.notify.stream().map((Function<? super Object, ?>)Utility::getMemberById).filter(Optional::isPresent).map((Function<? super Object, ?>)Optional::get).map((Function<? super Object, ?>)Member::getEffectiveName).map(s -> " **-** " + s).collect((Collector<? super Object, ?, String>)Collectors.joining("\n"));
            source.sendCommandResponse("People to notify:", staff, 30);
        }
        catch (Exception e) {
            throw new DiscordCommandException(e.getMessage());
        }
    }
}
