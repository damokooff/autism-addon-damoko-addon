package com.example.addon.modules;

import net.minecraft.world.level.block.Blocks;

public final class EnderChestScannerModule extends NearbyBlockScannerModule {
    public EnderChestScannerModule() { super("ender-chest-scanner", "Ender Chest Scanner", "Alerts locally when an ender chest is nearby.", Blocks.ENDER_CHEST); }
    @Override protected String displayName() { return "Ender chest"; }
}
