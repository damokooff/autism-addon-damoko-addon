package com.example.addon.modules;

import net.minecraft.world.level.block.Blocks;

public final class AncientDebrisScannerModule extends NearbyBlockScannerModule {
    public AncientDebrisScannerModule() { super("ancient-debris-scanner", "Ancient Debris Scanner", "Alerts locally when ancient debris is nearby.", Blocks.ANCIENT_DEBRIS); }
    @Override protected String displayName() { return "Ancient debris"; }
}
