package com.example.addon.modules;

import autismclient.api.module.DoubleSetting;
import autismclient.api.module.IntSetting;
import autismclient.modules.Module;
import autismclient.util.AutismInventoryHelper;
import com.example.addon.AddonCategories;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

/** Uses golden apples when health falls below the configured threshold. */
public final class AutoGapModule extends Module {
    private final DoubleSetting health = add(new DoubleSetting("health", "Health threshold", 12.0, 1.0, 20.0, 0.5));
    private final IntSetting delay = add(new IntSetting("delay", "Use delay", 40, 1, 200, 1).unit("ticks"));
    private int cooldown;

    public AutoGapModule() {
        super("damoko-addon:auto-gap", "Auto Gap", AddonCategories.DAMOKO,
            "Automatically eats golden apples when health is low.");
    }

    @Override public void onEnable() { cooldown = 0; }

    @Override
    public void tick() {
        if (MC.player == null || MC.gameMode == null) return;
        if (cooldown > 0) { cooldown--; return; }
        if (MC.player.getHealth() > health.get()) return;
        if (MC.player.getOffhandItem().is(Items.GOLDEN_APPLE) || MC.player.getOffhandItem().is(Items.ENCHANTED_GOLDEN_APPLE)) {
            MC.gameMode.useItem(MC.player, InteractionHand.OFF_HAND);
            cooldown = delay.get();
            return;
        }
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            var stack = MC.player.getInventory().getItem(i);
            if (stack.is(Items.ENCHANTED_GOLDEN_APPLE)) { slot = i; break; }
            if (slot < 0 && stack.is(Items.GOLDEN_APPLE)) slot = i;
        }
        if (slot >= 0) {
            AutismInventoryHelper.selectHotbarSlot(MC, slot);
            MC.gameMode.useItem(MC.player, InteractionHand.MAIN_HAND);
            cooldown = delay.get();
        }
    }
}
