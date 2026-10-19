package com.example.addon.modules;

import autismclient.api.module.DoubleSetting;
import autismclient.api.module.BoolSetting;
import autismclient.modules.Module;
import com.example.addon.AddonCategories;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;

/** Lunge toward the living target under the crosshair while attacking with a spear. */
public final class SpearKillModule extends Module {
    private final BoolSetting playersOnly = add(new BoolSetting("players-only", "Target players only", true));
    private final DoubleSetting velocity = add(new DoubleSetting("velocity", "Spear velocity", 5.0, 0.0, 10.0, 0.1));
    private final DoubleSetting maxRange = add(new DoubleSetting("range", "Target range", 32.0, 1.0, 64.0, 1.0));
    private int spearTicks;
    private LivingEntity lockedTarget;

    public SpearKillModule() {
        super("damoko-addon:spear-kill", "Spear Kill", AddonCategories.TROUSER_STREAK,
            "Lunges toward the living target under your crosshair while holding a spear and attacking.");
    }

    @Override
    public void tick() {
        if (MC.player == null || MC.level == null || MC.options == null
            || !MC.options.keyAttack.isDown() || !holdingSpear()) {
            resetTarget();
            return;
        }

        if (lockedTarget == null || !lockedTarget.isAlive() || MC.player.distanceTo(lockedTarget) > maxRange.get()) {
            lockedTarget = findTarget();
            spearTicks = 0;
        }
        if (lockedTarget == null) return;

        spearTicks++;
        Vec3 towardTarget = lockedTarget.getBoundingBox().getCenter().subtract(MC.player.getEyePosition()).normalize();
        float yaw = (float) (Math.toDegrees(Math.atan2(towardTarget.z, towardTarget.x)) - 90.0);
        float pitch = (float) -Math.toDegrees(Math.asin(towardTarget.y));
        MC.player.setYRot(yaw);
        MC.player.setYHeadRot(yaw);
        MC.player.setXRot(pitch);
        if (spearTicks >= 10) MC.player.setDeltaMovement(MC.player.getLookAngle().scale(velocity.get()));
    }

    private LivingEntity findTarget() {
        Vec3 look = MC.player.getLookAngle();
        AABB search = MC.player.getBoundingBox().expandTowards(look.scale(maxRange.get())).inflate(1.0);
        return MC.level.getEntitiesOfClass(LivingEntity.class, search,
                entity -> entity != MC.player && entity.isAlive()
                    && (!playersOnly.get() || entity instanceof net.minecraft.world.entity.player.Player)
                    && MC.player.distanceTo(entity) <= maxRange.get())
            .stream()
            .filter(entity -> {
                Vec3 toEntity = entity.getBoundingBox().getCenter().subtract(MC.player.getEyePosition()).normalize();
                return look.dot(toEntity) > 0.999;
            })
            .min(Comparator.comparingDouble(MC.player::distanceTo))
            .orElse(null);
    }

    private void resetTarget() { spearTicks = 0; lockedTarget = null; }

    private boolean holdingSpear() {
        return isSpear(MC.player.getMainHandItem()) || isSpear(MC.player.getOffhandItem());
    }

    private boolean isSpear(net.minecraft.world.item.ItemStack stack) {
        var id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id != null && id.getPath().contains("spear");
    }
}
