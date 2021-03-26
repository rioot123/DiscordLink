package net.dirtcraft.discordlink.forge.platform;

import net.dirtcraft.discordlink.forge.DiscordLink;
import net.dirtcraft.discordlink.common.storage.PluginConfiguration;
import net.dirtcraft.discordlink.common.users.MessageSourceImpl;
import net.dirtcraft.discordlink.api.users.platform.PlatformUser;
import net.dirtcraft.discordlink.common.utility.Utility;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static net.dirtcraft.discordlink.common.utility.Utility.*;

public class PlatformChat {

    public static void discordToMCAsync(MessageSourceImpl sender, MessageReceivedEvent event){
        Task.builder()
                .async()
                .execute(() -> discordToMc(sender, event.getMessage().getContentDisplay()))
                .submit(DiscordLink.get());
    }

    private static void discordToMc(MessageSourceImpl sender, String message){
        try {
            final Optional<User> optUser = sender.getPlayerData().map(PlatformUser::getOfflinePlayer);
            final String mcUsername = optUser.map(User::getName).orElse(null);
            final Text.Builder toBroadcast = Text.builder();
            final String username;

            if (sender.isStaff() || mcUsername == null) username = sender.getHighestRank().getStyle() + sender.getEffectiveName().replaceAll(STRIP_CODE_REGEX, "");
            else username = sender.getNameStyle() + mcUsername;


            String[] messageElements = PluginConfiguration.Format.discordToServer
                    .replace("{username}", username)
                    .replace("»", sender.getChevron())
                    .split("\\{message}");

            if (messageElements.length == 0) return;
            toBroadcast.append(formatNonContentElements(sender, mcUsername, messageElements[0]));
            toBroadcast.append(formatContentElement(sender.isStaff(), message));
            if (messageElements.length > 1)
                toBroadcast.append(formatNonContentElements(sender, mcUsername, messageElements[1]));

            Sponge.getServer().getBroadcastChannel().send(toBroadcast.build());
        } catch (Exception e){
            Utility.dmExceptionAsync(e, 248056002274918400L);
            e.printStackTrace();
        }
    }

    private static Collection<Text> formatContentElement(boolean isStaff, String message){
        final List<Text> text = new ArrayList<>();
        final StringBuilder sb = new StringBuilder();
        Arrays.stream(message.split(" ")).forEach(s->{
            if (s.toLowerCase().matches(URL_DETECT_REGEX)){
                s+=" ";
                Text.Builder url = Text.builder(s);
                try{
                    url.color(TextColors.BLUE);
                    url.style(TextStyles.UNDERLINE, TextStyles.ITALIC);
                    url.onClick(TextActions.openUrl(new URL(s)));
                    url.onHover(TextActions.showText(Text.of("§aClick to open link")));
                } catch (MalformedURLException e){
                    url.color(TextColors.BLUE);
                    url.style(TextStyles.UNDERLINE, TextStyles.ITALIC);
                    url.onHover(TextActions.showText(Text.of("§cMalformed URL")));
                }
                text.add(Text.of(sb.toString()));
                text.add(url.build());
                sb.setLength(0);
            } else {
                s+=" ";
                String element = isStaff? s.replaceAll(STRIP_CODE_REGEX, "§$1") : s.replaceAll(STRIP_CODE_REGEX,"");
                sb.append(element);
            }
        });
        if (sb.length() != 0){
            sb.setLength(sb.length()-1);
            text.add(Text.of(sb.toString()));
        }
        return text;
    }

    private static Text formatNonContentElements(MessageSourceImpl sender, String mcUsername, String element){
        Text.Builder text = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize(element));
        List<String> tooltip = new ArrayList<>();

        tooltip.add("&5&nClick me&7 to join &cDirtCraft's &9Discord");
        if (mcUsername != null) tooltip.add("&7MC Username&8: &6" + mcUsername);
        tooltip.add("&7Discord Name&8: &6" + sender.getUser().getName() + "&8#&7" + sender.getUser().getDiscriminator());
        tooltip.add("§7Rank§8: §6" + sender.getHighestRank().getName());
        tooltip.add("§7Staff Member§8: §6" + (sender.isStaff() ? "§aYes" : "§cNo"));

        text.onHover(TextActions.showText(format(String.join("\n", tooltip))));
        try{
            text.onClick(TextActions.openUrl(new URL("https://dirtcraft.net")));
        } catch (MalformedURLException ignored){}
        return text.toText();
    }
}
