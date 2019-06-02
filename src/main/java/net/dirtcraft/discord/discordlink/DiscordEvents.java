package net.dirtcraft.discord.discordlink;

import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.net.MalformedURLException;
import java.net.URL;

public class DiscordEvents extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getChannel().getId().equals(PluginConfiguration.Main.channelID)) return;
        if (event.getAuthor().isBot() || event.getAuthor().isFake()) return;

        String username = event.getAuthor().getName();
        String message = event.getMessage().getContentRaw();

        //Role staffRole = event.getGuild().getRoleById("531631265443479562");
        Role staffRole = event.getGuild().getRoleById("549039481450397699");
        boolean isStaff = event.getMember().getRoles().contains(staffRole);

        String staff;

        if (isStaff) {
            staff = "Yes";
        } else {
            staff = "No";
        }

        Text.Builder toBroadcast = Text.builder();
        toBroadcast.append(
                Utility.format(PluginConfiguration.Format.discordToServer
                        .replace("{username}", username)
                        .replace("{message}", message)));
        try {
            toBroadcast.onClick(TextActions.openUrl(new URL("http://discord.dirtcraft.gg/")));
            toBroadcast.onHover(TextActions.showText(
                    Utility.format(
                            "&5&nClick me&7 to join &cDirtCraft's &9Discord" + "\n"
                                    + "&7Discord Name&8: &6" + event.getAuthor().getName() + "&8#" + event.getAuthor().getDiscriminator() + "\n"
                                    + "&7Nickname&8: &6" + event.getMember().getNickname() + "\n"
                                    + "&7Staff Member&8: &6" + staff
                    )));
        } catch (MalformedURLException exception) {
            toBroadcast.onHover(TextActions.showText(
                    Utility.format(
                            "&cMalformed URL, contact administrator!" + "\n"
                                    + "&7User&8: &6" + event.getAuthor().getName() + "&8#" + event.getAuthor().getAsTag() + "\n"
                                    + "&7Nickname&8: &6" + event.getMember().getNickname() + "\n"
                                    + "&7Staff Member&8: &6" + staff
                    )
            ));
            exception.printStackTrace();
        }

        Sponge.getServer().getBroadcastChannel().send(toBroadcast.build());

    }

}
