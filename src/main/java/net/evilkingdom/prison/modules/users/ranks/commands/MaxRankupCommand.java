package net.evilkingdom.prison.modules.users.ranks.commands;

import net.evilkingdom.prison.commands.Command;
import net.evilkingdom.prison.commands.PluginCommand;
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

public class MaxRankupCommand implements PluginCommand {
    @Command(names = {"maxrankup", "rankupmax", "mru"})
    public void execute(@NotNull final Player player) {
        final User user = UsersHandler.getInstance().getUser(player.getUniqueId());
        final long currentRank = user.getRank();
        final long maxRankups = RanksHandler.getInstance().getRankupAlgorithm().getMaxRankups(currentRank, user.getCurrency(Currency.TOKENS));
        final long newRank = currentRank + maxRankups;
        final long cost = RanksHandler.getInstance().getRankupAlgorithm().getTotalRankupPrice(currentRank, newRank);

        if (maxRankups <= 0) {
            Response.get("not-enough-currency").replace("%currency%", Currency.TOKENS.toString())
                    .replace("%amount%", Numbers.formatLetters(RanksHandler.getInstance().getRankupAlgorithm().getRankupPrice(user.getRank())))
                    .send(player);
            return;
        }

        final PlayerRankupEvent rankupEvent = new PlayerRankupEvent(player, user, currentRank, newRank);
        Bukkit.getPluginManager().callEvent(rankupEvent);
        if (rankupEvent.isCancelled()) return;

        user.removeCurrency(Currency.TOKENS, cost);
        user.setRank(newRank);

        for (final String command : RanksHandler.getInstance().getRankCommands(currentRank, newRank))
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));

        Response.get("maxrankup").replace("%rank-before%", RanksHandler.getInstance().getRankDisplay(currentRank))
                .replace("%rank-after%", RanksHandler.getInstance().getRankDisplay(newRank)).send(player);
    }
}