package com.github.aetherfamily.aetherlib;

import com.github.aetherfamily.aetherlib.listeners.MenuListener;
import com.github.aetherfamily.aetherlib.utilities.SignMenuFactory;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AetherLib extends JavaPlugin {
    private SignMenuFactory signMenuFactory;

    @Override
    public void onEnable() {
        signMenuFactory = new SignMenuFactory(this);

        int pluginId = 17648;
        Metrics metrics = new Metrics(this, pluginId);
        Bukkit.getPluginManager().registerEvents(new MenuListener(), this);
    }

    @Override
    public void onDisable() {

    }

    public SignMenuFactory getSignMenuFactory() {
        return signMenuFactory;
    }
}
