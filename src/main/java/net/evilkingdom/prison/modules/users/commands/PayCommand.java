package net.evilkingdom.prison.modules.users.commands;

import net.evilkingdom.commons.commands.Command;
import net.evilkingdom.commons.commands.PluginCommand;
import net.evilkingdom.commons.commands.parameter.Param;
import net.evilkingdom.prison.modules.users.User;
import net.evilkingdom.prison.modules.users.UsersHandler;
import net.evilkingdom.prison.modules.users.currency.Currency;
import net.evilkingdom.prison.utils.Response;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnegative;

public class PayCommand implements PluginCommand {
    @Command(names = {"pay"}, async = true)
    public void pay(@NotNull final Player sender, @Param final User receiver, @Param final Currency currency, @Nonnegative @Param final long amount) {
        final User user = UsersHandler.getInstance().getOrLoadAndCacheUser(sender.getUniqueId());
        if (!user.hasCurrency(currency, amount)) {
            Response.get("not-enough-currency").replace("%currency%", currency.getFormattedName()).send(sender);
            return;
        }

        user.removeCurrency(currency, amount);
        receiver.addCurrency(currency, amount);
        Response.get("currency-pay").replace("%amount%", currency.formatAmountFull(amount))
                .replace("%player%", receiver.getUsername()).send(sender);
        Response.get("currency-receive").replace("%amount%", currency.formatAmountFull(amount))
                .replace("%player%", sender.getName()).send(receiver.getPlayer());
    }
}
