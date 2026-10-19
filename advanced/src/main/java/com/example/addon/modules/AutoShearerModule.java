package com.example.addon.modules;

import autismclient.api.module.IntSetting;
import autismclient.modules.Module;
import com.example.addon.AddonCategories;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;

/** Shears a ready sheep under the crosshair. */
public final class AutoShearerModule extends Module {
    private final IntSetting delay = add(new IntSetting("delay", "Shear delay", 10, 1, 80, 1).unit("ticks"));
    private int cooldown;

    public AutoShearerModule() {
        super("damoko-addon:auto-shearer", "Auto Shearer", AddonCategories.DAMOKO,
            "Shears an unsheared adult sheep when you aim at it.");
    }

    @Override public void onEnable() { cooldown = 0; }

    @Override
    public void tick() {
        if (cooldown > 0) { cooldown--; return; }
        if (MC.player == null || MC.gameMode == null || !(MC.hitResult instanceof EntityHitResult hit)
            || !(hit.getEntity() instanceof Sheep sheep) || sheep.isBaby() || sheep.isSheared()
            || !MC.player.getMainHandItem().is(Items.SHEARS)) return;
        MC.gameMode.interact(MC.player, sheep, hit, InteractionHand.MAIN_HAND);
        cooldown = delay.get();
    }
}
