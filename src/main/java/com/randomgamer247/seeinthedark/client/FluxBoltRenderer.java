package com.randomgamer247.seeinthedark.client;

import com.randomgamer247.seeinthedark.SeeintheDark;
import com.randomgamer247.seeinthedark.entity.FluxBoltEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class FluxBoltRenderer extends ArrowRenderer<FluxBoltEntity> {
    public FluxBoltRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FluxBoltEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(SeeintheDark.MODID, "textures/entity/flux_bolt.png");
    }


}
