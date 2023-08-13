// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.events;

import java.util.Iterator;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.discordlink.storage.tables.Verification;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.discordlink.utility.PlatformChat;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.discordlink.channels.MessageIntent;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dirtcraft.discordlink.users.MessageSourceImpl;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.users.UserManagerImpl;
import net.dirtcraft.discordlink.channels.ChannelManagerImpl;
import net.dirtcraft.discordlink.commands.DiscordCommandManagerImpl;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordEvents extends ListenerAdapter
{
    private final DiscordCommandManagerImpl commandManager;
    private final ChannelManagerImpl channelManager;
    private final UserManagerImpl userManager;
    private final Database storage;
    
    public DiscordEvents(final DiscordLink discordLink) {
        this.commandManager = discordLink.getCommandManager();
        this.channelManager = discordLink.getChannelManager();
        this.userManager = discordLink.getUserManager();
        this.storage = discordLink.getStorage();
    }
    
    public void onMessageReceived(@Nonnull final MessageReceivedEvent event) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        net/dirtcraft/discordlink/events/DiscordEvents.channelManager:Lnet/dirtcraft/discordlink/channels/ChannelManagerImpl;
        //     4: aload_1         /* event */
        //     5: invokevirtual   net/dv8tion/jda/api/events/message/MessageReceivedEvent.getChannel:()Lnet/dv8tion/jda/api/entities/MessageChannel;
        //     8: invokevirtual   net/dirtcraft/discordlink/channels/ChannelManagerImpl.isGamechat:(Lnet/dv8tion/jda/api/entities/MessageChannel;)Z
        //    11: istore_2        /* gamechat */
        //    12: aload_1         /* event */
        //    13: invokevirtual   net/dv8tion/jda/api/events/message/MessageReceivedEvent.getChannelType:()Lnet/dv8tion/jda/api/entities/ChannelType;
        //    16: getstatic       net/dv8tion/jda/api/entities/ChannelType.PRIVATE:Lnet/dv8tion/jda/api/entities/ChannelType;
        //    19: if_acmpne       26
        //    22: iconst_1       
        //    23: goto            27
        //    26: iconst_0       
        //    27: istore_3        /* privateDm */
        //    28: iload_2         /* gamechat */
        //    29: ifne            36
        //    32: iload_3         /* privateDm */
        //    33: ifeq            56
        //    36: aload_1         /* event */
        //    37: invokevirtual   net/dv8tion/jda/api/events/message/MessageReceivedEvent.getAuthor:()Lnet/dv8tion/jda/api/entities/User;
        //    40: invokeinterface net/dv8tion/jda/api/entities/User.isBot:()Z
        //    45: ifne            56
        //    48: aload_0         /* this */
        //    49: aload_1         /* event */
        //    50: invokespecial   net/dirtcraft/discordlink/events/DiscordEvents.hasAttachment:(Lnet/dv8tion/jda/api/events/message/MessageReceivedEvent;)Z
        //    53: ifeq            57
        //    56: return         
        //    57: aload_0         /* this */
        //    58: aload_1         /* event */
        //    59: iload_3         /* privateDm */
        //    60: invokedynamic   BootstrapMethod #0, run:(Lnet/dirtcraft/discordlink/events/DiscordEvents;Lnet/dv8tion/jda/api/events/message/MessageReceivedEvent;Z)Ljava/lang/Runnable;
        //    65: invokestatic    java/util/concurrent/CompletableFuture.runAsync:(Ljava/lang/Runnable;)Ljava/util/concurrent/CompletableFuture;
        //    68: pop            
        //    69: return         
        //    StackMapTable: 00 05 FC 00 1A 01 40 01 FC 00 08 01 13 00
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.generateNameForVariable(NameVariables.java:264)
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.assignNamesToVariables(NameVariables.java:198)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:276)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void processGuildMessage(final MessageSourceImpl sender, final MessageReceivedEvent event) {
        if (sender.isMuted() && !sender.isStaff()) {
            if (event.getChannelType() != ChannelType.PRIVATE) {
                event.getMessage().delete().queue(s -> {}, e -> {});
            }
            sender.sendPrivateMessage("<@" + sender.getId() + "> You are **not** allowed to talk there! Please open an appeal in <#590388043379376158> to lift your sanction.");
            return;
        }
        final String rawMessage = event.getMessage().getContentRaw();
        final MessageIntent intent = MessageIntent.fromMessageRaw(rawMessage);
        if (intent.isBotCommand()) {
            this.commandManager.process(sender, intent.getCommand(event));
        }
        else if (PlatformProvider.isGameReady() && intent.isChat()) {
            PlatformChat.discordToMCAsync(sender, event);
        }
        else if (PlatformProvider.isGameReady() && intent.isConsole()) {
            final boolean executed = Utility.toConsole(intent.getCommand(event), sender, intent);
            if (executed && intent.isPrivate()) {
                Utility.logCommand((MessageSource)sender, "__Executed Private Command__");
            }
            event.getMessage().delete().queue(s -> {}, e -> {});
        }
    }
    
    public void processPrivateMessage(final MessageSourceImpl sender, final MessageReceivedEvent event) {
        if (!PlatformProvider.isGameReady()) {
            return;
        }
        final MessageIntent intent = (MessageIntent.fromMessageRaw(event.getMessage().getContentRaw()) == MessageIntent.DISCORD_COMMAND) ? MessageIntent.DISCORD_COMMAND : MessageIntent.PRIVATE_COMMAND;
        final String message = MessageIntent.filterConsolePrefixes(event.getMessage().getContentRaw());
        if (intent == MessageIntent.DISCORD_COMMAND) {
            this.commandManager.process(sender, intent.getCommand(event));
        }
        else if (Utility.toConsole(message, sender, intent)) {
            Utility.logCommand((MessageSource)sender, "__Executed Private Command via DM__");
        }
    }
    
    public void processUnverifiedMessage(final MessageSourceImpl sender, final MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.PRIVATE) {
            event.getMessage().delete().queue();
        }
        final Database database = this.storage;
        final Verification.VerificationData data = database.getVerificationData(sender.getId()).orElse(null);
        if (data != null && data.getUUID().isPresent()) {
            sender.setRoleIfAbsent(DiscordRoles.VERIFIED);
            sender.sendCommandResponse("Verified role was missing, But you appear to be verified so it has been added again. Please send message or command again.");
        }
        else if (data != null && data.getCode().isPresent()) {
            final String code = data.getCode().get();
            String message = "You need to be verified in order to use the gamechat or send commands.\n";
            message = message + "Please enter /verify " + code + " in-game to verify your account.";
            final MessageEmbed embed = Utility.embedBuilder().addField("Error", message, false).build();
            sender.sendPrivateMessage(embed);
        }
        else {
            final String code = Utility.getSaltString();
            database.createRecord(sender.getId(), code);
            String message = "You need to be verified in order to use the gamechat or send commands.\n";
            message = message + "Please enter /verify " + code + " in-game to verify your account.";
            final MessageEmbed embed = Utility.embedBuilder().addField("Error", message, false).build();
            sender.sendPrivateMessage(embed);
        }
    }
    
    private boolean hasAttachment(final MessageReceivedEvent event) {
        for (final Message.Attachment attachment : event.getMessage().getAttachments()) {
            if (attachment != null) {
                return true;
            }
        }
        return false;
    }
}
