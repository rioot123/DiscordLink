package net.dirtcraft.discordlink.api.commands;

import net.dirtcraft.discordlink.commands.DiscordCommandImpl;
import net.dirtcraft.discordlink.users.discord.Roles;

import java.util.function.Supplier;

public interface DiscordCommand {
    static Builder builder(){
        return Builder.builderSupplier.get();
    }

    abstract class Builder {
        static Supplier<Builder> builderSupplier;

        public abstract DiscordCommandImpl.BuilderImpl setRequiredRoles(Roles... roles);

        public abstract DiscordCommandImpl.BuilderImpl setCommandExecutor(DiscordCommandExecutor executor);

        public abstract DiscordCommandImpl.BuilderImpl setDescription(String description);

        public abstract DiscordCommandImpl.BuilderImpl setCommandUsage(String commandUsage);

        public abstract DiscordCommandImpl.BuilderImpl setPreBootEnabled(boolean b);

        public abstract DiscordCommandImpl build();
    }
}
