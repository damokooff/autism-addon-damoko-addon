package com.example.addon.modules;

import autismclient.api.module.DoubleSetting;
import autismclient.modules.Module;
import com.example.addon.AddonCategories;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.item.Items;

/** Adds a Meteor-style fall-distance spoof before a mace attack packet. */
public final class MaceKillModule extends Module {
    private final DoubleSetting extraHeight = add(new DoubleSetting(
        "extra-height", "Extra spoofed height", 0.0, 0.0, 100.0, 0.1)
        .group("Mace Smash"));

    private boolean sendingSpoof;

    public MaceKillModule() {
        super("damoko-addon:mace-kill", "Mace Kill", AddonCategories.DAMOKO,
            "Spoofs a short fall before mace attacks to trigger smash damage.");
    }

    @Override
    public boolean onPacketSend(Packet<?> packet) {
        if (sendingSpoof || MC.player == null || MC.getConnection() == null
            || !(packet instanceof ServerboundAttackPacket)
            || !MC.player.getMainHandItem().is(Items.MACE)
            || MC.player.isFallFlying()) return false;

        sendingSpoof = true;
        try {
            sendPosition(0.0);
            sendPosition(1.501 + extraHeight.get());
            sendPosition(0.0);
        } finally {
            sendingSpoof = false;
        }
        return false;
    }

    private void sendPosition(double height) {
        MC.getConnection().send(new ServerboundMovePlayerPacket.Pos(
            MC.player.getX(), MC.player.getY() + height, MC.player.getZ(), false, false));
    }
}
