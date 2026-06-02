package com.randomgamer247.seeinthedark.compat.curios.item;

import com.randomgamer247.seeinthedark.SeeintheDark;
import com.randomgamer247.seeinthedark.item.NightVisionGogglesItem;
import com.randomgamer247.seeinthedark.compat.curios.CuriosCompat;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.*;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

public class NightVisionGogglesCurioItem extends NightVisionGogglesItem implements ICurioItem {
    public NightVisionGogglesCurioItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity().level().isClientSide()) return;

        this.equippedServerSideTick(stack);
    }

    @Override
    public void SetNightVisionModifiers(ItemStack stack) {
        stack.set(CuriosCompat.CURIOS_ATTRIBUTE_MODIFIERS_PROXY, makeNightVisionScaleCuriosModifier(1.0));
        super.SetNightVisionModifiers(stack);
    }

    @Override
    public void RemoveNightVisionModifiers(ItemStack stack) {
        stack.remove(CuriosCompat.CURIOS_ATTRIBUTE_MODIFIERS_PROXY);
        super.RemoveNightVisionModifiers(stack);
    }

    public static CurioAttributeModifiers makeNightVisionScaleCuriosModifier(double modifier) {
        return CurioAttributeModifiers.builder().add(
                SeeintheDark.NIGHT_VISION_SCALE_ATTRIBUTE,
                new AttributeModifier(
                        SeeintheDark.NIGHT_VISION_GOGGLES_SCALE_MODIFIER_RESOURCE_LOCATION,
                        modifier,
                        AttributeModifier.Operation.ADD_VALUE
                ),
                "head"
        ).build().withTooltip(false);
    }

    @Override
    public ItemStack getEquipped(Player player) {
        ItemStack stack = super.getEquipped(player);
        if (!stack.isEmpty()) return stack;

        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(player);
        if (curiosInventory.isPresent()) {
            Optional<SlotResult> slotResult = curiosInventory.get().findFirstCurio(SeeintheDark.NIGHT_VISION_GOGGLES_ITEM.get());
            if (slotResult.isPresent()) return slotResult.get().stack();
        }

        return ItemStack.EMPTY;
    }
}
