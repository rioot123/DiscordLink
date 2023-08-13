// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink;

import net.dirtcraft.spongediscordlib.users.UserManager;
import net.dirtcraft.spongediscordlib.channels.ChannelManager;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandManager;
import java.lang.reflect.Method;
import net.dirtcraft.spongediscordlib.commands.DiscordCommand;
import java.util.function.Supplier;
import net.dirtcraft.spongediscordlib.DiscordApiProvider;
import net.dirtcraft.discordlink.commands.DiscordCommandImpl;
import org.spongepowered.api.command.CommandCallable;
import net.dirtcraft.discordlink.commands.sponge.prefix.Set;
import net.dirtcraft.discordlink.commands.sponge.prefix.Test;
import org.spongepowered.api.command.args.CommandElement;
import net.dirtcraft.discordlink.commands.sponge.prefix.Group;
import net.dirtcraft.discordlink.commands.sponge.prefix.Toggle;
import net.dirtcraft.discordlink.commands.sponge.prefix.Clear;
import net.dirtcraft.discordlink.commands.sponge.UnVerify;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import net.dirtcraft.discordlink.commands.sponge.Verify;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.GameState;
import net.dirtcraft.discordlink.events.UltimateChat;
import net.dirtcraft.discordlink.events.NormalChat;
import net.dirtcraft.discordlink.events.SpongeEvents;
import org.spongepowered.api.network.RawDataListener;
import net.dirtcraft.discordlink.events.PluginMessageHandler;
import net.dirtcraft.discordlink.utility.Utility;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import net.dirtcraft.discordlink.events.DiscordEvents;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.Listener;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import net.dirtcraft.spongediscordlib.users.roles.RoleManager;
import net.dv8tion.jda.api.JDA;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.storage.ConfigManager;
import net.dirtcraft.discordlink.users.discord.RoleManagerImpl;
import net.dirtcraft.discordlink.users.UserManagerImpl;
import net.dirtcraft.discordlink.channels.ChannelManagerImpl;
import net.dirtcraft.discordlink.commands.DiscordCommandManagerImpl;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.plugin.PluginContainer;
import org.slf4j.Logger;
import com.google.inject.Inject;
import org.spongepowered.api.config.DefaultConfig;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import net.dirtcraft.spongediscordlib.DiscordApi;
import net.dirtcraft.discordlink.events.ServerBootHandler;

@Plugin(id = "discord-link", name = "Discord Link", description = "Handles gamechats on the DirtCraft Discord.", authors = { "juliann", "ShinyAfro", "Worive" }, dependencies = { @Dependency(id = "ultimatechat", optional = true), @Dependency(id = "sponge-discord-lib"), @Dependency(id = "dirt-database-lib") })
public class DiscordLink extends ServerBootHandler implements DiscordApi
{
    private static DiscordLink instance;
    @DefaultConfig(sharedRoot = false)
    @Inject
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    @Inject
    private Logger logger;
    @Inject
    private PluginContainer container;
    private ChannelBinding.RawDataChannel channel;
    private boolean gameListenersRegistered;
    private boolean shouldDoPostInit;
    private DiscordCommandManagerImpl commandManager;
    private ChannelManagerImpl channelManager;
    private UserManagerImpl userManager;
    private RoleManagerImpl roleManager;
    private ConfigManager configManager;
    private Database storage;
    private JDA jda;
    
    public static DiscordLink get() {
        return DiscordLink.instance;
    }
    
    public JDA getJda() {
        return this.jda;
    }
    
    public UserManagerImpl getUserManager() {
        return this.userManager;
    }
    
    public ChannelManagerImpl getChannelManager() {
        return this.channelManager;
    }
    
    public DiscordCommandManagerImpl getCommandManager() {
        return this.commandManager;
    }
    
    public RoleManager getRoleManager() {
        return this.roleManager;
    }
    
    public boolean isLoaded() {
        return this.isReady;
    }
    
    public void saveConfig() {
        this.configManager.save();
    }
    
    public Database getStorage() {
        return this.storage;
    }
    
    public Logger getLogger() {
        return this.logger;
    }
    
    public ChannelBinding.RawDataChannel getChannel() {
        return this.channel;
    }
    
    @Listener(order = Order.PRE)
    @Override
    public void onGameConstruction(final GameConstructionEvent event) {
        SpongeDiscordLib.getInstance().onPreInit(event);
        this.preInitialize();
        super.onGameConstruction(event);
    }
    
    @Listener(order = Order.PRE)
    @Override
    public void onGameInitialization(final GameInitializationEvent event) {
        this.shouldDoPostInit = true;
        this.postInitialize();
    }
    
    private void preInitialize() {
        this.logger.info("Requesting JDA!");
        SpongeDiscordLib.getJDA(jda -> {
            this.jda = jda;
            if (jda == null) {
                this.logger.error("JDA failed to connect to discord gateway! " + this.container.getName() + " will not load.");
            }
            else {
                this.logger.info("Discord Link pre=initializing...");
                this.configManager = new ConfigManager(this.loader);
                this.storage = new Database();
                this.roleManager = new RoleManagerImpl(jda);
                this.channelManager = new ChannelManagerImpl(jda);
                this.userManager = new UserManagerImpl(this.channelManager, this.roleManager, this.storage);
                this.commandManager = new DiscordCommandManagerImpl();
                jda.addEventListener(new Object[] { new DiscordEvents(this) });
                DiscordLink.instance = this;
                this.isReady = true;
                if (this.shouldDoPostInit) {
                    Task.builder().execute(this::postInitialize).submit((Object)this.container);
                }
                this.sendGameStageEmbed(Sponge.getGame().getState());
                this.logger.info("Discord Link pre=initialized");
            }
        });
    }
    
    private void postInitialize() {
        if (!this.isReady || !this.shouldDoPostInit || this.gameListenersRegistered) {
            return;
        }
        this.gameListenersRegistered = true;
        this.logger.info("Discord Link post=initializing...");
        this.registerCommands();
        Utility.setStatus();
        Utility.setTopic();
        (this.channel = Sponge.getGame().getChannelRegistrar().createRawChannel((Object)this, "Discord-Link")).addListener((RawDataListener)new PluginMessageHandler());
        Sponge.getEventManager().registerListeners((Object)this, (Object)new SpongeEvents(this, this.storage));
        if (SpongeDiscordLib.getServerName().toLowerCase().contains("pixel")) {
            Sponge.getEventManager().registerListeners((Object)this, (Object)new NormalChat(this));
        }
        else {
            Sponge.getEventManager().registerListeners((Object)this, (Object)new UltimateChat(this));
        }
        if (Sponge.getGame().getState() == GameState.SERVER_STARTED) {
            this.sendLaunchedEmbed();
        }
        this.setInstance();
        this.logger.info("Discord Link post=initialized");
    }
    
    private void registerCommands() {
        final CommandSpec verify = CommandSpec.builder().description((Text)Text.of("Verifies your Discord account")).executor((CommandExecutor)new Verify(this.storage)).arguments(GenericArguments.optional(GenericArguments.string((Text)Text.of("code")))).build();
        final CommandSpec unverify = CommandSpec.builder().description((Text)Text.of("Unverifies your Discord account")).executor((CommandExecutor)new UnVerify(this.storage)).build();
        final CommandSpec clear = CommandSpec.builder().permission("discordlink.prefix.clear").executor((CommandExecutor)new Clear()).arguments(GenericArguments.onlyOne(GenericArguments.playerOrSource((Text)Text.of("Target")))).build();
        final CommandSpec toggle = CommandSpec.builder().permission("discordlink.prefix.toggle").executor((CommandExecutor)new Toggle()).arguments(GenericArguments.onlyOne(GenericArguments.playerOrSource((Text)Text.of("Target")))).build();
        final CommandSpec group = CommandSpec.builder().permission("discordlink.prefix.group").executor((CommandExecutor)new Group()).arguments(new CommandElement[] { GenericArguments.onlyOne(GenericArguments.playerOrSource((Text)Text.of("Target"))), GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.string((Text)Text.of("Group")))) }).build();
        final CommandSpec test = CommandSpec.builder().permission("discordlink.prefix.test").executor((CommandExecutor)new Test()).arguments(GenericArguments.flags().flag(new String[] { "s" }).valueFlag(GenericArguments.string((Text)Text.of("ArrowColor")), new String[] { "a" }).valueFlag(GenericArguments.string((Text)Text.of("BracketColor")), new String[] { "c" }).buildWith(GenericArguments.seq(new CommandElement[] { GenericArguments.onlyOne(GenericArguments.playerOrSource((Text)Text.of("Target"))), GenericArguments.remainingJoinedStrings((Text)Text.of("Prefix")) }))).build();
        final CommandSpec set = CommandSpec.builder().permission("discordlink.prefix.modify").executor((CommandExecutor)new Set()).arguments(GenericArguments.flags().flag(new String[] { "s" }).valueFlag(GenericArguments.string((Text)Text.of("ArrowColor")), new String[] { "a" }).valueFlag(GenericArguments.string((Text)Text.of("BracketColor")), new String[] { "c" }).buildWith(GenericArguments.seq(new CommandElement[] { GenericArguments.onlyOne(GenericArguments.playerOrSource((Text)Text.of("Target"))), GenericArguments.remainingJoinedStrings((Text)Text.of("Prefix")) }))).build();
        final CommandSpec prefix = CommandSpec.builder().permission("discordlink.prefix.use").child((CommandCallable)clear, new String[] { "clear", "none", "c" }).child((CommandCallable)toggle, new String[] { "toggle", "arrow", "star", "t" }).child((CommandCallable)set, new String[] { "set", "s" }).child((CommandCallable)group, new String[] { "group", "g" }).child((CommandCallable)test, new String[] { "debug", "test", "d" }).build();
        Sponge.getCommandManager().register((Object)this, (CommandCallable)verify, new String[] { "verify", "link" });
        Sponge.getCommandManager().register((Object)this, (CommandCallable)unverify, new String[] { "unverify", "unlink" });
        Sponge.getCommandManager().register((Object)this, (CommandCallable)prefix, new String[] { "prefix" });
    }
    
    public void setInstance() {
        try {
            this.registerProvider();
            final Supplier<DiscordCommand.Builder> supplier = (Supplier<DiscordCommand.Builder>)DiscordCommandImpl::builder;
            final Method setter = DiscordApiProvider.class.getDeclaredMethod("setProvider", DiscordApi.class, Supplier.class);
            setter.setAccessible(true);
            setter.invoke(null, this, supplier);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void registerProvider() {
        Task.builder().execute(() -> Sponge.getServiceManager().setProvider((Object)this, (Class)DiscordApi.class, (Object)this)).submit((Object)this);
    }
}
