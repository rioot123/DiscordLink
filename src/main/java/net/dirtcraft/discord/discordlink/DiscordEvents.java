package net.dirtcraft.discord.discordlink;

import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordEvents extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getChannel().getId().equals(SpongeDiscordLib.getGamechatChannelID())) return;
        if (event.getAuthor().isBot() || event.getAuthor().isFake()) return;
        if (hasAttachment(event)) return;

        String username = event.getAuthor().getName();
        String effectiveName = event.getMember().getEffectiveName();

        String message = event.getMessage().getContentDisplay();

        if (event.getMessage().getContentRaw().startsWith(PluginConfiguration.Main.botPrefix + "list")) {
            Utility.listCommand(event);
            return;
        }

        if (event.getMessage().getContentRaw().startsWith(PluginConfiguration.Main.consolePrefix)) {
            Utility.toConsole(event);
            return;
        }

        Role staffRole = event.getGuild().getRoleById(PluginConfiguration.Roles.staffRoleID);
        boolean isStaff = event.getMember().getRoles().contains(staffRole);

        String staff = isStaff ? "Yes" : "No";

        Text.Builder toBroadcast = Text.builder();
        if (!isStaff) {
            toBroadcast.append(
                    Utility.format(PluginConfiguration.Format.discordToServer
                            .replace("{username}", username)
                            .replace("{message}", TextSerializers.FORMATTING_CODE.stripCodes(message))));
        } else {
            toBroadcast.append(
                    Utility.format(PluginConfiguration.Format.discordToServer
                            .replace("{username}", effectiveName)
                            .replace("{message}", message)
                            .replace("&9&l»", "&c&l»")
                    ));
        }
        try {
            List<String> urls = checkURLs(event.getMessage().getContentRaw());
            if (!(urls.size() > 0)) {
                toBroadcast.onClick(TextActions.openUrl(new URL("http://discord.dirtcraft.gg/")));
                if (event.getMember().getNickname() == null) {
                    toBroadcast.onHover(TextActions.showText(
                            Utility.format(
                                    "&5&nClick me&7 to join &cDirtCraft's &9Discord" + "\n"
                                            + "&7Discord Name&8: &6" + event.getAuthor().getName() + "&8#" + event.getAuthor().getDiscriminator() + "\n"
                                            + "&7Staff Member&8: &6" + staff
                            )));
                } else {
                    toBroadcast.onHover(TextActions.showText(
                            Utility.format(
                                    "&5&nClick me&7 to join &cDirtCraft's &9Discord" + "\n"
                                            + "&7Discord Name&8: &6" + event.getAuthor().getName() + "&8#" + event.getAuthor().getDiscriminator() + "\n"
                                            + "&7Nickname&8: &6" + event.getMember().getNickname() + "\n"
                                            + "&7Staff Member&8: &6" + staff
                            )));
                }
            } else {
                toBroadcast.onClick(TextActions.openUrl(new URL(urls.get(0))));
                if (event.getMember().getNickname() == null) {
                    toBroadcast.onHover(TextActions.showText(
                            Utility.format(
                                    "&5&nClick me&7 to open the &dlink" + "\n"
                                            + "&7Discord Name&8: &6" + event.getAuthor().getName() + "&8#" + event.getAuthor().getDiscriminator() + "\n"
                                            + "&7Staff Member&8: &6" + staff
                            )));
                } else {
                    toBroadcast.onHover(TextActions.showText(
                            Utility.format(
                                    "&5&nClick me&7 to open the &dlink" + "\n"
                                            + "&7Discord Name&8: &6" + event.getAuthor().getName() + "&8#" + event.getAuthor().getDiscriminator() + "\n"
                                            + "&7Nickname&8: &6" + event.getMember().getNickname() + "\n"
                                            + "&7Staff Member&8: &6" + staff
                            )));
                }
            }
        } catch (MalformedURLException exception) {
            if (event.getMember().getNickname() == null) {
                toBroadcast.onHover(TextActions.showText(
                        Utility.format(
                                "&cMalformed URL, contact administrator!" + "\n"
                                        + "&7User&8: &6" + event.getAuthor().getName() + "&8#" + event.getAuthor().getAsTag() + "\n"
                                        + "&7Staff Member&8: &6" + staff
                        )
                ));
            } else {
                toBroadcast.onHover(TextActions.showText(
                        Utility.format(
                                "&cMalformed URL, contact administrator!" + "\n"
                                        + "&7User&8: &6" + event.getAuthor().getName() + "&8#" + event.getAuthor().getAsTag() + "\n"
                                        + "&7Nickname&8: &6" + event.getMember().getNickname() + "\n"
                                        + "&7Staff Member&8: &6" + staff
                        )
                ));
            }
            exception.printStackTrace();
        }

        Sponge.getServer().getBroadcastChannel().send(toBroadcast.build());

    }

    public static List<String> checkURLs(String text)
    {
        List<String> containedUrls = new ArrayList<>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
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
