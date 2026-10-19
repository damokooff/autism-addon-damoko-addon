package com.example.addon.modules;

import autismclient.api.module.BoolSetting;
import autismclient.modules.Module;
import com.example.addon.AddonCategories;
import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Notifies locally when players join or leave the current world. */
public final class PlayerActivityModule extends Module {
    private final BoolSetting showJoins = add(new BoolSetting("joins", "Show joins", true));
    private final BoolSetting showLeaves = add(new BoolSetting("leaves", "Show leaves", true));
    private final Set<UUID> knownPlayers = new HashSet<>();

    public PlayerActivityModule() {
        super("damoko-addon:player-activity", "Player Activity", AddonCategories.TROUSER_STREAK,
            "Shows local join and leave notifications for players in the current world.");
    }

    @Override public void onEnable() { knownPlayers.clear(); }

    @Override
    public void tick() {
        if (MC.player == null || MC.level == null) { knownPlayers.clear(); return; }
        Set<UUID> current = new HashSet<>();
        MC.level.players().forEach(player -> {
            UUID id = player.getUUID();
            current.add(id);
            if (!knownPlayers.contains(id) && !player.getUUID().equals(MC.player.getUUID()) && showJoins.get()) {
                MC.player.sendSystemMessage(Component.literal(player.getName().getString() + " joined"));
            }
        });
        if (showLeaves.get()) {
            knownPlayers.stream().filter(id -> !current.contains(id) && !id.equals(MC.player.getUUID()))
                .forEach(id -> MC.player.sendSystemMessage(Component.literal("A player left")));
        }
        knownPlayers.clear();
        knownPlayers.addAll(current);
    }
}
