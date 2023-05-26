package net.evilkingdom.prison.modules.users;

import com.google.gson.annotations.JsonAdapter;
import net.evilkingdom.prison.modules.users.currency.Currency;
import net.evilkingdom.prison.modules.users.currency.serializers.CurrencySerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnegative;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class User {
    private final transient boolean requireSave = false;
    private @NotNull
    final UUID uuid;
    @JsonAdapter(CurrencySerializer.class)
    private final Map<Currency, Long> currencies;
    private transient long cacheExpiry = 0L;
    private long rank;

    public User(final @NotNull UUID uuid) {
        this.uuid = uuid;
        this.currencies = new EnumMap<>(Currency.class);
    }

    @NotNull
    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return getPlayer().getName();
    }

    public Player getPlayer() { return Bukkit.getPlayer(this.uuid); }

    public long getRank() {
        return rank;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }

    public long getCurrency(@NotNull Currency currency) {
        return this.currencies.getOrDefault(currency, 0L);
    }

    public boolean hasCurrency(@NotNull Currency currency, long amount) {
        return getCurrency(currency) >= amount;
    }

    public void addCurrency(@NotNull Currency currency, @Nonnegative long amount) {
        long current = getCurrency(currency);
        this.currencies.put(currency, current + amount);
    }

    public void removeCurrency(@NotNull Currency currency, @Nonnegative long amount) {
        long current = getCurrency(currency);
        this.currencies.put(currency, Math.max(current - amount, 0));
    }

    public void setCurrency(@NotNull Currency currency, @Nonnegative long amount) {
        this.currencies.put(currency, amount);
    }

    public void setCacheExpiry(long cacheExpiry) {
        this.cacheExpiry = cacheExpiry;
    }
}
