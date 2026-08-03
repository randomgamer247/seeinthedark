package com.randomgamer247.seeinthedark.util;

import com.randomgamer247.seeinthedark.SeeintheDark;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class DebugItemEnergyStorage implements IEnergyStorage {
    private final DataComponentType<Integer> ENERGY_COMPONENT = SeeintheDark.ITEM_ENERGY_COMPONENT.get();

    protected ItemStack stack;
    protected int capacity;
    protected int maxReceive;

    public DebugItemEnergyStorage(ItemStack stack, int capacity, int maxReceive) {
        this.stack = stack;
        this.capacity = capacity;
        this.maxReceive = maxReceive;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        if (!canReceive() || toReceive <= 0) {
            return 0;
        }

        int energy = this.stack.getOrDefault(ENERGY_COMPONENT, 0);

        int energyReceived = Mth.clamp(this.capacity - energy, 0, Math.min(this.maxReceive, toReceive));
        if (!simulate)
            stack.set(ENERGY_COMPONENT, energy + energyReceived);
        return energyReceived;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return Math.min(this.stack.getOrDefault(ENERGY_COMPONENT, 0), this.capacity);
    }

    @Override
    public int getMaxEnergyStored() {
        return this.capacity;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
    }
}
