package net.evilkingdom.prison.modules.users.notes.listeners;

import net.evilkingdom.prison.modules.users.User;
import net.evilkingdom.prison.modules.users.UsersHandler;
import net.evilkingdom.prison.modules.users.currency.Currency;
import net.evilkingdom.prison.modules.users.notes.NotesHandler;
import net.evilkingdom.prison.utils.Response;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class NoteRedeemListener implements Listener {
    @EventHandler
    public void onPlayerInteractEvent(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Player player = event.getPlayer();
            if (player.getEquipment() != null && NotesHandler.getInstance().isNoteItem(player.getEquipment().getItemInMainHand())) {
                final ItemStack itemStack = player.getEquipment().getItemInMainHand();
                final User user = UsersHandler.getInstance().getUser(player.getUniqueId());
                final long value = NotesHandler.getInstance().getNoteValue(itemStack);
                final Currency currency = NotesHandler.getInstance().getNoteCurrency(itemStack);
                final int amount = player.isSneaking() ? itemStack.getAmount() : 1;
                final long total = value * amount;

                user.addCurrency(currency, total);
                itemStack.setAmount(itemStack.getAmount() - amount);
                Response.get("currency-redeem").replace("%formatted%", currency.formatAmountFull(total)).send(player);
            }
        }
    }
}
