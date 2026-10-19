package com.example.addon.modules;

import net.minecraft.world.level.block.Blocks;

public final class PortalFinderModule extends NearbyBlockScannerModule {
    public PortalFinderModule() { super("portal-finder", "Portal Finder", "Alerts locally when Nether portal blocks are nearby.", Blocks.NETHER_PORTAL); }
    @Override protected String displayName() { return "Nether portal"; }
}
