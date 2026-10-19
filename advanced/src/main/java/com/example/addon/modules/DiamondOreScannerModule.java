package com.example.addon.modules;

import net.minecraft.world.level.block.Blocks;

public final class DiamondOreScannerModule extends NearbyBlockScannerModule {
    public DiamondOreScannerModule() { super("diamond-ore-scanner", "Diamond Ore Scanner", "Alerts locally when diamond ore is nearby.", Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE); }
    @Override protected String displayName() { return "Diamond ore"; }
}
