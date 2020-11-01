package net.dirtcraft.discord.discordlink.Events;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.dirtcraft.discord.discordlink.API.Action;
import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandManager;
import net.dirtcraft.discord.discordlink.Compatability.PlatformUser;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Compatability.PlatformUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static net.dirtcraft.discord.discordlink.Utility.Utility.*;

public class DiscordEvents extends ListenerAdapter {
    private final DiscordCommandManager commandManager = new DiscordCommandManager();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        CompletableFuture.runAsync(()->{
            try {
                if (event.getChannelType() == ChannelType.PRIVATE) processPrivateMessage(event);
                else if (GameChat.isGamechat(event.getChannel())) processGuildMessage(event);
            } catch (Exception e){
                System.out.println("Exception while processing gamechat message!");
                e.printStackTrace();
                Utility.dmExceptionAsync(e, 248056002274918400L);
            }
        });
    }

    public void processGuildMessage(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || hasAttachment(event)) return;
        final MessageSource sender = new MessageSource(event);
        final String message = event.getMessage().getContentDisplay();
        final String rawMessage = event.getMessage().getContentRaw();
        final Action intent = Action.fromMessageRaw(rawMessage);

        if (intent.isChat() && PlatformUtils.isGameReady()) discordToMCAsync(sender, message);
        else if (intent.isBotCommand()) commandManager.process(sender, intent.getCommand(event));
        else if (intent.isConsole() && PlatformUtils.isGameReady()) {
            boolean executed = toConsole(intent.getCommand(event), sender, intent);
            if (executed && intent.isPrivate()) Utility.logCommand(sender, "__Executed Private Command__");
            event.getMessage().delete().queue(s->{},e->{});
        }
    }

    public void processPrivateMessage(MessageReceivedEvent event) {
        if (!PlatformUtils.isGameReady() || event.getAuthor().isBot()) return;
        final MessageSource sender = new MessageSource(event);
        final Action intent = Action.PRIVATE_COMMAND;
        final String message = Action.filterConsolePrefixes(event.getMessage().getContentRaw());
        if (toConsole(message, sender, intent)) logCommand(sender, "__Executed Private Command via DM__");
    }

    private void discordToMCAsync(MessageSource sender, String message){
        final Optional<PlatformUser> optSpigotUser = sender.getPlayerData();
        final String mcUsername = optSpigotUser.flatMap(PlatformUser::getName).orElse(null);
        final String username;

        if (sender.isStaff() || mcUsername == null) username = sender.getHighestRank().getStyle() + sender.getEffectiveName().replaceAll(STRIP_CODE_REGEX, "");
        else username = sender.getNameStyle() + mcUsername;

        final String[] elements = formatColourCodes(PluginConfiguration.Format.discordToServer)
                .replace("{username}", username)
                .replace("»", sender.getChevron())
                .split("\\{message}");

        if (elements.length < 1) return;
        ArrayList<BaseComponent> result = new ArrayList<>();
        result.add(new TextComponent(formatNonMessageElements(elements[0], sender, mcUsername)));
        formatMessageElements(message, sender, result);
        if (elements.length == 2) result.add(new TextComponent(formatNonMessageElements(elements[1], sender, mcUsername)));

        BaseComponent[] toBroadcast = new BaseComponent[result.size()];
        for (int i = 0; i < toBroadcast.length; i++) toBroadcast[i] = result.get(i);

        if (!sender.isStaff() && optSpigotUser.isPresent()) {
            sendMessageToPlayer(optSpigotUser.map(PlatformUser::getUser).get(), toBroadcast);
        } else {
            Bukkit.getOnlinePlayers().forEach(p->p.spigot().sendMessage(toBroadcast));
        }

        final StringBuilder plain = new StringBuilder();
        Arrays.stream(toBroadcast).forEach(t->plain.append(Utility.removeColourCodes(t.toLegacyText())));
        System.out.println(plain);
    }

    private void sendMessageToPlayer(OfflinePlayer optSpigotUser, BaseComponent[] toBroadcast) {
        final Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

        if (ess == null) {
            Bukkit.getOnlinePlayers().forEach(p->p.spigot().sendMessage(toBroadcast));
        } else {
            final User senderUser = ess.getUser(optSpigotUser.getUniqueId());

            if (senderUser == null) {
                Bukkit.getOnlinePlayers().forEach(p->p.spigot().sendMessage(toBroadcast));
                return;
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                final User playerUser = ess.getUser(player.getUniqueId());

                if (playerUser != null)  {
                    if (playerUser.isIgnoredPlayer(senderUser)) continue;
                }

                player.spigot().sendMessage(toBroadcast);
            }
        }
    }

    private void formatMessageElements(String message, MessageSource sender, List<BaseComponent> output){
        final StringBuilder sb = new StringBuilder();
        Arrays.stream(message.split(" ")).forEach(s->{
            if (s.toLowerCase().matches(DETECT_URL_REGEX)){
                BaseComponent toBroadcast = new TextComponent(s);
                BaseComponent[] urlHover = new BaseComponent[1];
                urlHover[0] = new TextComponent("Click to open url!");
                toBroadcast.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, s));
                toBroadcast.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, urlHover));
                toBroadcast.setColor(ChatColor.BLUE);
                toBroadcast.setItalic(true);
                toBroadcast.setUnderlined(true);
                output.add(new TextComponent(sb.toString()));
                output.add(toBroadcast);
                sb.setLength(0);
                sb.append(" ");
            } else {
                if (sender.isStaff()) sb.append(formatColourCodes(s)).append(" ");
                else sb.append(s.replaceAll(STRIP_CODE_REGEX, "")).append(" ");
            }
        });
        if (sb.length() != 0) {
            sb.setLength(sb.length()-1);
            output.add(new TextComponent(sb.toString()));
        }
    }

    private TextComponent formatNonMessageElements(String message, MessageSource sender, String mcUsername){
        TextComponent formattedMessage = new TextComponent(message);

        ArrayList<String> tooltip = new ArrayList<>();
        tooltip.add("§5§nClick me§7 to join §cDirtCraft's §9Discord");
        if (mcUsername != null) tooltip.add("\n§7MC Username§8: §6" + mcUsername);
        tooltip.add("\n§7Discord Name§8: §6" + sender.getUser().getName() + "§8#§7" + sender.getUser().getDiscriminator());
        tooltip.add("\n§7Rank§8: §6" + sender.getHighestRank().getName());
        tooltip.add("\n§7Staff Member§8: §6" + (sender.isStaff() ? "§aYes" : "§cNo"));

        final BaseComponent[] hoverElement = tooltip.stream().map(TextComponent::new).toArray(BaseComponent[]::new);

        formattedMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverElement));
        formattedMessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, PluginConfiguration.Main.DISCORD_INVITE));
        return formattedMessage;
    }

    private boolean hasAttachment(MessageReceivedEvent event) {
        boolean hasAttachment = false;
        for (Message.Attachment attachment : event.getMessage().getAttachments()) {
            if (attachment != null) {
                hasAttachment = true;
            }
        }
        return hasAttachment;
    }

}
