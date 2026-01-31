package com.randomgamer247.seeinthedark.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.randomgamer247.seeinthedark.SeeintheDark;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.randomgamer247.seeinthedark.Config.NIGHT_VISION_GOGGLES_ENERGY_USAGE;

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
        ItemStack stack = this.minecraft.player.getItemBySlot(EquipmentSlot.HEAD);
        if (stack.getOrDefault(SeeintheDark.ITEM_ENABLED_COMPONENT, false) && stack.getOrDefault(SeeintheDark.ITEM_ENERGY_COMPONENT, 0) >= NIGHT_VISION_GOGGLES_ENERGY_USAGE.get()) {
            f5.set(Math.max(f5.get(), 1.0F));
        }
    }
}
