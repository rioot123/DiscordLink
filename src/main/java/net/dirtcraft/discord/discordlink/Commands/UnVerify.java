package net.dirtcraft.discord.discordlink.Commands;

import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Database.Storage;
import net.dirtcraft.discord.discordlink.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nullable;

public class UnVerify implements CommandExecutor {

    private final Storage storage;

    public UnVerify(Storage storage) {
        this.storage = storage;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        if (!(source instanceof Player)) throw new CommandException(Utility.format("&cOnly a player can unverify their Discord account!"));
        Player player = (Player) source;

        if (!storage.isVerified(player.getUniqueId())) throw new CommandException(Utility.format("&cYour account is not verified!"));

        String discordID = storage.getDiscordUser(player.getUniqueId());

        @Nullable
        User user = SpongeDiscordLib.getJDA().getUserById(discordID);

        source.sendMessage(Utility.format(user != null ?
                "&7The account &6" + user.getName() + "&8#&7" + user.getDiscriminator() + " has been &cunverified" :
                "&7Your account has been &cunverified"));

        storage.deleteRecord(player.getUniqueId());

        if (user != null) {
            Guild guild = SpongeDiscordLib.getJDA().getGuildById(PluginConfiguration.Main.discordServerID);
            Member member = guild.getMemberById(discordID);
            Role verifiedRole = guild.getRoleById(PluginConfiguration.Roles.verifiedRoleID);
            Role donorRole = guild.getRoleById(PluginConfiguration.Roles.donatorRoleID);

            if (member.getRoles().contains(verifiedRole)) {
                guild.getController().removeSingleRoleFromMember(guild.getMemberById(discordID), verifiedRole).queue();
            }
            if (member.getRoles().contains(donorRole)) {
                guild.getController().removeSingleRoleFromMember(member, donorRole).queue();
            }
        }


        return CommandResult.success();
    }
}
