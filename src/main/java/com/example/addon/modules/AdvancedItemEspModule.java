package com.example.addon.modules;

import autismclient.api.module.StringSetting;
import autismclient.modules.Module;
import com.example.addon.AddonCategories;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Local alerts for configured dropped item types. */
public final class AdvancedItemEspModule extends Module {
    private final StringSetting items = add(new StringSetting("items", "Item IDs (comma separated)",
        "minecraft:elytra,minecraft:totem_of_undying,minecraft:diamond"));
    private final Set<UUID> seen = new HashSet<>();
    private int scanDelay;

    public AdvancedItemEspModule() {
        super("damoko-addon:advanced-item-esp", "Advanced Item ESP", AddonCategories.TROUSER_STREAK,
            "Notifies locally about nearby dropped items matching the configured item IDs.");
    }

    @Override public void onEnable() { seen.clear(); scanDelay = 0; }

    @Override
    public void tick() {
        if (MC.player == null || MC.level == null) { seen.clear(); scanDelay = 0; return; }
        if (++scanDelay < 20) return;
        scanDelay = 0;
        Set<UUID> current = new HashSet<>();
        for (var entity : MC.level.entitiesForRendering()) {
            if (!(entity instanceof ItemEntity item) || MC.player.distanceTo(item) > 32.0f) continue;
            var id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item.getItem().getItem()).toString();
            if (!matches(id)) continue;
            current.add(item.getUUID());
            if (!seen.contains(item.getUUID())) {
                MC.player.sendSystemMessage(Component.literal("Item nearby: " + id + " at "
                    + item.blockPosition().getX() + ", " + item.blockPosition().getY() + ", " + item.blockPosition().getZ()));
            }
        }
        seen.clear();
        seen.addAll(current);
    }

    private boolean matches(String id) {
        for (String configured : items.get().split(",")) if (id.equalsIgnoreCase(configured.trim())) return true;
        return false;
    }
}
