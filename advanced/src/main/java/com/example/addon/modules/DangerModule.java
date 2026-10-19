package com.example.addon.modules;

import autismclient.api.module.DoubleSetting;
import autismclient.modules.Module;
import com.example.addon.AddonCategories;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

/** Warns about nearby projectiles moving toward the player and swings at aimed fireballs. */
public final class DangerModule extends Module {
    private final DoubleSetting range = add(new DoubleSetting("range", "Detection range", 16.0, 4.0, 48.0, 1.0));
    private final DoubleSetting alertDistance = add(new DoubleSetting("alert-distance", "Alert distance", 10.0, 2.0, 24.0, 1.0));
    private final Set<Integer> alerted = new HashSet<>();
    private int messageCooldown;

    public DangerModule() {
        super("damoko-addon:danger", "Danger", AddonCategories.DAMOKO,
            "Alerts when a projectile is closing in and tries to hit a fireball you are aiming at.");
    }

    @Override public void onEnable() { alerted.clear(); messageCooldown = 0; }
    @Override public void onGameLeft() { alerted.clear(); messageCooldown = 0; }

    @Override
    public void tick() {
        if (MC.player == null || MC.level == null) return;
        if (messageCooldown > 0) messageCooldown--;
        var nearby = MC.level.getEntitiesOfClass(Projectile.class, MC.player.getBoundingBox().inflate(range.get()));
        for (Projectile projectile : nearby) {
            Vec3 toPlayer = MC.player.position().subtract(projectile.position());
            double distance = toPlayer.length();
            if (distance > alertDistance.get() || distance < 0.001) continue;
            Vec3 velocity = projectile.getDeltaMovement();
            if (velocity.lengthSqr() < 0.0001 || velocity.normalize().dot(toPlayer.normalize()) < 0.82) continue;
            if (alerted.add(projectile.getId()) && messageCooldown == 0) {
                MC.gui.hud.setOverlayMessage(Component.literal("Â§cDanger: projectile incoming"), false);
                messageCooldown = 20;
            }
        }
        alerted.removeIf(id -> MC.level.getEntity(id) == null);

        if (MC.gameMode != null && MC.hitResult instanceof EntityHitResult hit
            && hit.getEntity() instanceof Projectile projectile
            && BuiltInRegistries.ENTITY_TYPE.getKey(projectile.getType()).getPath().contains("fireball")
            && MC.player.distanceTo(projectile) <= 4.0) {
            MC.gameMode.attack(MC.player, projectile);
            MC.player.swing(InteractionHand.MAIN_HAND);
        }
    }
}
