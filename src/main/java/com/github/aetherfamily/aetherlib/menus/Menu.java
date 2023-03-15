package com.github.aetherfamily.aetherlib.menus;

import com.github.aetherfamily.aetherlib.AetherLib;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.types.PermissionNode;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.BiPredicate;

/**
 * Work in progress to be used for all menus. I will see what is necessary and add later
 */
public abstract class Menu {
    private static final String SIGN_ARROWS = "^^^^^^^^^^^^^^^";

    private static final HashMap<Player, Menu> menus = new HashMap<>();

    protected final Inventory inventory;
    protected final Player player;
    protected Button[][] buttons;
    protected BukkitTask task;
    private boolean opened = false;

    public Menu(Player player, int size, String title){
        this.player = player;
        inventory = Bukkit.createInventory(null, size, title);
        buttons = new Button[inventory.getSize() / 9][9];

    }

    public void openMenu() {
        if (!opened) {
            initializeItems();
            opened = true;
        }
        updateMenu();
        player.openInventory(inventory);
        menus.put(player, this);
    }

    public void updateMenu() {
        ItemStack[] stack = new ItemStack[buttons.length * buttons[0].length];

        for (int r = 0; r < buttons.length; r++)
            for (int c = 0; c <  9; c++)
                if (buttons[r][c] != null)
                    stack[r * 9 + c] = buttons[r][c].getItemStack();
                else
                    stack[r * 9 + c] = null;

        inventory.setContents(stack);
    }

    // UTILITIES

    public void animateTitle(Player p, String title) {
        ServerPlayer ep = ((CraftPlayer)p).getHandle();
        ClientboundOpenScreenPacket packet = new ClientboundOpenScreenPacket(ep.containerMenu.containerId, MenuType.GENERIC_9x6, Component.Serializer.fromJson("{\"text\": \"" + title + "\"}"));
        ep.connection.send(packet);
        p.updateInventory();
    }

    public void getFromSign(String line3, String line4, BiPredicate<Player, String[]> response) {
        AetherLib.getPlugin(AetherLib.class).getSignMenuFactory().newMenu(Arrays.asList("", SIGN_ARROWS, line3, line4))
                .reopenIfFail(true).response(response).open(player);
    }

    public void setPermission(UUID userUuid, String permission, boolean value) {
        LuckPermsProvider.get().getUserManager().modifyUser(userUuid, user -> {
            PermissionNode node = PermissionNode.builder(permission).value(value).build();
            user.data().add(node);
        }).whenComplete((s, error) -> updateItems());
    }

    // INVENTORY MODIFIERS

    public void addButtonFirstEmpty(Button button) {
        updateMenu();
        addButton1D(inventory.firstEmpty(), button);
    }

    public void addButton1D(int pos, Button button) {
        buttons[pos / 9][pos % 9] = button;
    }

    public void addFillerRows(Material material, int... rows) {
        for (int r : rows)
            for (int c = 0; c < 9; c++)
                if (buttons[r][c] == null) buttons[r][c] = Button.of(material).name(" ");
    }

    public void addFillerColumns(Material material, int... cols) {
        for (int r = 0; r < buttons.length; r++)
            for (int c : cols)
                if (buttons[r][c] == null) buttons[r][c] = Button.of(material).name(" ");
    }

    public void addFiller(Material material, int r1, int r2, int c1, int c2) {
        for (int r = r1; r <= r2; r++)
            for (int c = c1; c <= c2; c++)
                if (buttons[r][c] == null) buttons[r][c] = Button.of(material).name(" ");
    }

    public void removeItems(int r1, int r2, int c1, int c2) {
        for (int r = r1; r <= r2; r++)
            for (int c = c1; c <= c2; c++) {
                buttons[r][c] = null;
                inventory.clear(r * 9  + c);
            }
    }

    // HANDLERS

    public void handleClick(InventoryClickEvent e) {
        final int slot = e.getSlot();
        final Button button = getButtons()[slot / 9][slot % 9];

        if (button != null)
            button.handleClick(e);

        e.setCancelled(true);
    }

    public void handleClose() {}

    // IMPLEMENTATIONS

    public abstract void initializeItems();
    public abstract void updateItems();

    // BOILERPLATE CODE

    public Inventory getInventory() {
        return inventory;
    }
    public Button[][] getButtons() {
        return buttons;
    }

    public static Menu getMenu(Player p) {
        return menus.get(p);
    }
    public static void eraseMenu(Player player) {
        menus.remove(player);
    }

    protected abstract class AetherRunnable extends BukkitRunnable {
        protected Button button;
        public synchronized @NotNull BukkitTask runTaskTimer(@NotNull Plugin plugin, long delay, long period, Button button) throws IllegalArgumentException, IllegalStateException {
            if (task != null) {
                task.cancel();
            }
            this.button = button;
            task = super.runTaskTimer(plugin, delay, period);
            return task;
        }
    }
}
