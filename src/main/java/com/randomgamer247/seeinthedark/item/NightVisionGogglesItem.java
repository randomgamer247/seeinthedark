package com.randomgamer247.seeinthedark.item;

import com.randomgamer247.seeinthedark.Config;
import com.randomgamer247.seeinthedark.SeeintheDark;
import com.randomgamer247.seeinthedark.network.NightVisionToggleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
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

        this.equippedServerSideTick(stack);
    }

    public void equippedServerSideTick(@NotNull ItemStack stack) {
        // This would not work if I wanted the night vision goggles to have any other attribute modifiers
        // Wish the ItemAttributeModifiers class had a simple way to remove a specific attribute modifier...
        ItemAttributeModifiers modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        if (stack.getOrDefault(SeeintheDark.ITEM_ENABLED_COMPONENT, false)) {
            int usage = Config.NIGHT_VISION_GOGGLES_ENERGY_USAGE.get();
            int reducedEnergy = Math.max(stack.getOrDefault(SeeintheDark.ITEM_ENERGY_COMPONENT, 0) - usage, 0);
            stack.set(SeeintheDark.ITEM_ENERGY_COMPONENT, reducedEnergy);

            if (modifiers != ItemAttributeModifiers.EMPTY && reducedEnergy < usage)
                this.RemoveNightVisionModifiers(stack);
            else if (modifiers == ItemAttributeModifiers.EMPTY && reducedEnergy >= usage)
                this.SetNightVisionModifiers(stack);
            return;
        }
        if (modifiers != ItemAttributeModifiers.EMPTY) this.RemoveNightVisionModifiers(stack);
    }

    public void SetNightVisionModifiers(ItemStack stack) {
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, makeNightVisionScaleModifier(1.0));
    }

    public void RemoveNightVisionModifiers(ItemStack stack) {
        stack.remove(DataComponents.ATTRIBUTE_MODIFIERS);
    }

    public static ItemAttributeModifiers makeNightVisionScaleModifier(double modifier) {
        return ItemAttributeModifiers.builder().add(
                SeeintheDark.NIGHT_VISION_SCALE_ATTRIBUTE,
                new AttributeModifier(
                        SeeintheDark.NIGHT_VISION_GOGGLES_SCALE_MODIFIER_RESOURCE_LOCATION,
                        modifier,
                        AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.HEAD
        ).build().withTooltip(false);
    }

    public ItemStack getEquipped(Player player) {
        ItemStack itemStack = player.getItemBySlot(EquipmentSlot.HEAD);
        return (itemStack.getItem().equals(SeeintheDark.NIGHT_VISION_GOGGLES_ITEM.get())) ?
                itemStack :
                ItemStack.EMPTY;
    }

    // This exists because I want to override getEquipped, but I also need to call it from static contexts...
    public static ItemStack getEquippedStatic(Player player) {
        return ((NightVisionGogglesItem)SeeintheDark.NIGHT_VISION_GOGGLES_ITEM.get()).getEquipped(player);
    }

    // Toggle the enabled component when the button press packet is received
    public static void handleToggleData(final NightVisionToggleData data, final IPayloadContext context) {
        ItemStack itemStack = getEquippedStatic(context.player());
        if (itemStack.isEmpty()) return;

        boolean newValue = data.shouldEnable();
        // Change the component
        itemStack.set(SeeintheDark.ITEM_ENABLED_COMPONENT, newValue);
        // Display a message
        context.player().displayClientMessage(Component.translatable(
                newValue ? "item.seeinthedark.night_vision_goggles.toggle_on_message" : "item.seeinthedark.night_vision_goggles.toggle_off_message"
        ), true);
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
                    ItemStack itemStack = getEquippedStatic(player);
                    if (itemStack.isEmpty()) return;

                    PacketDistributor.sendToServer(new NightVisionToggleData(!itemStack.getOrDefault(SeeintheDark.ITEM_ENABLED_COMPONENT, true)));
                }
            }
        }
    }
}
