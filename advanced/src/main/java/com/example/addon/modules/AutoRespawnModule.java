package com.example.addon.modules;

import autismclient.api.module.IntSetting;
import autismclient.modules.Module;
import com.example.addon.AddonCategories;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;

/** Automatically requests respawn after death. */
public final class AutoRespawnModule extends Module {
    private final IntSetting delay = add(new IntSetting("delay", "Respawn delay", 20, 0, 200, 1).unit("ticks"));
    private int timer;
    private boolean requested;

    public AutoRespawnModule() {
        super("damoko-addon:auto-respawn", "Auto Respawn", AddonCategories.DAMOKO,
            "Automatically respawns after the configured delay.");
    }

    @Override public void onEnable() { timer = 0; requested = false; }

    @Override
    public void tick() {
        if (MC.player == null || MC.getConnection() == null || !MC.player.isDeadOrDying()) {
            timer = 0;
            requested = false;
            return;
        }
        if (requested) return;
        if (timer++ >= delay.get()) {
            MC.getConnection().send(new ServerboundClientCommandPacket(
                ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
            requested = true;
        }
    }
}
