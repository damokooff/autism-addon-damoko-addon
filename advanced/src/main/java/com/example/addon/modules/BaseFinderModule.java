package com.example.addon.modules;

import autismclient.api.module.IntSetting;
import autismclient.modules.Module;
import com.example.addon.AddonCategories;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Estimates nearby base-like chunks from locally loaded utility blocks. */
public final class BaseFinderModule extends Module {
    private static final Set<Block> INDICATORS = Set.of(
        Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.BARREL, Blocks.ENDER_CHEST,
        Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER, Blocks.CRAFTING_TABLE,
        Blocks.LOOM, Blocks.SMITHING_TABLE, Blocks.ANVIL, Blocks.CHIPPED_ANVIL,
        Blocks.DAMAGED_ANVIL, Blocks.LECTERN, Blocks.COMPOSTER, Blocks.BREWING_STAND,
        Blocks.SHULKER_BOX
    );

    private final IntSetting radius = add(new IntSetting("radius", "Scan radius", 8, 4, 16, 1).unit("blocks"));
    private final IntSetting threshold = add(new IntSetting("threshold", "Indicators per chunk", 6, 2, 30, 1));
    private final Map<ChunkPos, Integer> reported = new HashMap<>();
    private int scanDelay;

    public BaseFinderModule() {
        super("damoko-addon:base-finder", "Base Finder", AddonCategories.TROUSER_STREAK,
            "Estimates nearby base-like chunks from utility blocks in loaded terrain. Results stay local.");
    }

    @Override public void onEnable() { reported.clear(); scanDelay = 0; }

    @Override
    public void tick() {
        if (MC.player == null || MC.level == null || ++scanDelay < 200) return;
        scanDelay = 0;
        int r = radius.get();
        BlockPos center = MC.player.blockPosition();
        Map<ChunkPos, Integer> counts = new HashMap<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = -r; x <= r; x++) for (int y = -8; y <= 8; y++) for (int z = -r; z <= r; z++) {
            pos.set(center.getX() + x, center.getY() + y, center.getZ() + z);
            if (!MC.level.hasChunkAt(pos)) continue;
            if (INDICATORS.contains(MC.level.getBlockState(pos).getBlock())) {
                counts.merge(ChunkPos.containing(pos), 1, Integer::sum);
            }
        }
        int[] alerts = {0};
        counts.forEach((chunk, count) -> {
            if (alerts[0] < 3 && count >= threshold.get() && !reported.containsKey(chunk)) {
                MC.player.sendSystemMessage(Component.literal("Possible base nearby (chunk "
                    + chunk.x() + ", " + chunk.z() + "): " + count + " utility blocks"));
                alerts[0]++;
            }
        });
        reported.clear();
        reported.putAll(counts);
    }
}
