package net.dirtcraft.discordlink.api;

import net.dirtcraft.discordlink.api.commands.DiscordCommand;

import java.lang.reflect.Field;
import java.util.function.Supplier;

public abstract class DiscordApiProvider {
    static DiscordApi provider;

    private static void setProvider(DiscordApi provider, Supplier<DiscordCommand.Builder> builderFactory) throws NoSuchFieldException, IllegalAccessException {
        DiscordApiProvider.provider = provider;
        Class<DiscordCommand.Builder> clazz = DiscordCommand.Builder.class;
        Field field = clazz.getDeclaredField("builderSupplier");
        field.setAccessible(true);
        field.set(null, builderFactory);
    }

    public static DiscordApi getInstance(){
        return provider;
    }
}
