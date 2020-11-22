package net.dirtcraft.discord.discordlink.Utility.Compatability.Platform;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static net.dirtcraft.discord.discordlink.Utility.Utility.*;

public class PlatformChat {
    public static final PlatformChat INSTANCE = new PlatformChat();
    final ServerInfo lobby;
    public PlatformChat(){
        lobby = ProxyServer.getInstance().getServerInfo("lobby");
    }

    public void discordToMCAsync(MessageSource sender, String message){
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

        lobby.getPlayers().forEach(p->p.sendMessage(toBroadcast));

        final StringBuilder plain = new StringBuilder();
        Arrays.stream(toBroadcast).forEach(t->plain.append(Utility.removeColourCodes(t.toLegacyText())));
        System.out.println(plain);
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
}
