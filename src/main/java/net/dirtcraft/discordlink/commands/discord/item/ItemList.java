// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord.item;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Inventory;
import net.dirtcraft.discordlink.users.UserManagerImpl;
import org.spongepowered.api.item.inventory.property.AbstractInventoryProperty;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import java.util.ArrayList;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import org.spongepowered.api.entity.living.player.User;
import net.dirtcraft.discordlink.DiscordLink;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dirtcraft.discordlink.utility.Pair;
import java.util.Map;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class ItemList implements DiscordCommandExecutor
{
    public void execute(final MessageSource source, final String command, final List<String> args) throws DiscordCommandException {
        final Map<Integer, String> slotNames = Stream.of((Pair[])new Pair[] { new Pair((T)36, (S)"Boots"), new Pair((T)37, (S)"Pants"), new Pair((T)38, (S)"Chest"), new Pair((T)39, (S)"Helmet"), new Pair((T)40, (S)"Offhand") }).collect(Collectors.toMap((Function<? super Pair, ? extends Integer>)Pair::getKey, (Function<? super Pair, ? extends String>)Pair::getValue));
        final UserManagerImpl userManager = DiscordLink.get().getUserManager();
        final User player = this.removeIfPresent((List)args, (Function)userManager::getUser).map(PlatformUser::getOfflinePlayer).orElseThrow(() -> new DiscordCommandException("Player not online / Specified!"));
        final List<String> slots = new ArrayList<String>();
        slots.add(String.format("``%-2s | %-7s | %s``", "Id", "Type", "Contents"));
        final int index;
        final Map<K, String> map;
        final String type;
        final String name;
        final List<String> list;
        player.getInventory().slots().forEach(slot -> {
            index = slot.getInventoryProperty((Class)SlotIndex.class).map(AbstractInventoryProperty::getValue).orElse(-1);
            type = map.getOrDefault(index, "Storage");
            name = slot.peek().map(i -> i.getType().getTranslation().get()).orElse("Empty");
            list.add(String.format("``%02d | %-7s | %s``", index, type, name));
            return;
        });
        source.sendPrivateMessage(String.join("\n", slots));
    }
}
