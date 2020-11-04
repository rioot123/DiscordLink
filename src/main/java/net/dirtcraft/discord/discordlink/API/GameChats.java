package net.dirtcraft.discord.discordlink.API;

import litebans.api.Events;
import net.dirtcraft.discord.discordlink.Commands.Sources.ResponseScheduler;
import net.dirtcraft.discord.discordlink.Commands.Sources.ScheduledSender;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.dirtcraft.discord.discordlink.Storage.PluginConfiguration.Main.GAMECHAT_CATEGORY_ID;
import static net.dirtcraft.discord.discordlink.Storage.PluginConfiguration.Main.SANCTION_CHANNEL_ID;

public class GameChats {
    public static final JDA jda = DiscordLink.getJDA();
    public static final long category = GAMECHAT_CATEGORY_ID;
    public static final long guild = getCategory().getGuild().getIdLong();
    private static final GameChat sanctions = initSanctions();
    private static final Map<Long, GameChat> channels = getCategory().getChannels().stream()
            .filter(TextChannel.class::isInstance)
            .map(ISnowflake::getIdLong)
            .collect(Collectors.toMap(c->c, GameChat::new));

    static {
        if (sanctions != null) {
            channels.put(sanctions.channel, sanctions);
            ScheduledSender sender = ScheduledSender.getSender(sanctions::sendMessage, 1996, false);
            Events.get().register(new Events.Listener() {
                @Override
                public void broadcastSent(@NotNull String message, @Nullable String type) {
                    ResponseScheduler.submit(sender, "``" + message + "``");
                }
            });
        }
    }

    public static Category getCategory(){
        return jda.getCategoryById(category);
    }

    public static Guild getGuild(){
        return jda.getGuildById(guild);
    }

    public static Optional<GameChat> getChat(TextChannel channel){
        return Optional.ofNullable(channels.get(channel.getIdLong()));
    }

    public static Optional<GameChat> getChat(MessageReceivedEvent event){
        return getChat(event.getTextChannel());
    }

    private static GameChat initSanctions(){
        TextChannel channel = jda.getTextChannelById(SANCTION_CHANNEL_ID);
        return channel == null ? null : new GameChat(SANCTION_CHANNEL_ID);
    }

    public static Optional<GameChat> getChannel(MessageChannel channel){
        return Optional.ofNullable(channels.get(channel.getIdLong()));
    }

}
