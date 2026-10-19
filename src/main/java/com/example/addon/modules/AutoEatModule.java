package com.example.addon.modules;

import autismclient.api.module.DoubleSetting;
import autismclient.api.module.IntSetting;
import autismclient.modules.Module;
import autismclient.util.AutismInventoryHelper;
import com.example.addon.AddonCategories;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;

/** Meteor-inspired automatic eating, with conservative hotbar-only item selection. */
public final class AutoEatModule extends Module {
    private final DoubleSetting healthThreshold = add(new DoubleSetting(
        "health-threshold", "Health threshold", 10.0, 1.0, 19.0, 0.5).group("Threshold"));
    private final IntSetting hungerThreshold = add(new IntSetting(
        "hunger-threshold", "Hunger threshold", 16, 1, 19, 1).group("Threshold"));
    private int previousSlot = -1;
    private int eatingSlot = -1;

    public AutoEatModule() {
        super("damoko-addon:auto-eat", "Auto Eat", AddonCategories.DAMOKO,
            "Automatically eats hotbar food when health or hunger is low.");
    }

    @Override
    public void onDisable() {
        restoreSlot();
    }

    @Override
    public void tick() {
        if (MC.player == null || MC.gameMode == null) return;
        boolean shouldEat = MC.player.getHealth() <= healthThreshold.get()
            || MC.player.getFoodData().getFoodLevel() <= hungerThreshold.get();
        if (!shouldEat || !MC.player.canEat(false)) {
            if (!MC.player.isUsingItem()) restoreSlot();
            return;
        }

        if (MC.player.getOffhandItem().has(DataComponents.FOOD)) {
            MC.gameMode.useItem(MC.player, InteractionHand.OFF_HAND);
            return;
        }
        int foodSlot = findFoodSlot();
        if (foodSlot < 0) return;
        if (eatingSlot != foodSlot) {
            if (eatingSlot < 0) previousSlot = MC.player.getInventory().getSelectedSlot();
            eatingSlot = foodSlot;
            AutismInventoryHelper.selectHotbarSlot(MC, foodSlot);
        }
        if (!MC.player.isUsingItem()) MC.gameMode.useItem(MC.player, InteractionHand.MAIN_HAND);
    }

    private int findFoodSlot() {
        int bestSlot = -1;
        int bestNutrition = -1;
        for (int slot = 0; slot < 9; slot++) {
            var stack = MC.player.getInventory().getItem(slot);
            var food = stack.get(DataComponents.FOOD);
            if (food == null) continue;
            int nutrition = food.nutrition();
            if (nutrition > bestNutrition) {
                bestNutrition = nutrition;
                bestSlot = slot;
            }
        }
        return bestSlot;
    }

    private void restoreSlot() {
        if (MC.player != null && previousSlot >= 0) AutismInventoryHelper.restoreHotbarSlot(MC, previousSlot);
        previousSlot = eatingSlot = -1;
    }
}
