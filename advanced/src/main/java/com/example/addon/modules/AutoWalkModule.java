package com.example.addon.modules;

import autismclient.api.module.BoolSetting;
import autismclient.modules.Module;
import com.example.addon.AddonCategories;

/** Small Meteor AutoWalk port. */
public final class AutoWalkModule extends Module {
    private final BoolSetting backwards = add(new BoolSetting("backwards", "Walk backwards", false));

    public AutoWalkModule() {
        super("damoko-addon:auto-walk", "Auto Walk", AddonCategories.METEOR,
            "Holds forward movement until disabled.");
    }

    @Override
    public void tick() {
        if (MC.options == null) return;
        MC.options.keyUp.setDown(!backwards.get());
        MC.options.keyDown.setDown(backwards.get());
    }

    @Override
    public void onDisable() {
        if (MC.options != null) {
            MC.options.keyUp.setDown(false);
            MC.options.keyDown.setDown(false);
        }
    }
}
