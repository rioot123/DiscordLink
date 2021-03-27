package net.dirtcraft.discordlink.common.commands;

import com.google.common.collect.Lists;
import net.dirtcraft.discordlink.api.DiscordApi;
import net.dirtcraft.discordlink.api.DiscordApiProvider;
import net.dirtcraft.discordlink.forge.platform.PlatformProvider;
import net.dirtcraft.discordlink.common.utility.Utility;
import net.dirtcraft.discordlink.api.commands.DiscordCommand;
import net.dirtcraft.discordlink.api.commands.DiscordCommandExecutor;
import net.dirtcraft.discordlink.api.exceptions.DiscordPermissionException;
import net.dirtcraft.discordlink.api.users.DiscordMember;
import net.dirtcraft.discordlink.api.users.MessageSource;
import net.dirtcraft.discordlink.api.users.roles.DiscordRole;

import java.util.ArrayList;
import java.util.List;

public class DiscordCommandImpl implements DiscordCommand {
    private final PlatformProvider provider;
    private final List<DiscordRole> allowedRoles;
    private final String description;
    private final DiscordCommandExecutor executor;
    private final String commandUsage;
    private final boolean preBoot;

    private DiscordCommandImpl(PlatformProvider provider,
                               List<DiscordRole> allowed,
                               DiscordCommandExecutor executor,
                               String commandUsage,
                               String description,
                               boolean preBoot) {
        this.allowedRoles = allowed;
        this.executor = executor;
        this.provider = provider;
        this.commandUsage = commandUsage;
        this.description = description;
        this.preBoot = preBoot;
    }

    public static BuilderImpl builder()         {
        return new BuilderImpl();
    }

    public final void process(MessageSource member, String command, List<String> args) {
        if (!preBoot && !provider.isGameReady()) return;
        if (!allowedRoles.stream().allMatch(member::hasRole)) {
            Utility.sendPermissionError(member);
            return;
        }
        try {
            executor.execute(member, command, args);
        } catch (DiscordPermissionException e) {
            Utility.sendPermissionError(member);
        } catch (Exception e) {
            //e.printStackTrace();
            Utility.sendCommandError(member, e.getMessage() != null ? e.getMessage() : "an error occurred while executing the command.");
        }
    }

    public boolean hasPermission(DiscordMember member){
        return allowedRoles.stream().allMatch(member::hasRole);
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return commandUsage;
    }

    public static class BuilderImpl extends DiscordCommand.Builder {
        private List<DiscordRole> allowedRoles;
        private DiscordCommandExecutor executor;
        private String commandUsage;
        private String description;
        private boolean preBootCommand;

        private BuilderImpl(){}

        @Override
        public final BuilderImpl setRequiredRoles(DiscordRole... roles){
            allowedRoles = Lists.newArrayList(roles);
            return this;
        }

        @Override
        public final BuilderImpl setCommandExecutor(DiscordCommandExecutor executor){
            this.executor = executor;
            return this;
        }

        @Override
        public final BuilderImpl setDescription(String description){
            this.description = description;
            return this;
        }

        @Override
        public final BuilderImpl setCommandUsage(String commandUsage){
            this.commandUsage = commandUsage;
            return this;
        }

        @Override
        public final BuilderImpl setPreBootEnabled(boolean b){
            this.preBootCommand = b;
            return this;
        }

        @Override
        public DiscordCommandImpl build(){
            return new DiscordCommandImpl(
                    DiscordApiProvider.getApi().get().getPlatformProvider(),
                    allowedRoles != null ? allowedRoles : new ArrayList<>(),
                    executor     != null ? executor     : (member, command, event)->{},
                    commandUsage != null ? commandUsage : "",
                    description  != null ? description  : "",
                    preBootCommand
            );
        }

    }
}
