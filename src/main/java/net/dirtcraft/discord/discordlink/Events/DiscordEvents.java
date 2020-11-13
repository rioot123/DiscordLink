package net.dirtcraft.discord.discordlink.Events;

import net.dirtcraft.discord.discordlink.API.Action;
import net.dirtcraft.discord.discordlink.API.DiscordChannel;
import net.dirtcraft.discord.discordlink.API.GameChats;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandManager;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformChat;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

import static net.dirtcraft.discord.discordlink.Utility.Utility.logCommand;
import static net.dirtcraft.discord.discordlink.Utility.Utility.toConsole;


public class DiscordEvents extends ListenerAdapter {

    private final DiscordCommandManager commandManager = new DiscordCommandManager();

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        boolean gamechat = GameChats.isGamechat(event.getChannel());
        boolean privateDm = event.getChannelType() == ChannelType.PRIVATE;
        if (!gamechat && !privateDm || event.getAuthor().isBot() || hasAttachment(event)) return;
        CompletableFuture.runAsync(()->{
            try {
                final DiscordChannel chat = new DiscordChannel(event.getChannel().getIdLong());
                final MessageSource sender = new MessageSource(event);
                if (privateDm) processPrivateMessage(chat, sender, event);
                else processGuildMessage(chat, sender, event);
            } catch (Exception e){
                DiscordLink.getInstance().getLogger().warn("Exception while processing gamechat message!", e);
                Utility.dmExceptionAsync(e, 248056002274918400L);
            }
        });
    }

    public void processGuildMessage(DiscordChannel chat, MessageSource sender, MessageReceivedEvent event) {
        final String rawMessage = event.getMessage().getContentRaw();
        final Action intent = Action.fromMessageRaw(rawMessage);

        if (intent.isBotCommand()) commandManager.process(sender, intent.getCommand(event));
        else if (PlatformUtils.isGameReady() && intent.isChat()) PlatformChat.discordToMCAsync(sender, event);
        else if (PlatformUtils.isGameReady() && intent.isConsole()) {
            boolean executed = toConsole(chat, intent.getCommand(event), sender, intent);
            if (executed && intent.isPrivate()) Utility.logCommand(sender, "__Executed Private Command__");
            event.getMessage().delete().queue(s->{},e->{});
        }
    }

    public void processPrivateMessage(DiscordChannel chat, MessageSource sender, MessageReceivedEvent event) {
        if (!PlatformUtils.isGameReady()) return;
        final Action intent = Action.fromMessageRaw(event.getMessage().getContentRaw()) == Action.DISCORD_COMMAND? Action.DISCORD_COMMAND: Action.PRIVATE_COMMAND;
        final String message = Action.filterConsolePrefixes(event.getMessage().getContentRaw());
        if (intent == Action.DISCORD_COMMAND) commandManager.process(sender, intent.getCommand(event));
        else if (toConsole(chat, message, sender, intent)) logCommand(sender, "__Executed Private Command via DM__");
    }

    private boolean hasAttachment(MessageReceivedEvent event) {
        for (net.dv8tion.jda.api.entities.Message.Attachment attachment : event.getMessage().getAttachments()) {
            if (attachment != null) return true;
        }
        return false;
    }

}
