package com.randomgamer247.seeinthedark.util;

import com.randomgamer247.seeinthedark.SeeintheDark;
import com.randomgamer247.seeinthedark.item.NightVisionGogglesItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import org.jetbrains.annotations.NotNull;

/**
 * Key conflict context for night vision goggles. The keybind should conflict only when a pair of night vision goggles are equipped
 */
public class GogglesKeyConflictContext implements IKeyConflictContext {

    @Override
    public boolean isActive() {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && !NightVisionGogglesItem.getEquippedStatic(player).isEmpty())
            return Minecraft.getInstance().screen == null;

        return false;
    }

    @Override
    public boolean conflicts(@NotNull IKeyConflictContext other) {
        return this == other;
    }
}
