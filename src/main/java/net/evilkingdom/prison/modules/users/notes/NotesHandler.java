package net.evilkingdom.prison.modules.users.notes;

import net.evilkingdom.commons.item.ItemBuilder;
import net.evilkingdom.commons.plugin.PluginHandler;
import net.evilkingdom.commons.plugin.PluginModule;
import net.evilkingdom.prison.Prison;
import net.evilkingdom.prison.modules.users.UsersModule;
import net.evilkingdom.prison.modules.users.currency.Currency;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NotesHandler extends PluginHandler {
    private static final NamespacedKey NOTES_CURRENCY_KEY = new NamespacedKey(Prison.getInstance(), "Prison-Note-Currency");
    private static final NamespacedKey NOTES_VALUE_KEY = new NamespacedKey(Prison.getInstance(), "Prison-Note-Value");
    private static NotesHandler instance;

    public static NotesHandler getInstance() {
        return instance;
    }

    @Override
    public @NotNull PluginModule getModule() {
        return UsersModule.getInstance();
    }

    @Override
    public void load() {
        instance = this;
    }

    public ItemStack createNoteItem(final Currency currency, final long value, final int amount) {
        return ItemBuilder.of(currency.getMaterial()).amount(amount)
                .name(UsersModule.getInstance().getConfig().getString("currency-note.name")
                        .replace("%value%", currency.formatAmountFull(value)))
                .lore(UsersModule.getInstance().getConfig().getStringList("currency-note.lore"))
                .transformMeta(meta -> {
                    meta.getPersistentDataContainer().set(NOTES_CURRENCY_KEY, PersistentDataType.STRING, currency.toString());
                    meta.getPersistentDataContainer().set(NOTES_VALUE_KEY, PersistentDataType.LONG, value);
                }).glow(true).build();
    }

    public long getNoteValue(@Nullable final ItemStack itemStack) {
        if (!isNoteItem(itemStack)) return 0L;
        //noinspection ConstantConditions
        return itemStack.getItemMeta().getPersistentDataContainer().get(NOTES_VALUE_KEY, PersistentDataType.LONG);
    }

    public Currency getNoteCurrency(@Nullable final ItemStack itemStack) {
        if (!isNoteItem(itemStack)) return null;
        final String currency = itemStack.getItemMeta().getPersistentDataContainer().get(NOTES_CURRENCY_KEY, PersistentDataType.STRING);
        return Currency.getCurrency(currency);
    }

    public boolean isNoteItem(@Nullable final ItemStack itemStack) {
        if (itemStack == null || itemStack.getItemMeta() == null) return false;
        return itemStack.getItemMeta().getPersistentDataContainer().has(NOTES_CURRENCY_KEY, PersistentDataType.STRING);
    }
}
