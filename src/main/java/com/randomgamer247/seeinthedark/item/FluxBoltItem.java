package com.randomgamer247.seeinthedark.item;

import com.randomgamer247.seeinthedark.Config;
import com.randomgamer247.seeinthedark.SeeintheDark;
import com.randomgamer247.seeinthedark.entity.FluxBoltEntity;
import com.randomgamer247.seeinthedark.util.ReadableIntToStrFormatter;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.entity.living.LivingGetProjectileEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class FluxBoltItem extends ArrowItem {
    public FluxBoltItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        if (Config.NIGHT_VISION_GOGGLES_ENERGY_USAGE.get() > 0) {
            // Add energy status
            tooltipComponents.add(Component.translatable(
                    "item.seeinthedark.flux_bolt.energy_tooltip",
                    Component.literal(
                            ReadableIntToStrFormatter.format(stack.getOrDefault(SeeintheDark.ITEM_ENERGY_COMPONENT, 0), 2)
                    ).withColor(0xAA0000),
                    Component.literal(
                            ReadableIntToStrFormatter.format(Objects.requireNonNull(stack.getCapability(Capabilities.EnergyStorage.ITEM)).getMaxEnergyStored(), 2)
                    ).withColor(0xAA0000),
                    Component.translatable(
                            Config.ENERGY_UNIT_VISUAL.get().unit_key
                    ).withColor(0xAA0000)
            ).withColor(0xAAAAAA));
            // Add energy usage hint
            tooltipComponents.add(Component.translatable(
                    "item.seeinthedark.flux_bolt.energy_usage_tooltip",
                    Component.literal(
                            Integer.toString(Config.FLUX_BOLT_ENERGY_USAGE.get())
                    ).withColor(0xAA0000),
                    Component.translatable(
                            Config.ENERGY_UNIT_VISUAL.get().unit_key
                    ).withColor(0xAA0000)
            ).withColor(0xAAAAAA));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) { return true; }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round((float)stack.getOrDefault(SeeintheDark.ITEM_ENERGY_COMPONENT, 0) * 13.0F / (float) Objects.requireNonNull(stack.getCapability(Capabilities.EnergyStorage.ITEM)).getMaxEnergyStored());
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return Mth.color(1, 0, 0);
    }

    @Override
    public @NotNull AbstractArrow createArrow(@NotNull Level level, @NotNull ItemStack ammo, @NotNull LivingEntity shooter, @Nullable ItemStack weapon) {
        return new FluxBoltEntity(level, shooter, weapon);
    }

    @Override
    public boolean isInfinite(@NotNull ItemStack ammo, @NotNull ItemStack bow, net.minecraft.world.entity.@NotNull LivingEntity livingEntity) {
        // LivingProjectileEvent should never get called after this so reducing energy here should be safe...
        // TODO: Maybe add a multishot check here to reduce 3x the energy when multishot is used?
        ammo.update(
                SeeintheDark.ITEM_ENERGY_COMPONENT,
                0,
                energy -> Math.max(energy - Config.FLUX_BOLT_ENERGY_USAGE.get(), 0)
        );
        return true;
    }

    @EventBusSubscriber(modid = SeeintheDark.MODID)
    public static class FluxBoltEvents {
        @SubscribeEvent
        public static void LivingGetProjectileEvent(LivingGetProjectileEvent event) {
            ItemStack projectile = event.getProjectileItemStack();
            if (projectile.getItem().equals(SeeintheDark.FLUX_BOLT_ITEM.get())) {
                int remainingEnergy = projectile.getOrDefault(SeeintheDark.ITEM_ENERGY_COMPONENT, 0) - Config.FLUX_BOLT_ENERGY_USAGE.get();
                if (remainingEnergy <= 0) event.setProjectileItemStack(ItemStack.EMPTY);
            }
        }
    }
}
