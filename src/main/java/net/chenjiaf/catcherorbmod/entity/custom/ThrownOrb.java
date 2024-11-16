package net.chenjiaf.catcherorbmod.entity.custom;

import net.chenjiaf.catcherorbmod.entity.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.chenjiaf.catcherorbmod.item.ModItems;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class ThrownOrb extends ThrowableItemProjectile {
    public ThrownOrb(EntityType<? extends ThrownOrb> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ThrownOrb(Level pLevel, LivingEntity pShooter) {
        super(ModEntities.ORB.get(), pShooter, pLevel);
    }

    @Override
    protected Item getDefaultItem() { return ModItems.ORB.get(); }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (!this.level().isClientSide) {
            this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), this.getItem()));
        }

        this.discard();
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
    }
}
