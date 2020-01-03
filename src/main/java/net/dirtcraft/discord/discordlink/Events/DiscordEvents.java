package net.dirtcraft.discord.discordlink.Events;

import net.dirtcraft.discord.discordlink.API.DiscordSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommand;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Database.Storage;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordEvents extends ListenerAdapter {

    private final HashMap<String, DiscordCommand> commandMap;

    private final Storage storage;

    public DiscordEvents(Storage storage, HashMap<String, DiscordCommand> commandMap) {
        this.storage = storage;
        this.commandMap = commandMap;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getChannel().getId().equals(SpongeDiscordLib.getGamechatChannelID())) return;
        if (event.getAuthor().isBot() || event.getAuthor().isFake()) return;
        if (hasAttachment(event)) return;

        final DiscordSource sender = new DiscordSource(event.getMember());
        final String username = TextSerializers.FORMATTING_CODE.stripCodes(event.getAuthor().getName());
        final String effectiveName = TextSerializers.FORMATTING_CODE.stripCodes(event.getMember().getEffectiveName());

        final String message = event.getMessage().getContentDisplay();
        final String rawMessage = event.getMessage().getContentRaw();

        if (rawMessage.startsWith(PluginConfiguration.Main.botPrefix)) {
            final String[] args = event.getMessage().getContentRaw()
                    .substring(PluginConfiguration.Main.botPrefix.length())
                    .toLowerCase()
                    .split(" ");
            if (args.length != 0) {
                DiscordCommand command = commandMap.get(args[0]);
                if (command != null) command.process(sender, args, event);
            }
            return;
        } else if (rawMessage.startsWith(PluginConfiguration.Main.consolePrefix)) {
            Utility.toConsole(event, sender,false);
            return;
        } else if (rawMessage.startsWith(PluginConfiguration.Main.silentConsolePrefix)) {
            Utility.toConsole(event, sender, true);
            return;
        }

        Task.builder()
                .async()
                .execute(() -> {
                    Text.Builder toBroadcast = Text.builder();
                    String mcUsername = storage.getLastKnownUsername(storage.getUUIDfromDiscordID(event.getMember().getUser().getId()));
                    if (!sender.isStaff()) {

                        Role donorRole = event.getGuild().getRoleById(PluginConfiguration.Roles.donatorRoleID);

                        toBroadcast.append(Utility.format(PluginConfiguration.Format.discordToServer
                                .replace("{username}", mcUsername != null ? mcUsername : username)
                                .replace("{message}", TextSerializers.FORMATTING_CODE.stripCodes(message))
                                .replace("»", event.getMember().getRoles().contains(donorRole) ? "&6&l»" : "&9&l»")));

                    } else {
                        toBroadcast.append(
                                Utility.format(PluginConfiguration.Format.discordToServer
                                        .replace("{username}", effectiveName)
                                        .replace("{message}", message)
                                        .replace("»", !sender.isOwner() ? "&c&l»" : "&4&l»")
                                ));
                    }

                    ArrayList<String> hover = new ArrayList<>();
                    hover.add("&5&nClick me&7 to join &cDirtCraft's &9Discord");
                    if (mcUsername != null) {
                        hover.add("&7MC Username&8: &6" + mcUsername);
                    }
                    hover.add("&7Discord Name&8: &6" + event.getAuthor().getName() + "&8#&7" + event.getAuthor().getDiscriminator());
                    if (event.getMember().getNickname() != null) {
                        hover.add("&7Nickname&8: &6" + event.getMember().getNickname());
                    }
                    hover.add("&7Staff Member&8: &6" + (sender.isStaff() ? "&aYes" : "&cNo"));

                    try {
                        List<String> urls = checkURLs(event.getMessage().getContentRaw());
                        if (!(urls.size() > 0)) {
                            toBroadcast.onClick(TextActions.openUrl(new URL("http://discord.dirtcraft.gg/")));

                            toBroadcast.onHover(TextActions.showText(Utility.format(String.join("\n", hover))));

                        } else {
                            toBroadcast.onClick(TextActions.openUrl(new URL(urls.get(0))));

                            toBroadcast.onHover(TextActions.showText(Utility.format(String.join("\n", hover))));
                        }
                    } catch (MalformedURLException exception) {
                        hover.add("&cMalformed URL, Contact Administrator");

                        toBroadcast.onHover(TextActions.showText(Utility.format(String.join("\n", hover))));

                        exception.printStackTrace();
                    }

                    Sponge.getServer().getBroadcastChannel().send(toBroadcast.build());

                })
                .submit(DiscordLink.getInstance());

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
