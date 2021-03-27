package net.dirtcraft.discordlink.forge;

import net.dirtcraft.discordlink.common.channels.ChannelManagerImpl;
import net.dirtcraft.discordlink.common.commands.DiscordCommandImpl;
import net.dirtcraft.discordlink.common.commands.DiscordCommandManagerImpl;
import net.dirtcraft.discordlink.common.events.*;
import net.dirtcraft.discordlink.common.storage.ConfigManager;
import net.dirtcraft.discordlink.common.storage.Database;
import net.dirtcraft.discordlink.common.users.UserManagerImpl;
import net.dirtcraft.discordlink.common.users.discord.RoleManagerImpl;
import net.dirtcraft.discordlink.common.utility.JdaSupplier;
import net.dirtcraft.discordlink.common.utility.Utility;
import net.dirtcraft.discordlink.api.DiscordApi;
import net.dirtcraft.discordlink.api.DiscordApiProvider;
import net.dirtcraft.discordlink.api.commands.DiscordCommand;
import net.dirtcraft.discordlink.api.users.roles.RoleManager;
import net.dirtcraft.discordlink.forge.handlers.ServerBootHandler;
import net.dirtcraft.discordlink.forge.platform.PlatformProvider;
import net.dv8tion.jda.api.JDA;
import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.function.Supplier;

public class DiscordLink extends ServerBootHandler implements DiscordApi {
    private static final Logger LOGGER = LogManager.getLogger();
    private static DiscordLink instance;

    private boolean shouldDoPostInit;
    private DiscordCommandManagerImpl commandManager;
    private ChannelManagerImpl channelManager;
    private UserManagerImpl userManager;
    private RoleManagerImpl roleManager;
    private ConfigManager configManager;
    private PlatformProvider provider;
    private Database storage;
    private JDA jda;

    public static DiscordLink get(){
        return instance;
    }

    public DiscordLink() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::dedicatedSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::interModEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::interModProcess);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        Path configDir = FMLPaths.GAMEDIR.get()
                .resolve(FMLConfig.defaultConfigPath())
                .resolve("Discord-Link")
                .resolve("configuration.hocon");
        ConfigurationOptions options = ConfigurationOptions.defaults().shouldCopyDefaults(true);
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
                .path(configDir)
                .defaultOptions(options)
                .build();
        this.configManager = new ConfigManager(loader);
        preInitialize(new JdaSupplier(loader));
        super.commonSetup(event);
    }

    public void dedicatedSetup(FMLDedicatedServerSetupEvent event) {
        shouldDoPostInit = true;
        postInitialize();
    }

    private void preInitialize(JdaSupplier supplier){
        LOGGER.info("Requesting JDA!");
        supplier.getJDA(jda->{
            LOGGER.info("Discord Link pre=initializing...");
            if ((this.jda = jda) == null) {
                LOGGER.error("JDA failed to connect to discord gateway! Discord-Link will not load.");
                return;
            }

            IntegratedServer server = Minecraft.getInstance().getIntegratedServer();
            this.provider = new PlatformProvider(server);
            this.storage = new Database(provider);
            this.roleManager = new RoleManagerImpl(jda);
            this.channelManager = new ChannelManagerImpl(jda);
            this.userManager = new UserManagerImpl(channelManager, roleManager, storage, provider);
            this.commandManager = new DiscordCommandManagerImpl();
            jda.addEventListener(new DiscordEvents(this, provider));
            instance = this;
            isReady = true;
            //todo platform executor
            server.execute(this::postInitialize);
            sendGameStageEmbed(state);
            LOGGER.info("Discord Link pre=initialized");
        });
    }

    private void postInitialize(){
        LOGGER.info("Discord Link post=initializing...");
        if (!isReady || !shouldDoPostInit) return;
        shouldDoPostInit = false;
        //todo commands
        Utility.setStatus();
        Utility.setTopic();

        //todo register forge events
        //todo register chat handler
        if (isStarted()) sendLaunchedEmbed();
        setInstance();
        LOGGER.info("Discord Link post=initialized");
    }

    public void setInstance(){
        try {
            Supplier<DiscordCommand.Builder> supplier = DiscordCommandImpl::builder;
            Method setter = DiscordApiProvider.class
                    .getDeclaredMethod("setProvider", DiscordApi.class, Supplier.class);
            setter.setAccessible(true);
            setter.invoke(null, this, supplier);
        } catch (Exception e){
            e.printStackTrace();
        }
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
    public PlatformProvider getPlatformProvider() {
        return provider;
    }

    @Override
    public boolean isLoaded() {
        return isReady;
    }

    public void saveConfig(){
        configManager.save();
    }

    @Override
    public Database getStorage(){
        return storage;
    }

}
