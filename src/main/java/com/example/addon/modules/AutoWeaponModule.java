package com.example.addon.modules;

import autismclient.modules.Module;
import autismclient.util.AutismInventoryHelper;
import com.example.addon.AddonCategories;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;

/** Selects a strong sword when a living target is under the crosshair. */
public final class AutoWeaponModule extends Module {
    public AutoWeaponModule() {
        super("damoko-addon:auto-weapon", "Auto Weapon", AddonCategories.DAMOKO,
            "Selects your best hotbar sword while aiming at a living target.");
    }

    @Override
    public void tick() {
        if (MC.player == null || !(MC.hitResult instanceof EntityHitResult hit)
            || !(hit.getEntity() instanceof LivingEntity target) || !target.isAlive()) return;
        int best = -1;
        int bestScore = -1;
        for (int slot = 0; slot < 9; slot++) {
            var item = MC.player.getInventory().getItem(slot).getItem();
            int score = weaponScore(item);
            if (score > bestScore) { bestScore = score; best = slot; }
        }
        if (best >= 0) AutismInventoryHelper.selectHotbarSlot(MC, best);
    }

    private int weaponScore(net.minecraft.world.item.Item item) {
        if (item == Items.NETHERITE_SWORD) return 7;
        if (item == Items.DIAMOND_SWORD) return 6;
        if (item == Items.IRON_SWORD) return 5;
        if (item == Items.STONE_SWORD) return 4;
        if (item == Items.GOLDEN_SWORD) return 3;
        if (item == Items.WOODEN_SWORD) return 2;
        if (item == Items.MACE) return 1;
        return -1;
    }
}
