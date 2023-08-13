// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands;

import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import java.util.ArrayList;
import com.google.common.collect.Lists;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.spongediscordlib.exceptions.DiscordPermissionException;
import net.dirtcraft.discordlink.utility.Utility;
import java.util.function.Predicate;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRole;
import java.util.List;
import net.dirtcraft.spongediscordlib.commands.DiscordCommand;

public class DiscordCommandImpl implements DiscordCommand
{
    private final List<DiscordRole> allowedRoles;
    private final String description;
    private final DiscordCommandExecutor executor;
    private final String commandUsage;
    private final boolean preBoot;
    
    private DiscordCommandImpl(final List<DiscordRole> allowed, final DiscordCommandExecutor executor, final String commandUsage, final String description, final boolean preBoot) {
        this.allowedRoles = allowed;
        this.executor = executor;
        this.commandUsage = commandUsage;
        this.description = description;
        this.preBoot = preBoot;
    }
    
    public static BuilderImpl builder() {
        return new BuilderImpl();
    }
    
    public final void process(final MessageSource member, final String command, final List<String> args) {
        if (!this.preBoot && !PlatformProvider.isGameReady()) {
            return;
        }
        if (!this.allowedRoles.stream().allMatch((Predicate<? super Object>)member::hasRole)) {
            Utility.sendPermissionError(member);
            return;
        }
        try {
            this.executor.execute(member, command, (List)args);
        }
        catch (DiscordPermissionException e2) {
            Utility.sendPermissionError(member);
        }
        catch (Exception e) {
            Utility.sendCommandError(member, (e.getMessage() != null) ? e.getMessage() : "an error occurred while executing the command.");
        }
    }
    
    public boolean hasPermission(final DiscordMember member) {
        return this.allowedRoles.stream().allMatch((Predicate<? super Object>)member::hasRole);
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String getUsage() {
        return this.commandUsage;
    }
    
    public static class BuilderImpl extends DiscordCommand.Builder
    {
        private List<DiscordRole> allowedRoles;
        private DiscordCommandExecutor executor;
        private String commandUsage;
        private String description;
        private boolean preBootCommand;
        
        private BuilderImpl() {
        }
        
        public final BuilderImpl setRequiredRoles(final DiscordRole... roles) {
            this.allowedRoles = (List<DiscordRole>)Lists.newArrayList((Object[])roles);
            return this;
        }
        
        public final BuilderImpl setCommandExecutor(final DiscordCommandExecutor executor) {
            this.executor = executor;
            return this;
        }
        
        public final BuilderImpl setDescription(final String description) {
            this.description = description;
            return this;
        }
        
        public final BuilderImpl setCommandUsage(final String commandUsage) {
            this.commandUsage = commandUsage;
            return this;
        }
        
        public final BuilderImpl setPreBootEnabled(final boolean b) {
            this.preBootCommand = b;
            return this;
        }
        
        public DiscordCommandImpl build() {
            return new DiscordCommandImpl((this.allowedRoles != null) ? this.allowedRoles : new ArrayList<DiscordRole>(), (this.executor != null) ? this.executor : ((member, command, event) -> {}), (this.commandUsage != null) ? this.commandUsage : "", (this.description != null) ? this.description : "", this.preBootCommand, null);
        }
    }
}
