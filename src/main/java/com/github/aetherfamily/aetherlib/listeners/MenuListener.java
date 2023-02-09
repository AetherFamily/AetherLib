package com.github.aetherfamily.aetherlib.listeners;

import com.github.aetherfamily.aetherlib.menus.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MenuListener implements Listener {
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player player))
            return;

        Menu menu = Menu.getMenu(player);

        if (menu != null) {
            Menu.getMenu(player).onClose();
            Menu.eraseMenu(player);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Menu.eraseMenu(e.getPlayer());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p))
            return;

        final Menu menu = Menu.getMenu(p);

        if (menu == null || !e.getInventory().equals(menu.getInventory()))
            return;

        menu.onClick(e);
    }
}
