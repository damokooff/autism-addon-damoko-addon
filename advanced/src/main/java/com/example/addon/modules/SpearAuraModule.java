package com.example.addon.modules;

import autismclient.api.module.DoubleSetting;
import autismclient.modules.Module;
import com.example.addon.AddonCategories;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;

/** Spear-focused aura with target and reach checks. */
public final class SpearAuraModule extends Module {
    private final DoubleSetting range = add(new DoubleSetting("range", "Attack range", 5.0, 1.0, 7.0, 0.1));

    public SpearAuraModule() {
        super("damoko-addon:spear-aura", "Spear Aura", AddonCategories.DAMOKO,
            "Attacks the living entity under your crosshair while holding a spear.");
    }

    @Override
    public void tick() {
        var spear = BuiltInRegistries.ITEM.get(Identifier.parse("minecraft:spear"));
        if (spear.isEmpty() || MC.player == null || MC.gameMode == null || !MC.player.getMainHandItem().is(spear.get())
            || !(MC.hitResult instanceof EntityHitResult hit) || !(hit.getEntity() instanceof LivingEntity target)
            || !target.isAlive() || MC.player.distanceTo(target) > range.get()
            || MC.player.getAttackStrengthScale(0.0f) < 1.0f) return;
        MC.gameMode.attack(MC.player, target);
        MC.player.swing(InteractionHand.MAIN_HAND);
    }
}
