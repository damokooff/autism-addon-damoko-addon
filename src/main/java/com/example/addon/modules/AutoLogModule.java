package com.example.addon.modules;

import autismclient.api.module.DoubleSetting;
import autismclient.modules.Module;
import com.example.addon.AddonCategories;
import net.minecraft.network.chat.Component;

/** Disconnects automatically when health reaches a configured danger level. */
public final class AutoLogModule extends Module {
    private final DoubleSetting health = add(new DoubleSetting("health", "Disconnect below health", 4.0, 1.0, 20.0, 0.5));

    public AutoLogModule() {
        super("damoko-addon:auto-log", "Auto Log", AddonCategories.DAMOKO,
            "Disconnects from the server at critical health.");
    }

    @Override
    public void tick() {
        if (MC.player != null && MC.getConnection() != null && MC.player.getHealth() <= health.get()) {
            MC.getConnection().getConnection().disconnect(Component.literal("Auto Log: critical health"));
        }
    }
}
