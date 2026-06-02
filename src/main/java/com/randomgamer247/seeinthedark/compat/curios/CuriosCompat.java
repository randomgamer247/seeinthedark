package com.randomgamer247.seeinthedark.compat.curios;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import top.theillusivec4.curios.api.CurioAttributeModifiers;

import java.util.function.Supplier;

public class CuriosCompat {
    public static final Supplier<DataComponentType<CurioAttributeModifiers>> CURIOS_ATTRIBUTE_MODIFIERS_PROXY = DeferredHolder.create(
            Registries.DATA_COMPONENT_TYPE,
            ResourceLocation.fromNamespaceAndPath("curios", "attribute_modifiers"));
}
