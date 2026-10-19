package com.example.addon.modules;

import autismclient.api.module.IntSetting;
import autismclient.modules.Module;
import com.example.addon.AddonCategories;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** Read-only, rate-limited local scan for selected block types. */
public abstract class NearbyBlockScannerModule extends Module {
    private final IntSetting radius = add(new IntSetting("radius", "Scan radius", 4, 2, 8, 1).unit("blocks"));
    private final Set<Block> targets;
    private Set<BlockPos> found = new HashSet<>();
    private int scanDelay = Math.floorMod(System.identityHashCode(this), 100);

    protected NearbyBlockScannerModule(String id, String name, String description, Block... targets) {
        super("damoko-addon:" + id, name, AddonCategories.TROUSER_STREAK, description);
        this.targets = Set.copyOf(Arrays.asList(targets));
    }

    @Override public void onEnable() { found.clear(); scanDelay = Math.floorMod(id().hashCode(), 100); }

    @Override
    public void tick() {
        if (MC.player == null || MC.level == null || ++scanDelay < 100) return;
        scanDelay = 0;
        int r = radius.get();
        BlockPos center = MC.player.blockPosition();
        Set<BlockPos> current = new HashSet<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int alerts = 0;
        for (int x = -r; x <= r; x++) for (int y = -r; y <= r; y++) for (int z = -r; z <= r; z++) {
            pos.set(center.getX() + x, center.getY() + y, center.getZ() + z);
            if (!MC.level.hasChunkAt(pos)) continue;
            if (targets.contains(MC.level.getBlockState(pos).getBlock())) {
                current.add(pos.immutable());
                if (alerts < 4 && !found.contains(pos)) {
                    MC.player.sendSystemMessage(Component.literal(displayName() + " nearby at "
                        + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()));
                    alerts++;
                }
            }
        }
        found = current;
    }

    protected abstract String displayName();
}
