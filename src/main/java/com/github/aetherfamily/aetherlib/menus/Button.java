package com.github.aetherfamily.aetherlib.menus;

import com.github.aetherfamily.aetherlib.AetherLib;
import com.github.aetherfamily.aetherlib.utilities.Utils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class Button implements Comparable<Button> {
    private ItemStack itemStack;
    private Consumer<InventoryClickEvent> consumer;

    private Sound sound;

    private Button(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static Button of(Material mat) {
        return new Button(new ItemStack(mat));
    }

    public Button name(String name) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Utils.color(name));
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public Button name(Component name) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(name);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public Button lore(List<String> lore) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(lore.stream().map(Utils::color).toList());
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public Button lore(String... s) {
        lore(Arrays.stream(s).toList());
        return this;
    }

    public Button skull(String base64) {
        final UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());
        itemStack = Bukkit.getUnsafe().modifyItemStack(itemStack,
                "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}"
        );
        return this;
    }

    public Button skullOwner(Player player) {
        if (!(itemStack.getItemMeta() instanceof SkullMeta meta)) return this;
        meta.setOwningPlayer(player);
        itemStack.setItemMeta(meta);
        return this;
    }

    public Button glow() {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addEnchant(Enchantment.MENDING, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public Button sound(String str) {
        this.sound = Sound.sound(Key.key(str), Sound.Source.MASTER, 1, 1);
        return this;
    }

    public Button hideAttributes() {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public Button updateEvery(long ticks, Menu.AetherRunnable aetherRunnable) {
        aetherRunnable.runTaskTimer(AetherLib.getPlugin(AetherLib.class), 0, ticks, this);
        return this;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }


    public Button action(Consumer<InventoryClickEvent> consumer) {
        this.consumer = consumer;
        return this;
    }

    public void onPress(InventoryClickEvent e) {
        if (consumer != null) consumer.accept(e);
        if (sound != null) e.getWhoClicked().playSound(sound);
    }

    @Override
    public int compareTo(Button o) {
        if (this == o) return 0;
        if (o == null || getClass() != o.getClass()) return 0;
        return itemStack.getItemMeta().getDisplayName().compareTo(o.getItemStack().getItemMeta().getDisplayName());
    }
}
