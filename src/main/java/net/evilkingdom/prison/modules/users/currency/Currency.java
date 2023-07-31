package net.evilkingdom.prison.modules.users.currency;

import net.evilkingdom.commons.utilities.numbers.Numbers;
import net.evilkingdom.commons.utilities.text.Text;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public enum Currency {
    TOKENS("Tokens", "", "&6", "&e", Material.SUNFLOWER),
    GEMS("Gems", "", "&b", "&3", Material.EMERALD);

    private final String name;
    private final String symbol;
    private final String primaryColor;
    private final String secondaryColor;
    private final Material material;

    Currency(final String name, final String symbol, final String primaryColor, final String secondaryColor, final Material material) {
        this.name = name;
        this.symbol = symbol;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.material = material;
    }

    public static Currency getCurrency(@NotNull final String currency) {
        try {
            return valueOf(currency);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public String formatAmountFull(final long amount) {
        return formatAmount(amount) + " " + getFormattedName();
    }

    public String formatAmount(final long amount) {
        return Text.colorize(this.primaryColor + this.symbol + Numbers.formatLetters(amount));
    }

    public String getFormattedName() {
        return Text.colorize(this.secondaryColor + this.name);
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public Material getMaterial() {
        return material;
    }
}
