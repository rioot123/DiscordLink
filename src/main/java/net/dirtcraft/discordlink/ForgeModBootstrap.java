package net.dirtcraft.discordlink;

import net.dirtcraft.discordlink.forge.DiscordLink;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(value = "discord-link")
public class ForgeModBootstrap {
    public ForgeModBootstrap() {
        for (int i = 0; i < 50; i++) System.out.println("Hello World!!!");
        DiscordLink discordLink = new DiscordLink();
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(discordLink::commonSetup);
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(discordLink::dedicatedSetup);
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(discordLink::interModEnqueue);
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(discordLink::interModProcess);
        //MinecraftForge.EVENT_BUS.register(discordLink);

    }
}
