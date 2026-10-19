package com.example.addon.modules;

import autismclient.api.module.DoubleSetting;
import autismclient.modules.Module;
import com.example.addon.AddonCategories;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;

/** Mace-focused crosshair aura. */
public final class MaceAuraModule extends Module {
    private final DoubleSetting range = add(new DoubleSetting("range", "Attack range", 4.5, 1.0, 6.0, 0.1));

    public MaceAuraModule() {
        super("damoko-addon:mace-aura", "Mace Aura", AddonCategories.DAMOKO,
            "Attacks the living entity under your crosshair while holding a mace.");
    }

    @Override
    public void tick() {
        if (MC.player == null || MC.gameMode == null || !MC.player.getMainHandItem().is(Items.MACE)
            || !(MC.hitResult instanceof EntityHitResult hit) || !(hit.getEntity() instanceof LivingEntity target)
            || !target.isAlive() || MC.player.distanceTo(target) > range.get()
            || MC.player.getAttackStrengthScale(0.0f) < 1.0f) return;
        MC.gameMode.attack(MC.player, target);
        MC.player.swing(InteractionHand.MAIN_HAND);
    }
}
