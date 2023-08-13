// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.commands.discord.item;

import org.spongepowered.api.item.inventory.Inventory;
import net.dirtcraft.discordlink.users.UserManagerImpl;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.query.QueryOperation;
import java.util.Optional;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import java.util.function.Function;
import org.spongepowered.api.entity.living.player.User;
import net.dirtcraft.discordlink.DiscordLink;
import java.util.List;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;

public class ItemRemove implements DiscordCommandExecutor
{
    public void execute(final MessageSource source, final String command, final List<String> args) throws DiscordCommandException {
        final UserManagerImpl userManager = DiscordLink.get().getUserManager();
        final User player = this.removeIfPresent((List)args, (Function)userManager::getUser).map(PlatformUser::getOfflinePlayer).orElseThrow(() -> new DiscordCommandException("Player not online / Specified!"));
        final int index = Optional.ofNullable(args.isEmpty() ? null : args.get(0)).filter(s -> s.matches("\\d+")).map((Function<? super String, ? extends Integer>)Integer::parseInt).orElseThrow(() -> new DiscordCommandException("You must specify a slot to clear!"));
        final Inventory slot = player.getInventory().query(new QueryOperation[] { QueryOperationTypes.INVENTORY_PROPERTY.of((Object)SlotIndex.of((Object)index)) });
        source.sendPrivateMessage("``" + slot.peek().map(s -> s.getType().getTranslation().get()).orElse("Empty") + " has been removed.``");
        slot.set(ItemStack.empty());
    }
}
