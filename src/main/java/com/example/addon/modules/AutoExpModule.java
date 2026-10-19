package com.example.addon.modules;

import autismclient.api.module.IntSetting;
import autismclient.modules.Module;
import autismclient.util.AutismInventoryHelper;
import com.example.addon.AddonCategories;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

/** Automatically throws XP bottles while damaged Mending equipment needs repair. */
public final class AutoExpModule extends Module {
    private final IntSetting delay = add(new IntSetting("delay", "Throw delay", 6, 1, 40, 1).unit("ticks"));
    private int cooldown;

    public AutoExpModule() {
        super("damoko-addon:auto-exp", "Auto EXP", AddonCategories.DAMOKO,
            "Throws experience bottles while you carry damaged Mending gear.");
    }

    @Override public void onEnable() { cooldown = 0; }

    @Override
    public void tick() {
        if (MC.player == null || MC.level == null || MC.gameMode == null) return;
        if (cooldown > 0) { cooldown--; return; }
        if (!hasDamagedMendingGear()) return;
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            if (MC.player.getInventory().getItem(i).is(Items.EXPERIENCE_BOTTLE)) { slot = i; break; }
        }
        if (slot < 0) return;
        AutismInventoryHelper.selectHotbarSlot(MC, slot);
        MC.gameMode.useItem(MC.player, InteractionHand.MAIN_HAND);
        cooldown = delay.get();
    }

    private boolean hasDamagedMendingGear() {
        try {
            var enchantment = MC.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.MENDING);
            for (int slot = 0; slot < 36; slot++) {
                var stack = MC.player.getInventory().getItem(slot);
                if (stack.isDamaged() && EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack) > 0) return true;
            }
            for (var slot : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                var equipment = MC.player.getItemBySlot(slot);
                if (equipment.isDamaged() && EnchantmentHelper.getItemEnchantmentLevel(enchantment, equipment) > 0) return true;
            }
        } catch (RuntimeException ignored) { }
        return false;
    }
}
