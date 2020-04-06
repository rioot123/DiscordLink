package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Utility;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.spongepowered.api.entity.living.player.User;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Unlink implements DiscordCommandExecutor {
    @Override
    public void execute(GuildMember source, String[] args, MessageReceivedEvent event) throws DiscordCommandException {
        System.out.println(args.length);
        if (args.length == 1){
            System.out.println(args[0]);
            GuildMember sender = new GuildMember(source);
            DiscordLink.getInstance().getStorage().deleteRecord(source.getUser().getId());
            Utility.sendResponse(event, "Successfully unlinked " + sender.getSpongeUser().map(User::getName).orElse("your account") + ".");
            return;
        }
        final String discordID = args[1];
        Pattern pattern = Pattern.compile("<?@?!?(\\d+)>?");
        Matcher matcher = pattern.matcher(discordID);
        if (!matcher.matches() || GameChat.getGuild().getMemberById(matcher.group(1)) == null) throw new DiscordCommandException("Invalid Discord ID");

        final GuildMember player = new GuildMember(GameChat.getGuild().getMemberById(matcher.group(1)));
        final Optional<User> user = player.getSpongeUser();
        if (!user.isPresent()) throw new DiscordCommandException("The user was not verified!");
        GameChat.sendEmbed(null, "Successfully unlinked " + user.get().getName() + ".");
    }
}
