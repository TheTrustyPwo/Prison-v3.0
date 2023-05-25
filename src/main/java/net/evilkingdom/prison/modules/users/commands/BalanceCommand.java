package net.evilkingdom.prison.modules.users.commands;

import net.evilkingdom.prison.commands.Command;
import net.evilkingdom.prison.commands.PluginCommand;
import net.evilkingdom.prison.commands.parameter.Param;
import net.evilkingdom.prison.modules.users.User;
import net.evilkingdom.prison.modules.users.currency.Currency;
import net.evilkingdom.prison.utils.Response;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BalanceCommand implements PluginCommand {
    @Command(names = {"balance", "bal"}, async = true)
    public void execute(@NotNull final CommandSender sender, @Param(defaultValue = "self") final User user) {
        final long tokens = user.getCurrency(Currency.TOKENS);
        final long gems = user.getCurrency(Currency.GEMS);
        Response.get("balance").replace("%player%", user.getUsername())
                .replace("%tokens%", Currency.TOKENS.formatAmountFull(tokens))
                .replace("%gems%", Currency.GEMS.formatAmountFull(gems)).send(sender);
    }
}
