package com.example.addon.modules;

import autismclient.api.module.IntSetting;
import autismclient.modules.Module;
import com.example.addon.AddonCategories;

/** Periodically makes a small downward movement while gliding. */
public final class FlightAntikickModule extends Module {
    private final IntSetting delay = add(new IntSetting("delay", "Delay", 20, 1, 60, 1).unit("ticks"));
    private final IntSetting duration = add(new IntSetting("duration", "Movement duration", 3, 1, 20, 1).unit("ticks"));
    private int ticks;
    private int descendTicks;

    public FlightAntikickModule() {
        super("damoko-addon:flight-antikick", "Flight Anti Kick", AddonCategories.TROUSER_STREAK,
            "Adds a brief, small descent periodically while gliding.");
    }

    @Override public void onEnable() { ticks = 0; descendTicks = 0; }

    @Override
    public void tick() {
        if (MC.player == null || !MC.player.isFallFlying()) { ticks = 0; descendTicks = 0; return; }
        if (descendTicks > 0) {
            var velocity = MC.player.getDeltaMovement();
            MC.player.setDeltaMovement(velocity.x, Math.min(velocity.y, -0.1), velocity.z);
            descendTicks--;
        } else if (++ticks >= delay.get()) {
            ticks = 0;
            descendTicks = duration.get();
        }
    }
}
