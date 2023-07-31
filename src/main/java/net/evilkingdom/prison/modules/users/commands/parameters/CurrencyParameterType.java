package net.evilkingdom.prison.modules.users.commands.parameters;

import net.evilkingdom.commons.commands.parameter.ParameterType;
import net.evilkingdom.prison.modules.users.currency.Currency;
import net.evilkingdom.prison.utils.Response;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CurrencyParameterType implements ParameterType<Currency> {
    @Override
    public @Nullable Currency transform(@NotNull CommandSender sender, @NotNull String source) {
        final Currency currency = Currency.getCurrency(source);
        if (currency == null) Response.get("invalid-currency").replace("%currency%", source).send(sender);
        return currency;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String source) {
        return Arrays.stream(Currency.values())
                .map(Currency::toString)
                .filter(string -> string.startsWith(source.toUpperCase()))
                .collect(Collectors.toList());
    }
}
