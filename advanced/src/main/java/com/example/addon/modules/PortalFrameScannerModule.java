package com.example.addon.modules;

import net.minecraft.world.level.block.Blocks;

public final class PortalFrameScannerModule extends NearbyBlockScannerModule {
    public PortalFrameScannerModule() { super("portal-frame-scanner", "Portal Frame Scanner", "Alerts locally when End portal frame blocks are nearby.", Blocks.END_PORTAL_FRAME); }
    @Override protected String displayName() { return "End portal frame"; }
}
