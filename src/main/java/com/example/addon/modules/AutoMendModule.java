package com.example.addon.modules;

import autismclient.api.module.BoolSetting;
import autismclient.modules.Module;
import autismclient.util.AutismInventoryHelper;
import com.example.addon.AddonCategories;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

/** Meteor-inspired Mending rotation: puts damaged Mending gear in the offhand for XP repair. */
public final class AutoMendModule extends Module {
    private final BoolSetting force = add(new BoolSetting("force", "Replace offhand item", false)
        .description("Move the current offhand item aside when it is not a damaged Mending item."));
    private final BoolSetting autoDisable = add(new BoolSetting("auto-disable", "Disable when repaired", true));
    private boolean movedItem;

    public AutoMendModule() {
        super("damoko-addon:auto-mend", "Auto Mend", AddonCategories.DAMOKO,
            "Cycles damaged Mending items through the offhand so experience repairs them.");
    }

    @Override
    public void onEnable() {
        movedItem = false;
    }

    @Override
    public void tick() {
        if (MC.player == null || MC.level == null || MC.gameMode == null) return;
        var offhand = MC.player.getOffhandItem();
        if (!offhand.isEmpty() && hasMending(offhand) && offhand.isDamaged()) return;
        if (!offhand.isEmpty() && !hasMending(offhand) && !force.get()) return;

        int repairSlot = findDamagedMendingItem();
        if (repairSlot >= 0) {
            if (AutismInventoryHelper.swapInventorySlots(MC, repairSlot, 40)) movedItem = true;
            return;
        }

        if (autoDisable.get()) {
            if (movedItem && !MC.player.getOffhandItem().isEmpty()) {
                int empty = findEmptySlot();
                if (empty >= 0) AutismInventoryHelper.swapInventorySlots(MC, empty, 40);
            }
            setEnabled(false);
        }
    }

    private int findDamagedMendingItem() {
        for (int slot = 0; slot < 36; slot++) {
            var stack = MC.player.getInventory().getItem(slot);
            if (!stack.isEmpty() && stack.isDamaged() && hasMending(stack)) return slot;
        }
        return -1;
    }

    private int findEmptySlot() {
        for (int slot = 0; slot < 36; slot++) {
            if (MC.player.getInventory().getItem(slot).isEmpty()) return slot;
        }
        return -1;
    }

    private boolean hasMending(net.minecraft.world.item.ItemStack stack) {
        try {
            var holder = MC.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.MENDING);
            return EnchantmentHelper.getItemEnchantmentLevel(holder, stack) > 0;
        } catch (RuntimeException ignored) {
            return false;
        }
    }
}
