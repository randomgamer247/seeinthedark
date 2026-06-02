package com.randomgamer247.seeinthedark.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.randomgamer247.seeinthedark.SeeintheDark.NIGHT_VISION_SCALE_ATTRIBUTE;

/**
 * Mixin targeting Minecraft's lightmap calculation.
 * If the client player is wearing a pair of enabled and powered night vision goggles
 * then the lightmap value controlling overall brightness should be set to 1.0f
 */
@Mixin(LightTexture.class)
public class UpdateLightTextureMixin {
    @Shadow
    private Minecraft minecraft;

    @Definition(id = "blockLightRedFlicker", field = "Lnet/minecraft/client/renderer/LightTexture;blockLightRedFlicker:F")
    @Expression("? = this.blockLightRedFlicker + ?")
    @Inject(
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getWaterVision()F", shift = At.Shift.AFTER)
            ),
            method = "updateLightTexture",
            at = @At(value = "MIXINEXTRAS:EXPRESSION",shift = At.Shift.AFTER)
    )
    private void modifyLightTextureLocalF5(CallbackInfo ci, @Local(ordinal = 7) LocalFloatRef f5) {
        f5.set(Math.max(f5.get(), (float)this.minecraft.player.getAttributeValue(NIGHT_VISION_SCALE_ATTRIBUTE)));
    }
}
