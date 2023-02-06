package com.github.aetherfamily.aetherlib.utilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class Msg {
    public static void send(Player p, String miniMessage) {
        p.sendMessage(MiniMessage.miniMessage().deserialize(miniMessage));
    }

    public static Component mini(String miniMessage) {
        return MiniMessage.miniMessage().deserialize(miniMessage);
    }
}
