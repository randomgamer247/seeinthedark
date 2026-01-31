package com.randomgamer247.seeinthedark;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Energy storage capability implementation for seeinthedark.
 * Takes in an IntValue config and uses it to determine the capacity and receive parameters
 */
public class ItemEnergyStorage implements IEnergyStorage {
    private final DataComponentType<Integer> ENERGY_COMPONENT = SeeintheDark.ITEM_ENERGY_COMPONENT.get();
    private final IntValue CAPACITY_VALUE;
    private final IntValue RECEIVE_VALUE;

    protected ItemStack stack;
    //protected int capacity;
    //protected int maxReceive;

    public ItemEnergyStorage(ItemStack stack, IntValue capacityConfig, IntValue maxReceiveConfig) {
        this.stack = stack;
        //this.capacity = capacity;
        //this.maxReceive = maxReceive;
        this.CAPACITY_VALUE = capacityConfig;
        this.RECEIVE_VALUE = maxReceiveConfig;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        if (!canReceive() || toReceive <= 0) {
            return 0;
        }

        int energy = this.stack.getOrDefault(ENERGY_COMPONENT, 0);

        int energyReceived = Mth.clamp(this.CAPACITY_VALUE.get() - energy, 0, Math.min(this.RECEIVE_VALUE.get(), toReceive));
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
        return Math.min(this.stack.getOrDefault(ENERGY_COMPONENT, 0), this.CAPACITY_VALUE.get());
    }

    @Override
    public int getMaxEnergyStored() {
        return this.CAPACITY_VALUE.get();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return this.RECEIVE_VALUE.get() > 0;
    }
}
