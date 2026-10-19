package com.example.addon.modules;

import net.minecraft.world.level.block.Blocks;

public final class SpawnerScannerModule extends NearbyBlockScannerModule {
    public SpawnerScannerModule() { super("spawner-scanner", "Spawner Scanner", "Alerts locally when a mob spawner is nearby.", Blocks.SPAWNER); }
    @Override protected String displayName() { return "Mob spawner"; }
}
