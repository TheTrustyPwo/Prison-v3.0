package net.evilkingdom.prison.item;

import net.evilkingdom.prison.utils.Text;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class ItemBuilder {
    private static final ItemFlag[] ALL_FLAGS = new ItemFlag[] {
            ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES,
            ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS,
            ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON
    };

    private final ItemStack itemStack;

    private ItemBuilder(ItemStack itemStack) {
        this.itemStack = Objects.requireNonNull(itemStack, "itemStack");
    }

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(new ItemStack(material)).hideAttributes();
    }

    public static ItemBuilder of(ItemStack itemStack) {
        return new ItemBuilder(itemStack).hideAttributes();
    }

    public ItemBuilder transform(Consumer<ItemStack> is) {
        is.accept(this.itemStack);
        return this;
    }

    public ItemBuilder transformMeta(Consumer<ItemMeta> meta) {
        ItemMeta m = this.itemStack.getItemMeta();
        if (m != null) {
            meta.accept(m);
            this.itemStack.setItemMeta(m);
        }
        return this;
    }

    public ItemBuilder name(String name) {
        return transformMeta(meta -> meta.setDisplayName(Text.colorize(name)));
    }

    public ItemBuilder type(Material material) {
        return transform(itemStack -> itemStack.setType(material));
    }

    public ItemBuilder lore(String line) {
        return transformMeta(meta -> {
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add(Text.colorize(line));
            meta.setLore(lore);
        });
    }

    public ItemBuilder lore(String... lines) {
        return transformMeta(meta -> {
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            for (String line : lines) {
                lore.add(Text.colorize(line));
            }
            meta.setLore(lore);
        });
    }

    public ItemBuilder lore(Iterable<String> lines) {
        return transformMeta(meta -> {
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            for (String line : lines) {
                lore.add(Text.colorize(line));
            }
            meta.setLore(lore);
        });
    }

    public ItemBuilder clearLore() {
        return transformMeta(meta -> meta.setLore(new ArrayList<>()));
    }

    public ItemBuilder glow(boolean glow) {
        return enchant(Enchantment.DURABILITY).flag(ItemFlag.HIDE_ENCHANTS);
    }

    public ItemBuilder amount(int amount) {
        return transform(itemStack -> itemStack.setAmount(amount));
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        return transform(itemStack -> itemStack.addUnsafeEnchantment(enchantment, level));
    }

    public ItemBuilder enchant(Enchantment enchantment) {
        return transform(itemStack -> itemStack.addUnsafeEnchantment(enchantment, 1));
    }

    public ItemBuilder clearEnchantments() {
        return transform(itemStack -> itemStack.getEnchantments().keySet().forEach(itemStack::removeEnchantment));
    }

    public ItemBuilder flag(ItemFlag... flags) {
        return transformMeta(meta -> meta.addItemFlags(flags));
    }

    public ItemBuilder unflag(ItemFlag... flags) {
        return transformMeta(meta -> meta.removeItemFlags(flags));
    }

    public ItemBuilder hideAttributes() {
        return flag(ALL_FLAGS);
    }

    public ItemBuilder showAttributes() {
        return unflag(ALL_FLAGS);
    }

    public ItemBuilder color(Color color) {
        return transform(itemStack -> {
            Material type = itemStack.getType();
            if (type == Material.LEATHER_BOOTS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_HELMET || type == Material.LEATHER_LEGGINGS) {
                LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
                meta.setColor(color);
                itemStack.setItemMeta(meta);
            }
        });
    }

    public ItemBuilder breakable(boolean flag) {
        return transformMeta(meta -> meta.setUnbreakable(!flag));
    }

    public ItemBuilder apply(Consumer<ItemBuilder> consumer) {
        consumer.accept(this);
        return this;
    }

    public ItemStack build() {
        return this.itemStack;
    }
}
