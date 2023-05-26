package net.evilkingdom.prison.modules.users.ranks.commands;

import net.evilkingdom.prison.commands.Command;
import net.evilkingdom.prison.commands.PluginCommand;
import net.evilkingdom.prison.commands.parameter.Param;
import net.evilkingdom.prison.modules.users.User;
import net.evilkingdom.prison.modules.users.UsersHandler;
import net.evilkingdom.prison.modules.users.currency.Currency;
import net.evilkingdom.prison.modules.users.ranks.RanksHandler;
import net.evilkingdom.prison.modules.users.ranks.events.PlayerRankupEvent;
import net.evilkingdom.prison.utils.Response;
import net.evilkingdom.prison.utils.numbers.Numbers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnegative;

public class RankupCommand implements PluginCommand {
    @Command(names = {"rankup"})
    public void execute(@NotNull final Player player, @Nonnegative @Param(defaultValue = "1") final long amount) {
        final User user = UsersHandler.getInstance().getUser(player.getUniqueId());
        final long currentRank = user.getRank();
        final long newRank = currentRank + amount;
        final long cost = RanksHandler.getInstance().getRankupAlgorithm().getTotalRankupPrice(currentRank, newRank);

        if (!user.hasCurrency(Currency.TOKENS, cost)) {
            Response.get("not-enough-currency").replace("%currency%", Currency.TOKENS.toString())
                    .replace("%amount%", Numbers.formatLetters(cost)).send(player);
            return;
        }

        final PlayerRankupEvent rankupEvent = new PlayerRankupEvent(player, user, currentRank, newRank);
        Bukkit.getPluginManager().callEvent(rankupEvent);
        if (rankupEvent.isCancelled()) return;

        user.removeCurrency(Currency.TOKENS, cost);
        user.setRank(newRank);

        for (final String command : RanksHandler.getInstance().getRankCommands(currentRank, newRank))
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));

        Response.get("rankup").replace("%rank-before%", RanksHandler.getInstance().getRankDisplay(currentRank))
                .replace("%rank-after%", RanksHandler.getInstance().getRankDisplay(newRank)).send(player);
    }
}
