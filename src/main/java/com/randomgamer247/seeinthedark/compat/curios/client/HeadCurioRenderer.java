package com.randomgamer247.seeinthedark.compat.curios.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class HeadCurioRenderer implements ICurioRenderer {
    private final ItemRenderer ITEM_RENDERER;

    public HeadCurioRenderer() {
        this.ITEM_RENDERER = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        LivingEntity wearer = slotContext.entity();

        matrixStack.pushPose();
        if (wearer.isCrouching()) matrixStack.translate(0.0F, 0.25F, 0.0F);
        matrixStack.mulPose(Axis.YP.rotationDegrees(netHeadYaw));
        matrixStack.mulPose(Axis.XP.rotationDegrees(headPitch));
        CustomHeadLayer.translateToHead(matrixStack, wearer instanceof Villager);
        this.ITEM_RENDERER.renderStatic(stack, ItemDisplayContext.HEAD, light, OverlayTexture.NO_OVERLAY, matrixStack, renderTypeBuffer, Minecraft.getInstance().level, 1);
        matrixStack.popPose();
    }
}
