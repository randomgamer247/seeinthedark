package com.randomgamer247.seeinthedark.compat.curios.client;

import com.randomgamer247.seeinthedark.SeeintheDark;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CuriosRendererRegistration {
    public static void registerCuriosRenderers(final FMLClientSetupEvent event) {
        CuriosRendererRegistry.register(SeeintheDark.NIGHT_VISION_GOGGLES_ITEM.get(), HeadCurioRenderer::new);
    }
}
