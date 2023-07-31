package net.evilkingdom.prison.modules.users.commands.admin;

import net.evilkingdom.commons.commands.Command;
import net.evilkingdom.commons.commands.PluginCommand;
import net.evilkingdom.commons.commands.parameter.Param;
import net.evilkingdom.prison.constants.Permissions;
import net.evilkingdom.prison.modules.users.User;
import net.evilkingdom.prison.modules.users.currency.Currency;
import net.evilkingdom.prison.utils.Response;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnegative;

public class CurrencyCommand implements PluginCommand {
    @Command(names = "currency set", permission = Permissions.CURRENCY_ADMIN, async = true)
    public void setCurrency(@NotNull final CommandSender sender, @Param final User user, @Param final Currency currency, @Nonnegative @Param final long amount) {
        user.setCurrency(currency, amount);
        Response.get("currency-set").replace("%player%", user.getUsername())
                .replace("%currency%", currency.getFormattedName())
                .replace("%amount%", currency.formatAmount(amount)).send(sender);
    }

    @Command(names = "currency add", permission = Permissions.CURRENCY_ADMIN, async = true)
    public void addCurrency(@NotNull final CommandSender sender, @Param final User user, @Param final Currency currency, @Nonnegative @Param final long amount) {
        user.addCurrency(currency, amount);
        Response.get("currency-add").replace("%player%", user.getUsername())
                .replace("%currency%", currency.getFormattedName())
                .replace("%amount%", currency.formatAmount(amount)).send(sender);
    }

    @Command(names = "currency remove", permission = Permissions.CURRENCY_ADMIN, async = true)
    public void removeCurrency(@NotNull final CommandSender sender, @Param final User user, @Param final Currency currency, @Nonnegative @Param final long amount) {
        user.removeCurrency(currency, amount);
        Response.get("currency-remove").replace("%player%", user.getUsername())
                .replace("%currency%", currency.getFormattedName())
                .replace("%amount%", currency.formatAmount(amount)).send(sender);
    }
}
