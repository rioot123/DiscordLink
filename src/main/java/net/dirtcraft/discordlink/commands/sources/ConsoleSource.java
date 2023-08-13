// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.sources;

import java.util.List;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.service.context.Context;
import java.util.Set;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectCollection;
import java.util.Optional;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.service.permission.SubjectReference;
import java.util.Iterator;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;

public abstract class ConsoleSource implements org.spongepowered.api.command.source.ConsoleSource
{
    private CommandSource actualSource;
    
    public ConsoleSource() {
        this.actualSource = (CommandSource)Sponge.getServer().getConsole();
    }
    
    public void sendMessages(final Iterable<Text> messages) {
        for (final Text message : messages) {
            this.sendMessage(message);
        }
    }
    
    public void sendMessages(final Text... messages) {
        for (final Text message : messages) {
            this.sendMessage(message);
        }
    }
    
    public String getName() {
        return this.actualSource.getName();
    }
    
    public boolean isSubjectDataPersisted() {
        return this.actualSource.isSubjectDataPersisted();
    }
    
    public SubjectReference asSubjectReference() {
        return this.actualSource.asSubjectReference();
    }
    
    public MessageChannel getMessageChannel() {
        return this.actualSource.getMessageChannel();
    }
    
    public void setMessageChannel(final MessageChannel channel) {
        this.actualSource.setMessageChannel(channel);
    }
    
    public Optional<CommandSource> getCommandSource() {
        return (Optional<CommandSource>)this.actualSource.getCommandSource();
    }
    
    public SubjectCollection getContainingCollection() {
        return this.actualSource.getContainingCollection();
    }
    
    public SubjectData getSubjectData() {
        return this.actualSource.getSubjectData();
    }
    
    public SubjectData getTransientSubjectData() {
        return this.actualSource.getTransientSubjectData();
    }
    
    public boolean hasPermission(final Set<Context> contexts, final String permission) {
        return this.actualSource.hasPermission((Set)contexts, permission);
    }
    
    public boolean hasPermission(final String permission) {
        return this.actualSource.hasPermission(permission);
    }
    
    public Tristate getPermissionValue(final Set<Context> contexts, final String permission) {
        return this.actualSource.getPermissionValue((Set)contexts, permission);
    }
    
    public boolean isChildOf(final SubjectReference parent) {
        return this.actualSource.isChildOf(parent);
    }
    
    public boolean isChildOf(final Set<Context> contexts, final SubjectReference parent) {
        return this.actualSource.isChildOf((Set)contexts, parent);
    }
    
    public List<SubjectReference> getParents() {
        return (List<SubjectReference>)this.actualSource.getParents();
    }
    
    public List<SubjectReference> getParents(final Set<Context> contexts) {
        return (List<SubjectReference>)this.actualSource.getParents((Set)contexts);
    }
    
    public Optional<String> getOption(final Set<Context> contexts, final String key) {
        return (Optional<String>)this.actualSource.getOption((Set)contexts, key);
    }
    
    public String getIdentifier() {
        return this.actualSource.getIdentifier();
    }
    
    public Set<Context> getActiveContexts() {
        return (Set<Context>)this.actualSource.getActiveContexts();
    }
}
