package com.example.addon.hud;

import autismclient.api.hud.HudElementProvider;
import com.example.addon.ExampleAddon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.Items;

/** Shows the number of elytras carried and the durability of the equipped one. */
public final class ElytraCountHud implements HudElementProvider {
    @Override public String id() { return ExampleAddon.ID + ":elytra-count"; }
    @Override public String label() { return "Elytra Count"; }
    @Override public String description() { return "Shows carried elytras and equipped elytra durability."; }
    @Override public int width() { return 128; }
    @Override public int height() { return 10; }

    @Override
    public void render(GuiGraphicsExtractor ctx, Font font, int x, int y, float alpha) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        int count = 0;
        for (var stack : player.getInventory().getNonEquipmentItems()) {
            if (stack.is(Items.ELYTRA)) count += stack.getCount();
        }
        var chest = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
        String equipped = chest.is(Items.ELYTRA)
            ? "  worn " + Math.max(0, chest.getMaxDamage() - chest.getDamageValue()) : "";
        ctx.text(font, "Elytras: " + count + equipped, x + 2, y, 0xFFFFFFFF);
    }

    @Override public boolean defaultEnabled() { return false; }
    @Override public String defaultAnchor() { return "TOP_LEFT"; }
    @Override public int defaultX() { return 4; }
    @Override public int defaultY() { return 152; }
}
