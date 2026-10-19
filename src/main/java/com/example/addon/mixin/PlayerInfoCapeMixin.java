package com.example.addon.mixin;

import com.example.addon.modules.CustomCapeModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/** Applies the selected bundled cape to the local player's skin. */
@Mixin(PlayerInfo.class)
public abstract class PlayerInfoCapeMixin {
    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void damoko$applyCape(CallbackInfoReturnable<PlayerSkin> cir) {
        if (Minecraft.getInstance().player == null) return;
        PlayerInfo entry = (PlayerInfo) (Object) this;
        if (!entry.getProfile().id().equals(Minecraft.getInstance().player.getUUID())) return;

        CustomCapeModule module = CustomCapeModule.instance();
        if (module == null || !module.isEnabled()) return;

        PlayerSkin skin = cir.getReturnValue();
        // ResourceTexture appends "textures/" and ".png" to this asset id itself.
        Identifier capeId = Identifier.fromNamespaceAndPath("damoko-addon", "capes/" + module.cape().texture());
        ClientAsset.ResourceTexture cape = new ClientAsset.ResourceTexture(capeId);
        cir.setReturnValue(new PlayerSkin(skin.body(), cape, cape, skin.model(), skin.secure()));
    }
}
