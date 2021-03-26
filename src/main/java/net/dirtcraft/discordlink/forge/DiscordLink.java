package net.dirtcraft.discordlink.forge;

import net.dirtcraft.discordlink.common.channels.ChannelManagerImpl;
import net.dirtcraft.discordlink.common.commands.DiscordCommandImpl;
import net.dirtcraft.discordlink.common.commands.DiscordCommandManagerImpl;
import net.dirtcraft.discordlink.common.events.*;
import net.dirtcraft.discordlink.common.storage.ConfigManager;
import net.dirtcraft.discordlink.common.storage.Database;
import net.dirtcraft.discordlink.common.storage.Permission;
import net.dirtcraft.discordlink.common.storage.Settings;
import net.dirtcraft.discordlink.common.users.UserManagerImpl;
import net.dirtcraft.discordlink.common.users.discord.RoleManagerImpl;
import net.dirtcraft.discordlink.common.utility.Utility;
import net.dirtcraft.discordlink.api.DiscordApi;
import net.dirtcraft.discordlink.api.DiscordApiProvider;
import net.dirtcraft.discordlink.api.commands.DiscordCommand;
import net.dirtcraft.discordlink.api.users.roles.RoleManager;
import net.dirtcraft.discordlink.forge.handlers.NormalChat;
import net.dirtcraft.discordlink.forge.handlers.PluginMessageHandler;
import net.dirtcraft.discordlink.forge.handlers.ServerBootHandler;
import net.dirtcraft.discordlink.forge.handlers.SpongeEvents;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.function.Supplier;

public class DiscordLink extends ServerBootHandler implements DiscordApi {
    private static DiscordLink instance;

    private boolean gameListenersRegistered;
    private boolean shouldDoPostInit;
    private DiscordCommandManagerImpl commandManager;
    private ChannelManagerImpl channelManager;
    private UserManagerImpl userManager;
    private RoleManagerImpl roleManager;
    private ConfigManager configManager;
    private Database storage;
    private JDA jda;

    public static DiscordLink get(){
        return instance;
    }

    @Override
    public JDA getJda() {
        return jda;
    }

    @Override
    public UserManagerImpl getUserManager() {
        return userManager;
    }

    @Override
    public ChannelManagerImpl getChannelManager() {
        return channelManager;
    }

    @Override
    public DiscordCommandManagerImpl getCommandManager() {
        return commandManager;
    }

    @Override
    public RoleManager getRoleManager() {
        return roleManager;
    }

    @Override
    public boolean isLoaded() {
        return isReady;
    }

    public void saveConfig(){
        configManager.save();
    }

    public Database getStorage(){
        return storage;
    }

    public Logger getLogger(){
        return logger;
    }

    public ChannelBinding.RawDataChannel getChannel(){
        return channel;
    }

    @Override
    @Listener (order = Order.PRE)
    public void onGameConstruction(GameConstructionEvent event) {
        SpongeDiscordLib.getInstance().onPreInit(event);
        preInitialize();
        super.onGameConstruction(event);
    }

    @Override
    @Listener(order = Order.PRE)
    public void onGameInitialization(GameInitializationEvent event) {
        shouldDoPostInit = true;
        postInitialize();
    }

    private void preInitialize(){
        logger.info("Requesting JDA!");
        SpongeDiscordLib.getJDA(jda->{
            logger.info("Discord Link pre=initializing...");
            if ((this.jda = jda) == null) {
                logger.error("JDA failed to connect to discord gateway! " + container.getName() + " will not load.");
                return;
            }

            this.configManager = new ConfigManager(loader);
            this.storage = new Database();
            this.roleManager = new RoleManagerImpl(jda);
            this.channelManager = new ChannelManagerImpl(jda);
            this.userManager = new UserManagerImpl(channelManager, roleManager, storage);
            this.commandManager = new DiscordCommandManagerImpl();
            jda.addEventListener(new DiscordEvents(this));
            instance = this;
            isReady = true;
            if (shouldDoPostInit) Task.builder()
                    .execute(this::postInitialize)
                    .submit(container);
            sendGameStageEmbed(Sponge.getGame().getState());
            logger.info("Discord Link pre=initialized");
        });
    }

    private void postInitialize(){
        logger.info("Discord Link post=initializing...");
        if (!isReady || !shouldDoPostInit || gameListenersRegistered) return;
        gameListenersRegistered = true;
        this.registerCommands();
        Utility.setStatus();
        Utility.setTopic();

        channel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, Settings.ROOT_CHANNEL);
        channel.addListener(new PluginMessageHandler());
        Sponge.getEventManager().registerListeners(this, new SpongeEvents(this, storage));
        if (SpongeDiscordLib.getServerName().toLowerCase().contains("pixel")) {
            Sponge.getEventManager().registerListeners(this, new NormalChat(this));
        } else {
            Sponge.getEventManager().registerListeners(this, new UltimateChat(this));
        }
        if (Sponge.getGame().getState() == GameState.SERVER_STARTED) sendLaunchedEmbed();
        setInstance();
        logger.info("Discord Link post=initialized");
    }

    private void registerCommands(){
        CommandSpec verify = CommandSpec.builder()
                .description(Text.of("Verifies your Discord account"))
                .executor(new Verify(storage))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("code"))))
                .build();

        CommandSpec unverify = CommandSpec.builder()
                .description(Text.of("Unverifies your Discord account"))
                .executor(new UnVerify(storage))
                .build();

        CommandSpec clear = CommandSpec.builder()
                .permission(Permission.PREFIX_CLEAR)
                .executor(new Clear())
                .arguments(GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of("Target"))))
                .build();

        CommandSpec toggle = CommandSpec.builder()
                .permission(Permission.PREFIX_TOGGLE)
                .executor(new Toggle())
                .arguments(GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of("Target"))))
                .build();

        CommandSpec group = CommandSpec.builder()
                .permission(Permission.PREFIX_GROUP)
                .executor(new Group())
                .arguments(GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of("Target"))),
                        GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.string(Text.of("Group")))))
                .build();

        CommandSpec test = CommandSpec.builder()
                .permission(Permission.PREFIX_TEST)
                .executor(new Test())
                .arguments(GenericArguments.flags()
                        .flag("s")
                        .valueFlag(GenericArguments.string(Text.of("ArrowColor")), "a")
                        .valueFlag(GenericArguments.string(Text.of("BracketColor")), "c")
                        .buildWith(GenericArguments.seq(
                                GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of("Target"))),
                                GenericArguments.remainingJoinedStrings(Text.of("Prefix")))))
                .build();

        CommandSpec set = CommandSpec.builder()
                .permission(Permission.PREFIX_MODIFY)
                .executor(new Set())
                .arguments(GenericArguments.flags()
                        .flag("s")
                        .valueFlag(GenericArguments.string(Text.of("ArrowColor")), "a")
                        .valueFlag(GenericArguments.string(Text.of("BracketColor")), "c")
                        .buildWith(GenericArguments.seq(
                                GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of("Target"))),
                                GenericArguments.remainingJoinedStrings(Text.of("Prefix")))))
                .build();

        CommandSpec prefix = CommandSpec.builder()
                .permission(Permission.PREFIX_USE)
                .child(clear, "clear", "none", "c")
                .child(toggle, "toggle", "arrow", "star", "t")
                .child(set, "set", "s")
                .child(group, "group", "g")
                .child(test, "debug", "test", "d")
                .build();

        Sponge.getCommandManager().register(this, verify, "verify", "link");
        Sponge.getCommandManager().register(this, unverify, "unverify", "unlink");
        Sponge.getCommandManager().register(this, prefix, "prefix");
    }

    public void setInstance(){
        try {
            registerProvider();
            Supplier<DiscordCommand.Builder> supplier = DiscordCommandImpl::builder;
            Method setter = DiscordApiProvider.class
                    .getDeclaredMethod("setProvider", DiscordApi.class, Supplier.class);
            setter.setAccessible(true);
            setter.invoke(null, this, supplier);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void registerProvider(){
        Task.builder()
                .execute(()-> Sponge.getServiceManager().setProvider(this, DiscordApi.class, this))
                .submit(this);
    }

}
