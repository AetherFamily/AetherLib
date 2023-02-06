package com.github.aetherfamily.aetherlib;

import com.github.aetherfamily.aetherlib.utilities.SignMenuFactory;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class AetherLib extends JavaPlugin {
    private SignMenuFactory signMenuFactory;

    @Override
    public void onEnable() {
        signMenuFactory = new SignMenuFactory(this);

        int pluginId = 17648;
        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {

    }

    public SignMenuFactory getSignMenuFactory() {
        return signMenuFactory;
    }
}
