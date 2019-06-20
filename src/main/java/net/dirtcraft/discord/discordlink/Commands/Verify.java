package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Database.Storage;
import net.dirtcraft.discord.discordlink.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;

public class Verify implements CommandExecutor {

    private final Storage storage;

    public Verify(Storage storage) {
        this.storage = storage;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        if (!(source instanceof Player)) throw new CommandException(Utility.format("&cOnly a player can verify their Discord account!"));
        Player player = (Player) source;

        String code = args.<String>getOne("code").get();

        if (storage.isVerified(player.getUniqueId())) {
            @Nullable
            User user = SpongeDiscordLib.getJDA().getUserById(storage.getDiscordUser(player.getUniqueId()));

            throw new CommandException(Utility.format(user != null ?
                    "&cYour account is already verified with &6" + user.getName() + "&8#&7" + user.getDiscriminator() + "&c!" :
                    "&cYour account is already verified!"));
        }
        if (!storage.validCode(code)) throw new CommandException(Utility.format("&cThe code &e" + code + "&c is not valid!"));

        storage.updateRecord(code, player.getUniqueId());

        String discordID = storage.getDiscordUser(player.getUniqueId());

        if (discordID == null) throw new CommandException(Utility.format("&cCould not verify your Discord account, please contact an Administrator!"));

        @Nullable
        User user = SpongeDiscordLib.getJDA().getUserById(discordID);

        if (user == null) {
            Text.Builder discordJoin = Text.builder();
            discordJoin.append(Utility.format("&cYour Discord account has been verified, but it is not in the DirtCraft Discord!\n&5&nClick Me&7 to &ajoin &7it"));
            try {
                discordJoin.onHover(TextActions.showText(Utility.format("&6Click me to join the DirtCraft Discord")));
                discordJoin.onClick(TextActions.openUrl(new URL("http://discord.dirtcraft.gg/")));
            } catch (MalformedURLException exception) {
                discordJoin.onHover(TextActions.showText(Utility.format("&cMalformed URL, contact Administrator")));
            }

            throw new CommandException(discordJoin.build());
        }

        String discordName = user.getName();
        String discordTag = user.getDiscriminator();

        player.sendMessage(Utility.format("&7Successfully verified &6" + player.getName() + "&7 with &6" + discordName + "&8#&7" + discordTag));

        Guild guild = SpongeDiscordLib.getJDA().getGuildById(PluginConfiguration.Main.discordServerID);
        Role verifiedRole = guild.getRoleById(PluginConfiguration.Roles.verifiedRoleID);

        guild.getController().addSingleRoleToMember(guild.getMemberById(discordID), verifiedRole).queue();

        return CommandResult.success();
    }


}
