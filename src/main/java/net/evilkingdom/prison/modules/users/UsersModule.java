package net.evilkingdom.prison.modules.users;

import net.evilkingdom.prison.commands.PluginCommand;
import net.evilkingdom.prison.modules.users.commands.PayCommand;
import net.evilkingdom.prison.modules.users.listeners.UserLoadListener;
import net.evilkingdom.prison.modules.users.notes.NotesHandler;
import net.evilkingdom.prison.modules.users.notes.commands.WithdrawCommand;
import net.evilkingdom.prison.modules.users.notes.listeners.NoteRedeemListener;
import net.evilkingdom.prison.modules.users.ranks.RanksHandler;
import net.evilkingdom.prison.modules.users.commands.BalanceCommand;
import net.evilkingdom.prison.modules.users.commands.ProfileCommand;
import net.evilkingdom.prison.modules.users.commands.admin.CurrencyCommand;
import net.evilkingdom.prison.modules.users.ranks.commands.MaxRankupCommand;
import net.evilkingdom.prison.modules.users.ranks.commands.RankupCommand;
import net.evilkingdom.prison.plugin.PluginHandler;
import net.evilkingdom.prison.plugin.PluginModule;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UsersModule extends PluginModule {
    private static UsersModule instance;

    public static UsersModule getInstance() {
        return instance;
    }

    @Override
    public @NotNull String getName() {
        return "Users";
    }

    @Override
    public @NotNull String getConfigName() {
        return "users";
    }

    @Override
    public void onEnable() {
        instance = this;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onReload() {
        super.onReload();
    }

    @Override
    public @NotNull List<PluginCommand> initializeCommands() {
        return List.of(
                new BalanceCommand(),
                new PayCommand(),
                new ProfileCommand(),
                new CurrencyCommand(),
                new WithdrawCommand(),
                new RankupCommand(),
                new MaxRankupCommand()
        );
    }

    @Override
    public @NotNull List<Listener> initializeListeners() {
        return List.of(
                new UserLoadListener(),
                new NoteRedeemListener()
        );
    }

    @Override
    public @NotNull List<PluginHandler> initializePluginHandlers() {
        return List.of(
                new UsersHandler(),
                new NotesHandler(),
                new RanksHandler()
        );
    }
}
