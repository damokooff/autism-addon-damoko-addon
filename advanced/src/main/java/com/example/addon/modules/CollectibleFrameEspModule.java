package com.example.addon.modules;

import autismclient.modules.Module;
import com.example.addon.AddonCategories;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ItemFrame;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Notifies when a nearby item frame displays an item. */
public final class CollectibleFrameEspModule extends Module {
    private final Set<UUID> seen = new HashSet<>();
    private int scanDelay;

    public CollectibleFrameEspModule() {
        super("damoko-addon:collectible-esp", "Collectible ESP", AddonCategories.TROUSER_STREAK,
            "Notifies locally about nearby item frames containing displayed items.");
    }

    @Override public void onEnable() { seen.clear(); scanDelay = 0; }

    @Override
    public void tick() {
        if (MC.player == null || MC.level == null) { seen.clear(); scanDelay = 0; return; }
        if (++scanDelay < 20) return;
        scanDelay = 0;
        Set<UUID> current = new HashSet<>();
        for (var entity : MC.level.entitiesForRendering()) {
            if (!(entity instanceof ItemFrame frame) || frame.getItem().isEmpty()
                || MC.player.distanceTo(frame) > 32.0f) continue;
            current.add(frame.getUUID());
            if (!seen.contains(frame.getUUID())) {
                MC.player.sendSystemMessage(Component.literal("Displayed item nearby: "
                    + frame.getItem().getHoverName().getString() + " at " + frame.blockPosition().getX()
                    + ", " + frame.blockPosition().getY() + ", " + frame.blockPosition().getZ()));
            }
        }
        seen.clear();
        seen.addAll(current);
    }
}
