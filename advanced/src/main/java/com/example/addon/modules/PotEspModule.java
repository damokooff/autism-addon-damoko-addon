package com.example.addon.modules;

import net.minecraft.world.level.block.Blocks;

public final class PotEspModule extends NearbyBlockScannerModule {
    public PotEspModule() { super("pot-esp", "Pot ESP", "Alerts locally when decorated pots are nearby.", Blocks.DECORATED_POT); }
    @Override protected String displayName() { return "Decorated pot"; }
}
