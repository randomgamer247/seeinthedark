package com.randomgamer247.seeinthedark.entity;

import com.randomgamer247.seeinthedark.SeeintheDark;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class FluxBoltEntity extends AbstractArrow {
    public FluxBoltEntity(EntityType<? extends FluxBoltEntity> entityType, Level level) {
        super(entityType, level);
    }

    public FluxBoltEntity(Level level, LivingEntity owner, @Nullable ItemStack firedFromWeapon) {
        super(SeeintheDark.FLUX_BOLT_ENTITY_TYPE.get(), owner, level, new ItemStack(SeeintheDark.FLUX_BOLT_ITEM.get()), firedFromWeapon);
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return new ItemStack(SeeintheDark.FLUX_BOLT_ITEM.get());
    }

    @Override
    public void playerTouch(@NotNull Player entity) {
        // Do nothing
    }
}
