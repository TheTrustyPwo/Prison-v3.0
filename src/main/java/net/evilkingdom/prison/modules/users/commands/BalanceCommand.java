package net.evilkingdom.prison.modules.users.commands;

import net.evilkingdom.commons.commands.Command;
import net.evilkingdom.commons.commands.PluginCommand;
import net.evilkingdom.commons.commands.parameter.Param;
import net.evilkingdom.prison.modules.users.User;
import net.evilkingdom.prison.modules.users.currency.Currency;
import net.evilkingdom.prison.utils.Response;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BalanceCommand implements PluginCommand {
    @Command(names = {"balance", "bal"}, async = true)
    public void execute(@NotNull final Player sender, @Param(defaultValue = "self") final User user) {
        final long tokens = user.getCurrency(Currency.TOKENS);
        final long gems = user.getCurrency(Currency.GEMS);
        Response.get("balance").replace("%player%", user.getUsername())
                .replace("%tokens%", Currency.TOKENS.formatAmountFull(tokens))
                .replace("%gems%", Currency.GEMS.formatAmountFull(gems)).send(sender);
    }
}
