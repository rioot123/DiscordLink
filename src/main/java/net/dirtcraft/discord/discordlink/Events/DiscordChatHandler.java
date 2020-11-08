package net.dirtcraft.discord.discordlink.Events;

import net.dirtcraft.discord.discordlink.API.Action;
import net.dirtcraft.discord.discordlink.API.GameChats;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandManager;
import net.dirtcraft.discord.discordlink.Utility.Platform.PlatformUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static net.dirtcraft.discord.discordlink.Utility.Utility.logCommand;
import static net.dirtcraft.discord.discordlink.Utility.Utility.toConsole;

public class DiscordChatHandler extends ListenerAdapter {
    private final DiscordCommandManager commandManager = new DiscordCommandManager();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || hasAttachment(event)) return;
        CompletableFuture.runAsync(()->{
            try {
                final MessageSource sender = new MessageSource(event);
                if (event.getChannelType() == ChannelType.PRIVATE) processPrivateMessage(sender, event);
                else if (GameChats.getChat(event.getTextChannel()).isPresent()) processGuildMessage(sender, event);
            } catch (Throwable e){
                System.out.println("Exception while processing gamechat message!");
                e.printStackTrace();
                Utility.dmExceptionAsync(e, 248056002274918400L);
            }
        });
    }

    public void processGuildMessage(MessageSource sender, MessageReceivedEvent event) {
        final String rawMessage = event.getMessage().getContentRaw();
        final Action intent = Action.fromMessageRaw(rawMessage);

        if (intent.isBotCommand()) commandManager.process(sender, intent.getCommand(event));
        else if (intent.isConsole() || intent.isBungee()) {
            boolean executed = toConsole(intent.getCommand(event), sender, intent);
            if (executed && intent.isPrivate()) Utility.logCommand(sender, "__Executed Private Command__");
            event.getMessage().delete().queue(s->{},e->{});
        }
    }

    public void processPrivateMessage(MessageSource sender, MessageReceivedEvent event) {
        if (!PlatformUtils.isGameReady()) return;
        final Action intent = Action.PRIVATE_COMMAND;
        final String message = Action.filterConsolePrefixes(event.getMessage().getContentRaw());
        if (toConsole(message, sender, intent)) logCommand(sender, "__Executed Private Command via DM__");
    }

    private boolean hasAttachment(MessageReceivedEvent event) {
        for (Message.Attachment attachment : event.getMessage().getAttachments()) {
            if (attachment != null) return true;
        }
        return false;
    }

}
