package com.example.addon.modules;

import autismclient.api.module.IntSetting;
import autismclient.api.module.StringSetting;
import autismclient.modules.Module;
import com.example.addon.AddonCategories;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;

/** Applies a configured display name to a living target using a name tag. */
public final class AutoNametagModule extends Module {
    private final StringSetting tagName = add(new StringSetting("name", "Name", "Damoko"));
    private final IntSetting delay = add(new IntSetting("delay", "Use delay", 20, 1, 100, 1).unit("ticks"));
    private int cooldown;

    public AutoNametagModule() {
        super("damoko-addon:auto-nametag", "Auto Nametag", AddonCategories.DAMOKO,
            "Names the living entity under your crosshair when holding a name tag.");
    }

    @Override public void onEnable() { cooldown = 0; }

    @Override
    public void tick() {
        if (cooldown > 0) { cooldown--; return; }
        if (MC.player == null || MC.gameMode == null || tagName.get().isBlank()
            || !(MC.hitResult instanceof EntityHitResult hit) || !hit.getEntity().isAlive()) return;
        var hand = MC.player.getMainHandItem().is(Items.NAME_TAG) ? InteractionHand.MAIN_HAND
            : MC.player.getOffhandItem().is(Items.NAME_TAG) ? InteractionHand.OFF_HAND : null;
        if (hand == null) return;
        var stack = hand == InteractionHand.MAIN_HAND ? MC.player.getMainHandItem() : MC.player.getOffhandItem();
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(tagName.get()));
        MC.gameMode.interact(MC.player, hit.getEntity(), hit, hand);
        cooldown = delay.get();
    }
}
