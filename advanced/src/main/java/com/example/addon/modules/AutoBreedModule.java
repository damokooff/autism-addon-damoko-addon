package com.example.addon.modules;

import autismclient.api.module.IntSetting;
import autismclient.modules.Module;
import com.example.addon.AddonCategories;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.EntityHitResult;

/** Feeds an adult animal under the crosshair with its accepted breeding item. */
public final class AutoBreedModule extends Module {
    private final IntSetting delay = add(new IntSetting("delay", "Feed delay", 12, 1, 80, 1).unit("ticks"));
    private int cooldown;

    public AutoBreedModule() {
        super("damoko-addon:auto-breed", "Auto Breed", AddonCategories.DAMOKO,
            "Feeds breedable animals when you aim at them with a matching food item.");
    }

    @Override public void onEnable() { cooldown = 0; }

    @Override
    public void tick() {
        if (cooldown > 0) { cooldown--; return; }
        if (MC.player == null || MC.gameMode == null || !(MC.hitResult instanceof EntityHitResult hit)
            || !(hit.getEntity() instanceof Animal animal) || animal.isBaby() || !animal.canFallInLove()
            || !animal.isFood(MC.player.getMainHandItem())) return;
        MC.gameMode.interact(MC.player, animal, hit, InteractionHand.MAIN_HAND);
        cooldown = delay.get();
    }
}
