package net.dirtcraft.discord.discordlink.Events;

import net.dirtcraft.discord.discordlink.Utility;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.serializer.TextSerializers;

public class NormalChat {

    @Listener
    public void onChat(MessageChannelEvent.Chat event, @Root Object cause) {
        if (!(cause instanceof Player)) return;
        Player player = (Player) cause;

        String prefix = TextSerializers.FORMATTING_CODE.stripCodes(player.getOption("prefix").orElse(""));
        String username = player.getName();
        String message = TextSerializers.FORMATTING_CODE.stripCodes(event.getRawMessage().toPlain())
                .replace("@everyone", "")
                .replace("@here", "")
                .replaceAll("<@\\d+>", "");

        Utility.chatToDiscord(prefix, username, message);
    }

}
