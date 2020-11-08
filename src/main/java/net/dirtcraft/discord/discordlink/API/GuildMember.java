package net.dirtcraft.discord.discordlink.API;

import net.dirtcraft.discord.discordlink.Commands.Sources.ResponseScheduler;
import net.dirtcraft.discord.discordlink.Commands.Sources.WrappedConsole;
import net.dirtcraft.discord.discordlink.Utility.Platform.PlatformPlayer;
import net.dirtcraft.discord.discordlink.Utility.Platform.PlatformUser;
import net.dirtcraft.discord.discordlink.Utility.Platform.PlatformUtils;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginManager;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class GuildMember extends WrappedMember {
    private PlatformUser user;
    private boolean retrievedPlayer;
    private List<Roles> roles;
    private Roles highestRank;

    public static Optional<GuildMember> fromPlayerId(UUID player) {
        String memberID = DiscordLink.getInstance().getStorage().getDiscordUser(player);
        if (memberID == null) return Optional.empty();

        final Member member = Utility.getMemberById(memberID).orElse(null);
        if (member == null) return Optional.empty();

        final GuildMember profile = new GuildMember(member);
        profile.user = PlatformUtils.getPlayerOffline(player).orElse(null);
        profile.retrievedPlayer = true;
        return Optional.of(profile);
    }

    public GuildMember(Member member){
        super(member);
        Collection<Role> discordRoles = member.getRoles();
        roles = new ArrayList<>();
        highestRank = Arrays.stream(Roles.values())
                .filter(e->discordRoles.contains(e.getRole()))
                .map(Roles::ordinal)
                .reduce(Integer::min)
                .map(integer -> Roles.values()[integer])
                .orElse(Roles.NONE);

        Arrays.stream(Roles.values())
                .filter(e->(e.isStaff() && highestRank.ordinal() <= e.ordinal()) || (discordRoles.contains(e.getRole())))
                .forEach(roles::add);
    }

    public Optional<PlatformPlayer> getPlayer(){
        if (!retrievedPlayer) return getPlayerData().flatMap(PlatformUtils::getPlayer);
        else return Optional.ofNullable(user).flatMap(PlatformUtils::getPlayer);
    }

    public Optional<PlatformUser> getPlayerData(){
        if (!retrievedPlayer) {
            final String playerId = DiscordLink.getInstance().getStorage().getUUIDfromDiscordID(member.getUser().getId());
            final Optional<PlatformUser> optData = Optional.ofNullable(playerId)
                    .map(UUID::fromString)
                    .flatMap(PlatformUtils::getPlayerOffline);
            retrievedPlayer = true;
            return optData;
        } else return Optional.ofNullable(user);
    }

    public void sendMessage(MessageEmbed embed) {
        member.getUser().openPrivateChannel().queue(dm-> dm.sendMessage(embed).queue());
    }

    public void sendMessage(String message) {
        member.getUser().openPrivateChannel().queue(dm-> dm.sendMessage(message).queue());
    }

    public boolean isStaff() {
        return roles.contains(Roles.STAFF);
    }

    public boolean isDonor() {
        return roles.contains(Roles.DONOR);
    }

    public boolean isBoosting() {
        return roles.contains(Roles.NITRO);
    }

    public boolean isVerified() {
        return roles.contains(Roles.VERIFIED);
    }

    public boolean hasRole(Roles role){
        return roles.contains(role);
    }

    public String getChevron(){
        return highestRank.getChevron();
    }

    public String getNameStyle(){
        return highestRank.getStyle();
    }

    public Roles getHighestRank(){
        return highestRank;
    }

    private Optional<SanctionCommand> formatSanctionCommand(String command){
        PlatformUser user = getPlayerData().orElse(null);
        String commandBase = PluginConfiguration.Command.sanctions.stream().filter(command::startsWith).findFirst().orElse(null);
        String args = commandBase == null? null: command.substring(commandBase.length());

        if (commandBase == null || user == null) return Optional.empty();
        else {
            String name = user.getName().orElse(member.getEffectiveName());
            String uuid = user.getUUID().toString();
            String formatted = String.format("%s --sender-uuid=%s --sender=%s %s", commandBase, uuid, name, args);
            return Optional.of(new SanctionCommand(formatted, name.replace(" ", "_")));
        }
    }

    private Optional<SanctionCommand> formatSanctionCommand(String command, String name){
        String commandBase = PluginConfiguration.Command.sanctions.stream().filter(command::startsWith).findFirst().orElse(null);
        String args = commandBase == null? null: command.substring(commandBase.length());

        if (commandBase == null) return Optional.empty();
        else {
            String formatted = String.format("%s --sender=%s %s", commandBase, name.replace(" ", "_"), args);
            return Optional.of(new SanctionCommand(formatted, name));
        }
    }

    public void executeSanction(String command, WrappedConsole sender, boolean bypass){
        CompletableFuture
                .supplyAsync(()->formatSanctionCommand(command))
                .thenAccept(t->{
                    final Optional<SanctionCommand> cmd;
                    if (t.isPresent()) {
                        PluginManager manager = ProxyServer.getInstance().getPluginManager();
                        ResponseScheduler.liteBansCallback(sender, t.get().executorName);
                        manager.dispatchCommand(sender, t.get().command);
                    } else if (bypass && (cmd = formatSanctionCommand(command, member.getEffectiveName())).isPresent()){
                        PluginManager manager = ProxyServer.getInstance().getPluginManager();
                        sender.sendMessage("Failed to find UUID/IGN. Sending as " + cmd.get().executorName);
                        ResponseScheduler.liteBansCallback(sender, "Console");
                        manager.dispatchCommand(sender, cmd.get().command);
                    } else if (bypass) {
                        PluginManager manager = ProxyServer.getInstance().getPluginManager();
                        sender.sendMessage("Failed to find UUID/IGN. Sending as CONSOLE");
                        ResponseScheduler.liteBansCallback(sender, "Console");
                        manager.dispatchCommand(sender, command);
                    } else {
                        sender.sendMessage("Failed to execute \"" + command + "\"\nUUID/IGN not found. (Are you verified?)");
                    }
                }).exceptionally(e->{
            e.printStackTrace();
            return null;
        });
    }

    private static class SanctionCommand{
        final private String command;
        final private String executorName;

        private SanctionCommand(String command, String executorName) {
            this.executorName = executorName;
            this.command = command;
        }
    }
}
