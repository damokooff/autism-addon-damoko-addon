package com.example.addon.modules;

import autismclient.api.module.DoubleSetting;
import autismclient.api.module.StringSetting;
import autismclient.modules.Module;
import com.example.addon.AddonCategories;
import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Local alert when a watched player enters the configured distance. */
public final class PlayerAlarmModule extends Module {
    private final StringSetting names = add(new StringSetting("names", "Watched names (comma separated)", ""));
    private final DoubleSetting range = add(new DoubleSetting("range", "Detection range", 48.0, 4.0, 128.0, 4.0));
    private final Set<UUID> nearby = new HashSet<>();

    public PlayerAlarmModule() {
        super("damoko-addon:player-alarm", "Player Alarm", AddonCategories.TROUSER_STREAK,
            "Shows a local alert when a watched player comes within range. Leave the name list empty to watch everyone.");
    }

    @Override public void onEnable() { updateNearby(false); }

    @Override public void tick() { updateNearby(true); }

    private void updateNearby(boolean notify) {
        if (MC.player == null || MC.level == null) { nearby.clear(); return; }
        Set<UUID> current = new HashSet<>();
        for (var player : MC.level.players()) {
            if (player == MC.player || MC.player.distanceTo(player) > range.get() || !isWatched(player.getName().getString())) continue;
            UUID id = player.getUUID();
            current.add(id);
            if (notify && !nearby.contains(id)) {
                MC.player.sendSystemMessage(Component.literal("Player nearby: " + player.getName().getString()));
            }
        }
        nearby.clear();
        nearby.addAll(current);
    }

    private boolean isWatched(String name) {
        String list = names.get().trim();
        if (list.isEmpty()) return true;
        for (String watched : list.split(",")) {
            if (name.equalsIgnoreCase(watched.trim())) return true;
        }
        return false;
    }
}
