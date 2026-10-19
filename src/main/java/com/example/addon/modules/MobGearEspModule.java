package com.example.addon.modules;

import autismclient.modules.Module;
import com.example.addon.AddonCategories;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Notifies when a nearby non-player mob is wearing equipment. */
public final class MobGearEspModule extends Module {
    private final Set<UUID> seen = new HashSet<>();
    private int scanDelay;

    public MobGearEspModule() {
        super("damoko-addon:mob-gear-esp", "Mob Gear ESP", AddonCategories.TROUSER_STREAK,
            "Notifies locally when a nearby mob is wearing armor or other equipment.");
    }

    @Override public void onEnable() { seen.clear(); scanDelay = 0; }

    @Override
    public void tick() {
        if (MC.player == null || MC.level == null) { seen.clear(); scanDelay = 0; return; }
        if (++scanDelay < 20) return;
        scanDelay = 0;
        Set<UUID> current = new HashSet<>();
        for (var entity : MC.level.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity mob) || mob instanceof Player || !mob.isAlive()
                || MC.player.distanceTo(mob) > 32.0f) continue;
            boolean equipped = !mob.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
                || !mob.getItemBySlot(EquipmentSlot.CHEST).isEmpty()
                || !mob.getItemBySlot(EquipmentSlot.LEGS).isEmpty()
                || !mob.getItemBySlot(EquipmentSlot.FEET).isEmpty();
            if (!equipped) continue;
            current.add(mob.getUUID());
            if (!seen.contains(mob.getUUID())) {
                MC.player.sendSystemMessage(Component.literal("Equipped mob nearby: " + mob.getName().getString()));
            }
        }
        seen.clear();
        seen.addAll(current);
    }
}
