package com.example.addon.modules;

import autismclient.api.module.BoolSetting;
import autismclient.api.module.DoubleSetting;
import autismclient.api.module.EnumSetting;
import autismclient.api.module.IntSetting;
import autismclient.modules.Module;
import autismclient.util.AutismInventoryHelper;
import com.example.addon.AddonCategories;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

/** Elytra flight controls adapted from Meteor's ElytraFly module. */
public final class AutoPilotModule extends Module {
    public enum FlightMode { VANILLA, PACKET, PITCH40, BOUNCE }

    // Meteor ElytraFly defaults.
    private final EnumSetting<FlightMode> mode = add(new EnumSetting<>("mode", "Mode", FlightMode.VANILLA, FlightMode.values())
        .group("General"));
    private final BoolSetting autoTakeoff = add(new BoolSetting("auto-take-off", "Auto take off", false).group("General"));
    private final DoubleSetting fallMultiplier = add(new DoubleSetting("fall-multiplier", "Fall multiplier", 0.01, 0.0, 1.0, 0.01)
        .group("General").visibleWhen(() -> mode.get() == FlightMode.VANILLA || mode.get() == FlightMode.PACKET));
    private final DoubleSetting horizontalSpeed = add(new DoubleSetting("horizontal-speed", "Horizontal speed", 1.0, 0.0, 10.0, 0.1)
        .group("General").visibleWhen(() -> mode.get() == FlightMode.VANILLA || mode.get() == FlightMode.PACKET));
    private final DoubleSetting verticalSpeed = add(new DoubleSetting("vertical-speed", "Vertical speed", 1.0, 0.0, 10.0, 0.1)
        .group("General").visibleWhen(() -> mode.get() == FlightMode.VANILLA || mode.get() == FlightMode.PACKET));
    private final BoolSetting acceleration = add(new BoolSetting("acceleration", "Acceleration", false).group("General"));
    private final DoubleSetting accelerationStep = add(new DoubleSetting("acceleration-step", "Acceleration step", 1.0, 0.1, 5.0, 0.1)
        .group("General").visibleWhen(acceleration::get));
    private final DoubleSetting accelerationStart = add(new DoubleSetting("acceleration-start", "Acceleration start", 0.0, 0.0, 10.0, 0.1)
        .group("General").visibleWhen(acceleration::get));
    private final BoolSetting stopInWater = add(new BoolSetting("stop-in-water", "Stop in water", true).group("General"));
    private final BoolSetting noUnloadedChunks = add(new BoolSetting("no-unloaded-chunks", "Avoid unloaded chunks", true).group("General"));
    private final BoolSetting autoHover = add(new BoolSetting("auto-hover", "Auto hover", false).group("General"));
    private final BoolSetting noCrash = add(new BoolSetting("no-crash", "No crash", false).group("General"));
    private final IntSetting crashLookAhead = add(new IntSetting("crash-look-ahead", "Crash look ahead", 5, 1, 15, 1)
        .group("General").visibleWhen(noCrash::get));
    private final BoolSetting instaDrop = add(new BoolSetting("insta-drop", "Insta drop", false).group("General"));
    private final DoubleSetting pitch40Lower = add(new DoubleSetting("pitch40-lower-bounds", "Pitch40 lower bounds", 80.0, -128.0, 360.0, 1.0)
        .group("Pitch40").visibleWhen(() -> mode.get() == FlightMode.PITCH40));
    private final DoubleSetting pitch40Upper = add(new DoubleSetting("pitch40-upper-bounds", "Pitch40 upper bounds", 120.0, -128.0, 360.0, 1.0)
        .group("Pitch40").visibleWhen(() -> mode.get() == FlightMode.PITCH40));
    private final DoubleSetting pitch40Rotation = add(new DoubleSetting("pitch40-rotate-speed", "Pitch40 rotate speed", 15.0, 1.0, 20.0, 1.0)
        .group("Pitch40").visibleWhen(() -> mode.get() == FlightMode.PITCH40));
    private final BoolSetting autoJump = add(new BoolSetting("auto-jump", "Auto jump", true).group("Bounce")
        .visibleWhen(() -> mode.get() == FlightMode.BOUNCE));
    private final DoubleSetting pitch = add(new DoubleSetting("pitch", "Pitch", 85.0, 0.0, 90.0, 1.0).group("Bounce")
        .visibleWhen(() -> mode.get() == FlightMode.BOUNCE));
    private final BoolSetting restart = add(new BoolSetting("restart", "Restart after rubberband", true).group("Bounce")
        .visibleWhen(() -> mode.get() == FlightMode.BOUNCE));
    private final IntSetting restartDelay = add(new IntSetting("restart-delay", "Restart delay", 7, 0, 20, 1).group("Bounce")
        .visibleWhen(() -> mode.get() == FlightMode.BOUNCE && restart.get()));
    private final BoolSetting sprint = add(new BoolSetting("sprint-constantly", "Sprint constantly", true).group("Bounce")
        .visibleWhen(() -> mode.get() == FlightMode.BOUNCE));
    private final BoolSetting manualTakeoff = add(new BoolSetting("manual-takeoff", "Manual takeoff", false).group("Bounce")
        .visibleWhen(() -> mode.get() == FlightMode.BOUNCE));

    private final BoolSetting replaceElytra = add(new BoolSetting("elytra-replace", "Replace damaged elytra", false).group("Inventory"));
    private final IntSetting replaceDurability = add(new IntSetting("replace-durability", "Replace durability", 2, 1, 431, 1)
        .group("Inventory").visibleWhen(replaceElytra::get));
    private final BoolSetting autoReplenish = add(new BoolSetting("replenish-fireworks", "Replenish fireworks", false).group("Inventory"));
    private final IntSetting replenishSlot = add(new IntSetting("replenish-slot", "Replenish slot", 9, 1, 9, 1)
        .group("Inventory").visibleWhen(autoReplenish::get));
    private final BoolSetting autoPilot = add(new BoolSetting("auto-pilot", "Auto pilot", false).group("Autopilot")
        .visibleWhen(() -> mode.get() == FlightMode.VANILLA || mode.get() == FlightMode.PACKET));
    private final BoolSetting useFireworks = add(new BoolSetting("use-fireworks", "Use fireworks", false).group("Autopilot")
        .visibleWhen(autoPilot::get));
    private final DoubleSetting fireworkDelay = add(new DoubleSetting("firework-delay", "Firework delay", 8.0, 1.0, 20.0, 0.5)
        .group("Autopilot").unit("seconds").visibleWhen(useFireworks::get));
    private final DoubleSetting minimumHeight = add(new DoubleSetting("minimum-height", "Minimum height", 120.0, -128.0, 260.0, 1.0)
        .group("Autopilot").visibleWhen(autoPilot::get));
    private final BoolSetting autoReplaceFireworks = add(new BoolSetting("auto-replace-fireworks", "Auto replace fireworks", true)
        .group("Autopilot").description("Move another firework stack into the selected hotbar slot when it runs out."));

    private int jumpTimer;
    private int fireworksTimer;
    private int accelerationTicks;
    private float pitch40Angle = 32.0f;
    private boolean pitch40Descending = true;
    private boolean wasPacketFlying;

    public AutoPilotModule() {
        super("damoko-addon:auto-pilot", "Elytra Fly", AddonCategories.DAMOKO,
            "Meteor-style elytra flight control and autopilot.");
    }

    @Override
    public void onEnable() {
        jumpTimer = fireworksTimer = accelerationTicks = 0;
        wasPacketFlying = false;
        pitch40Angle = 32.0f;
        pitch40Descending = true;
    }

    @Override
    public void onDisable() {
        stopPacketFlight();
    }

    @Override
    public void onGameLeft() {
        wasPacketFlying = false;
        jumpTimer = 0;
    }

    @Override
    public void tick() {
        if (MC.player == null || MC.getConnection() == null) return;
        if (mode.get() == FlightMode.PACKET) packetTick();
        else stopPacketFlight();

        if (autoReplenish.get()) replenishFireworks();
        if (replaceElytra.get()) replaceElytraIfNeeded();

        if (autoTakeoff.get() && mode.get() != FlightMode.PITCH40 && mode.get() != FlightMode.BOUNCE
            && MC.options.keyJump.isDown() && !MC.player.isFallFlying()) {
            if (++jumpTimer >= 8) {
                jumpTimer = 0;
                takeOff();
            }
        } else jumpTimer = 0;

        if (MC.player.isFallFlying()) {
            if (autoPilot.get() && MC.player.getY() > minimumHeight.get() && mode.get() != FlightMode.BOUNCE) {
                MC.options.keyUp.setDown(true);
            }
            useFireworksIfNeeded();
        } else fireworksTimer = 0;

        if (mode.get() == FlightMode.PITCH40 && MC.player.isFallFlying()) updatePitch40();
        if (mode.get() == FlightMode.BOUNCE && MC.player.isFallFlying()) updateBounce();
    }

    private void packetTick() {
        if (!hasElytra() || MC.player.fallDistance <= 0.2f || MC.options.keyShift.isDown()) {
            stopPacketFlight();
            return;
        }
        double yaw = Math.toRadians(MC.player.getYRot());
        Vec3 movement = Vec3.ZERO;
        if (MC.options.keyUp.isDown()) movement = new Vec3(0, 0, horizontalSpeed.get()).yRot((float) -yaw);
        else if (MC.options.keyDown.isDown()) movement = new Vec3(0, 0, horizontalSpeed.get()).yRot((float) yaw);
        double vertical = MC.options.keyJump.isDown() ? verticalSpeed.get() : -verticalSpeed.get();
        MC.player.setDeltaMovement(movement.add(0, vertical, 0));
        MC.getConnection().send(new ServerboundPlayerCommandPacket(MC.player,
            ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
        MC.getConnection().send(new ServerboundMovePlayerPacket.StatusOnly(true, MC.player.horizontalCollision));
    }

    @Override
    public Vec3 onPlayerMove(MoverType type, Vec3 movement) {
        if (MC.player == null || !MC.player.isFallFlying()) return movement;
        if (mode.get() == FlightMode.PACKET) {
            wasPacketFlying = true;
            MC.player.getAbilities().flying = true;
            MC.player.getAbilities().setFlyingSpeed((float) (horizontalSpeed.get() / 20.0));
            return movement;
        }
        if (mode.get() != FlightMode.VANILLA) return movement;
        if (stopInWater.get() && MC.player.isInWater()) {
            MC.getConnection().send(new ServerboundPlayerCommandPacket(MC.player,
                ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
            return movement;
        }

        double speed = horizontalSpeed.get();
        if (acceleration.get() && (MC.options.keyUp.isDown() || MC.options.keyDown.isDown()
            || MC.options.keyLeft.isDown() || MC.options.keyRight.isDown())) {
            accelerationTicks++;
            speed = Math.min(speed, accelerationStart.get() + accelerationStep.get() * 0.1 * accelerationTicks);
        } else accelerationTicks = 0;
        double yaw = Math.toRadians(MC.player.getYRot());
        double forward = (MC.options.keyUp.isDown() ? 1 : 0) - (MC.options.keyDown.isDown() ? 1 : 0);
        double strafe = (MC.options.keyLeft.isDown() ? 1 : 0) - (MC.options.keyRight.isDown() ? 1 : 0);
        double x = -Math.sin(yaw) * forward + Math.cos(yaw) * strafe;
        double z = Math.cos(yaw) * forward + Math.sin(yaw) * strafe;
        double norm = Math.sqrt(x * x + z * z);
        if (norm > 0) { x = x / norm * speed; z = z / norm * speed; }
        double y = movement.y;
        // Meteor applies the fall multiplier before vertical key input. Applying
        // it after this input zeroed every positive (jump) velocity.
        if (y < 0) y *= fallMultiplier.get();
        else if (y > 0) y = 0;
        if (MC.options.keyJump.isDown()) y += 0.5 * verticalSpeed.get();
        else if (MC.options.keyShift.isDown()) y -= 0.5 * verticalSpeed.get();
        return new Vec3(x, y, z);
    }

    @Override
    public boolean onPacketSend(Packet<?> packet) {
        if (mode.get() == FlightMode.PACKET && MC.player != null && MC.getConnection() != null
            && packet instanceof ServerboundMovePlayerPacket) {
            MC.getConnection().send(new ServerboundPlayerCommandPacket(MC.player,
                ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
        }
        return false;
    }

    private void useFireworksIfNeeded() {
        if (!autoPilot.get() || !useFireworks.get()) return;
        if (fireworksTimer > 0) { fireworksTimer--; return; }
        if (MC.gameMode == null) return;
        if (MC.player.getOffhandItem().is(Items.FIREWORK_ROCKET)) {
            MC.gameMode.useItem(MC.player, InteractionHand.OFF_HAND);
        } else {
            int selected = MC.player.getInventory().getSelectedSlot();
            if (!MC.player.getInventory().getItem(selected).is(Items.FIREWORK_ROCKET)) {
                if (!autoReplaceFireworks.get()) return;
                int rocket = findRocketSlot();
                if (rocket < 0 || !AutismInventoryHelper.swapInventorySlots(MC, rocket, selected)) return;
            }
            MC.gameMode.useItem(MC.player, InteractionHand.MAIN_HAND);
        }
        fireworksTimer = (int) (fireworkDelay.get() * 20.0);
    }

    private int findRocketSlot() {
        int selected = MC.player.getInventory().getSelectedSlot();
        for (int slot = 0; slot < 36; slot++) {
            if (slot != selected && MC.player.getInventory().getItem(slot).is(Items.FIREWORK_ROCKET)) return slot;
        }
        return -1;
    }

    private void replenishFireworks() {
        int target = replenishSlot.get() - 1;
        if (MC.player.getInventory().getItem(target).is(Items.FIREWORK_ROCKET)) return;
        for (int slot = 0; slot < 36; slot++) {
            if (slot != target && MC.player.getInventory().getItem(slot).is(Items.FIREWORK_ROCKET)) {
                AutismInventoryHelper.swapInventorySlots(MC, slot, target);
                return;
            }
        }
    }

    private void replaceElytraIfNeeded() {
        var chest = MC.player.getItemBySlot(EquipmentSlot.CHEST);
        if (!chest.is(Items.ELYTRA) || chest.getMaxDamage() - chest.getDamageValue() > replaceDurability.get()) return;
        int selected = MC.player.getInventory().getSelectedSlot();
        for (int slot = 0; slot < 36; slot++) {
            var candidate = MC.player.getInventory().getItem(slot);
            if (candidate.is(Items.ELYTRA) && candidate.getMaxDamage() - candidate.getDamageValue() > replaceDurability.get()) {
                AutismInventoryHelper.swapInventorySlots(MC, slot, selected);
                if (MC.gameMode != null) MC.gameMode.handleContainerInput(MC.player.containerMenu.containerId,
                    6, selected, ContainerInput.SWAP, MC.player);
                return;
            }
        }
    }

    private void takeOff() {
        if (!hasElytra()) return;
        MC.player.jumpFromGround();
        MC.getConnection().send(new ServerboundPlayerCommandPacket(MC.player,
            ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
    }

    private void updatePitch40() {
        if (MC.player.getY() <= pitch40Lower.get()) pitch40Descending = false;
        else if (MC.player.getY() >= pitch40Upper.get()) pitch40Descending = true;
        if (pitch40Descending) pitch40Angle = Math.max(-49.0f, pitch40Angle - pitch40Rotation.get().floatValue());
        else pitch40Angle = Math.min(32.0f, pitch40Angle + 0.5f);
        MC.player.setXRot(pitch40Angle);
    }

    private void updateBounce() {
        MC.options.keyUp.setDown(true);
        if (autoJump.get()) MC.options.keyJump.setDown(true);
        MC.player.setXRot((float) -pitch.get());
        if (sprint.get()) MC.player.setSprinting(true);
        if (restart.get() && !MC.player.isFallFlying() && restartDelay.get() == 0) takeOff();
    }

    private boolean hasElytra() {
        return MC.player != null && MC.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA);
    }

    private void stopPacketFlight() {
        if (wasPacketFlying && MC.player != null) {
            MC.player.getAbilities().flying = false;
            MC.player.getAbilities().mayfly = false;
        }
        wasPacketFlying = false;
    }
}
