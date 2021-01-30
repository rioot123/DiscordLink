package net.dirtcraft.discordlink.commands.discord.item;

import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.users.MessageSource;
import net.dirtcraft.discordlink.api.commands.DiscordCommandExecutor;
import net.dirtcraft.discordlink.api.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.users.UserManagerImpl;
import net.dirtcraft.discordlink.users.platform.PlatformUserImpl;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import java.util.List;
import java.util.Optional;

public class ItemRemove implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        UserManagerImpl userManager = DiscordLink.get().getUserManager();
        User player = removeIfPresent(args, userManager::getUser)
                .map(PlatformUserImpl::getUser)
                .orElseThrow(()->new DiscordCommandException("Player not online / Specified!"));
        int index = Optional.ofNullable(args.isEmpty()? null: args.get(0))
                .filter(s->s.matches("\\d+"))
                .map(Integer::parseInt)
                .orElseThrow(()->new DiscordCommandException("You must specify a slot to clear!"));
        Inventory slot = player.getInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(index)));
        source.sendMessage("``" + slot.peek().map(s->s.getType().getTranslation().get()).orElse("Empty") + " has been removed.``");
        slot.set(ItemStack.empty());
    }
}
