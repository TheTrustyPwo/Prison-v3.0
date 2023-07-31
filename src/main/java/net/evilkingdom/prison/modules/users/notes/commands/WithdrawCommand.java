package net.evilkingdom.prison.modules.users.notes.commands;

import net.evilkingdom.commons.commands.Command;
import net.evilkingdom.commons.commands.PluginCommand;
import net.evilkingdom.commons.commands.parameter.Param;
import net.evilkingdom.prison.modules.users.User;
import net.evilkingdom.prison.modules.users.UsersHandler;
import net.evilkingdom.prison.modules.users.currency.Currency;
import net.evilkingdom.prison.modules.users.notes.NotesHandler;
import net.evilkingdom.prison.utils.Response;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnegative;

public class WithdrawCommand implements PluginCommand {
    @Command(names = {"withdraw", "wd"}, async = true)
    public void execute(@NotNull final Player player, @NotNull @Param final Currency currency, @Nonnegative @Param final long value, @Nonnegative @Param(defaultValue = "1") final int amount) {
        final User user = UsersHandler.getInstance().getUser(player.getUniqueId());
        final long total = value * amount;
        if (user.hasCurrency(currency, total)) {
            final ItemStack itemStack = NotesHandler.getInstance().createNoteItem(currency, value, amount);
            user.removeCurrency(currency, total);
            player.getInventory().addItem(itemStack);
            Response.get("currency-withdraw").replace("%formatted%", currency.formatAmountFull(total)).send(player);
        } else {
            Response.get("not-enough-currency").replace("%currency%", currency.toString()).send(player);
        }
    }
}
