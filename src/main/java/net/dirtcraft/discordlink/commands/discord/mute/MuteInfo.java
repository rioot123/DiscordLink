// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord.mute;

import java.time.Instant;
import net.dirtcraft.discordlink.channels.ChannelManagerImpl;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.discordlink.users.discord.WrappedMember;
import net.dv8tion.jda.api.requests.RestAction;
import java.util.function.Supplier;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dirtcraft.discordlink.users.UserManagerImpl;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.storage.tables.Mutes;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import java.util.function.Function;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.discordlink.DiscordLink;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class MuteInfo implements DiscordCommandExecutor
{
    public void execute(final MessageSource source, final String command, final List<String> args) throws DiscordCommandException {
        final DiscordLink discordLink = DiscordLink.get();
        final Database database = discordLink.getStorage();
        final UserManagerImpl userManager = discordLink.getUserManager();
        final DiscordMember target = this.removeIfPresent((List)args, (Function)userManager::getMember).orElseThrow(() -> new DiscordCommandException("Discord user not specified."));
        final Mutes.MuteData data = database.hasActiveMute(target.getIdLong()).orElseThrow(() -> new DiscordCommandException("The player does not have an active mute!"));
        source.sendCommandResponse(getInfo(data));
    }
    
    public static MessageEmbed getInfo(final Mutes.MuteData data) {
        final ChannelManagerImpl channelManager = DiscordLink.get().getChannelManager();
        final UserManagerImpl userManager = DiscordLink.get().getUserManager();
        String name = or("Unknown", () -> data.getSubmitter().map(id -> channelManager.getGuild().retrieveMemberById((long)id)).map((Function<? super Object, ?>)RestAction::complete).map((Function<? super Object, ?>)userManager::getMember).map((Function<? super Object, ?>)WrappedMember::getEffectiveName), () -> data.getSubmitterUUID().flatMap((Function<? super UUID, ? extends Optional<?>>)PlatformProvider::getPlayerOffline).flatMap((Function<? super Object, ? extends Optional<?>>)PlatformUser::getNameIfPresent));
        if (!data.getSubmitter().isPresent()) {
            name += " (via MC/Proxy)";
        }
        else {
            name += " (via Discord)";
        }
        return Utility.embedBuilder().addField("Punisher", name, true).addField("Submitted", data.getSubmitted().toLocalDateTime().format(DateTimeFormatter.ofPattern("d MMM uuuu")), true).addField("Expires", getDuration(data.getExpires().orElse(null)), true).addField("Reason", data.getReason(), false).build();
    }
    
    private static String getDuration(final Timestamp date) {
        final Timestamp now = Timestamp.from(Instant.now());
        if (date == null) {
            return "Never.";
        }
        if (!date.after(now)) {
            return "Has already expired";
        }
        final long msRemaining = date.getTime() - now.getTime();
        long seconds = msRemaining / 1000L;
        long minutes = seconds / 60L;
        if (minutes == 0L) {
            return seconds + " seconds.";
        }
        long hours = minutes / 60L;
        seconds %= 60L;
        if (hours == 0L) {
            return minutes + " minutes, " + seconds + " seconds.";
        }
        final long days = hours / 24L;
        minutes %= 60L;
        if (days == 0L) {
            return hours + " hours, " + minutes + " minutes.";
        }
        hours %= 24L;
        return days + " days, " + hours + " hours.";
    }
    
    @SafeVarargs
    private static <T> Optional<T> or(final Supplier<Optional<T>>... optionals) {
        for (final Supplier<Optional<T>> supplier : optionals) {
            final Optional<T> optional = supplier.get();
            if (optional.isPresent()) {
                return optional;
            }
        }
        return Optional.empty();
    }
    
    @SafeVarargs
    private static <T> T or(final T t, final Supplier<Optional<T>>... optionals) {
        for (final Supplier<Optional<T>> supplier : optionals) {
            final Optional<T> optional = supplier.get();
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        return t;
    }
}
