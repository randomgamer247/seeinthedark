package com.randomgamer247.seeinthedark.item;

import com.randomgamer247.seeinthedark.Config;
import com.randomgamer247.seeinthedark.SeeintheDark;
import com.randomgamer247.seeinthedark.network.NightVisionToggleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Class for night vision goggles
 */
public class NightVisionGogglesItem extends Item implements Equipable {
    public NightVisionGogglesItem(Properties pProperties) {
        super(pProperties);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        // Add enabled status
        tooltipComponents.add(Component.translatable(
                stack.getOrDefault(SeeintheDark.ITEM_ENABLED_COMPONENT, false) ?
                        "item.seeinthedark.night_vision_goggles.toggle_on_tooltip"
                        :
                        "item.seeinthedark.night_vision_goggles.toggle_off_tooltip"
                )
        );
        // Add key bind hint
        tooltipComponents.add(Component.translatable(
                "item.seeinthedark.night_vision_goggles.keybind_tooltip"
        ));
        // Do not show energy status if energy usage is set to 0 in the config
        if (Config.NIGHT_VISION_GOGGLES_ENERGY_USAGE.get() > 0) {
            // Add energy status
            tooltipComponents.add(Component.translatable(
                    "item.seeinthedark.night_vision_goggles.energy_tooltip",
                    Component.literal(
                            stack.getOrDefault(SeeintheDark.ITEM_ENERGY_COMPONENT, 0).toString()
                    ).withColor(0xAA0000),
                    Component.literal(
                            Integer.toString(
                                    Objects.requireNonNull(stack.getCapability(Capabilities.EnergyStorage.ITEM)).getMaxEnergyStored()
                            )
                    ).withColor(0xAA0000),
                    Component.translatable(
                            Config.ENERGY_UNIT_VISUAL.get().unit_key
                    ).withColor(0xAA0000)
            ).withColor(0xAAAAAA));
            // Add energy usage hint
            tooltipComponents.add(Component.translatable(
                    "item.seeinthedark.night_vision_goggles.energy_usage_tooltip",
                    Component.literal(
                            Integer.toString(Config.NIGHT_VISION_GOGGLES_ENERGY_USAGE.get())
                    ).withColor(0xAA0000),
                    Component.translatable(
                            Config.ENERGY_UNIT_VISUAL.get().unit_key
                    ).withColor(0xAA0000)
            ).withColor(0xAAAAAA));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public @NotNull Holder<SoundEvent> getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        return this.swapWithEquipmentSlot(this, pLevel, pPlayer, pHand);
    }

    // Always show the energy bar unless the energy usage config is set to 0
    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return Config.NIGHT_VISION_GOGGLES_ENERGY_USAGE.get() > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round((float)stack.getOrDefault(SeeintheDark.ITEM_ENERGY_COMPONENT, 0) * 13.0F / (float) Objects.requireNonNull(stack.getCapability(Capabilities.EnergyStorage.ITEM)).getMaxEnergyStored());
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return Mth.color(1, 0, 0);
    }

    // Reduce energy until empty if the goggles are equipped and enabled
    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        // Head slot is apparently 39 and not EquipmentSlot.HEAD?
        if (level.isClientSide() || slotId != 39) return;

        if (stack.getOrDefault(SeeintheDark.ITEM_ENABLED_COMPONENT, false)) {
            stack.update(SeeintheDark.ITEM_ENERGY_COMPONENT,
                    0,
                    energy -> Math.max(energy - Config.NIGHT_VISION_GOGGLES_ENERGY_USAGE.get(), 0)
            );
        }
    }

    // Toggle the enabled component when the button press packet is received
    public static void handleToggleData(final NightVisionToggleData data, final IPayloadContext context) {
        ItemStack itemStack = context.player().getItemBySlot(EquipmentSlot.HEAD);
        if (itemStack.getItem().equals(SeeintheDark.NIGHT_VISION_GOGGLES_ITEM.get())) {
            boolean newValue = data.shouldEnable();
            // Change the component
            itemStack.set(SeeintheDark.ITEM_ENABLED_COMPONENT, newValue);
            // Display a message
            context.player().displayClientMessage(Component.translatable(
                    newValue ? "item.seeinthedark.night_vision_goggles.toggle_on_message" : "item.seeinthedark.night_vision_goggles.toggle_off_message"
            ), true);
        }
    }

    /**
     * A class containing night vision goggles related client side events
     */
    @EventBusSubscriber(modid = SeeintheDark.MODID, value = Dist.CLIENT)
    public static class GogglesEvents {
        // Subscribe to the client tick events to listen to the toggle key
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            while (SeeintheDark.TOGGLE_KEY.get().consumeClick()) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null) {
                    ItemStack itemStack = player.getItemBySlot(EquipmentSlot.HEAD);
                    if (itemStack.getItem().equals(SeeintheDark.NIGHT_VISION_GOGGLES_ITEM.get())) {
                        PacketDistributor.sendToServer(new NightVisionToggleData(!itemStack.getOrDefault(SeeintheDark.ITEM_ENABLED_COMPONENT, true)));
                    }
                }
            }
        }
    }
}
