// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.utility;

import org.spongepowered.api.text.serializer.TextSerializers;
import java.util.List;
import org.spongepowered.api.text.LiteralText;
import java.net.MalformedURLException;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.TextActions;
import java.net.URL;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextColors;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import java.util.Collection;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.entity.living.player.User;
import java.util.function.Function;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import net.dirtcraft.discordlink.DiscordLink;
import org.spongepowered.api.scheduler.Task;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dirtcraft.discordlink.users.MessageSourceImpl;

public class PlatformChat
{
    public static void discordToMCAsync(final MessageSourceImpl sender, final MessageReceivedEvent event) {
        Task.builder().async().execute(() -> discordToMc(sender, event.getMessage().getContentDisplay())).submit((Object)DiscordLink.get());
    }
    
    private static void discordToMc(final MessageSourceImpl sender, final String message) {
        try {
            final Optional<User> optUser = sender.getPlayerData().map((Function<? super PlatformUser, ? extends User>)PlatformUser::getOfflinePlayer);
            final String mcUsername = optUser.map((Function<? super User, ? extends String>)User::getName).orElse(null);
            final Text.Builder toBroadcast = Text.builder();
            String username;
            if (sender.isStaff() || mcUsername == null) {
                username = sender.getHighestRank().getStyle() + sender.getEffectiveName().replaceAll("[§&]([0-9a-fA-FrlonmkRLONMK])", "");
            }
            else {
                username = sender.getNameStyle() + mcUsername;
            }
            final String[] messageElements = PluginConfiguration.Format.discordToServer.replace("{username}", username).replace("»", sender.getChevron()).split("\\{message}");
            if (messageElements.length == 0) {
                return;
            }
            toBroadcast.append(new Text[] { formatNonContentElements(sender, mcUsername, messageElements[0]) });
            toBroadcast.append((Collection)formatContentElement(sender.isStaff(), message));
            if (messageElements.length > 1) {
                toBroadcast.append(new Text[] { formatNonContentElements(sender, mcUsername, messageElements[1]) });
            }
            Sponge.getServer().getBroadcastChannel().send(toBroadcast.build());
        }
        catch (Exception e) {
            Utility.dmExceptionAsync(e, 248056002274918400L);
            e.printStackTrace();
        }
    }
    
    private static Collection<Text> formatContentElement(final boolean isStaff, final String message) {
        final List<Text> text = new ArrayList<Text>();
        final StringBuilder sb = new StringBuilder();
        Text.Builder url;
        final List<LiteralText> list;
        final StringBuilder sb2;
        String element;
        Arrays.stream(message.split(" ")).forEach(s -> {
            if (s.toLowerCase().matches("((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)")) {
                s += " ";
                url = (Text.Builder)Text.builder(s);
                try {
                    url.color(TextColors.BLUE);
                    url.style(new TextStyle[] { (TextStyle)TextStyles.UNDERLINE, (TextStyle)TextStyles.ITALIC });
                    url.onClick((ClickAction)TextActions.openUrl(new URL(s)));
                    url.onHover((HoverAction)TextActions.showText((Text)Text.of("§aClick to open link")));
                }
                catch (MalformedURLException e) {
                    url.color(TextColors.BLUE);
                    url.style(new TextStyle[] { (TextStyle)TextStyles.UNDERLINE, (TextStyle)TextStyles.ITALIC });
                    url.onHover((HoverAction)TextActions.showText((Text)Text.of("§cMalformed URL")));
                }
                list.add(Text.of(sb2.toString()));
                list.add((LiteralText)url.build());
                sb2.setLength(0);
            }
            else {
                s += " ";
                element = (isStaff ? s.replaceAll("[§&]([0-9a-fA-FrlonmkRLONMK])", "§$1") : s.replaceAll("[§&]([0-9a-fA-FrlonmkRLONMK])", ""));
                sb2.append(element);
            }
            return;
        });
        if (sb.length() != 0) {
            sb.setLength(sb.length() - 1);
            text.add((Text)Text.of(sb.toString()));
        }
        return text;
    }
    
    private static Text formatNonContentElements(final MessageSourceImpl sender, final String mcUsername, final String element) {
        final Text.Builder text = Text.builder().append(new Text[] { TextSerializers.FORMATTING_CODE.deserialize(element) });
        final List<String> tooltip = new ArrayList<String>();
        tooltip.add("&5&nClick me&7 to join &cDirtCraft's &9Discord");
        if (mcUsername != null) {
            tooltip.add("&7MC Username&8: &6" + mcUsername);
        }
        tooltip.add("&7Discord Name&8: &6" + sender.getUser().getName() + "&8#&7" + sender.getUser().getDiscriminator());
        tooltip.add("§7Rank§8: §6" + sender.getHighestRank().getName());
        tooltip.add("§7Staff Member§8: §6" + (sender.isStaff() ? "§aYes" : "§cNo"));
        text.onHover((HoverAction)TextActions.showText(Utility.format(String.join("\n", tooltip))));
        try {
            text.onClick((ClickAction)TextActions.openUrl(new URL("https://dirtcraft.net")));
        }
        catch (MalformedURLException ex) {}
        return text.toText();
    }
}
