package net.chenjiaf.catcherorbmod.entity.custom;

import net.chenjiaf.catcherorbmod.CatcherOrbMod;
import net.chenjiaf.catcherorbmod.component.ModDataComponentTypes;
import net.chenjiaf.catcherorbmod.entity.ModEntities;
import net.chenjiaf.catcherorbmod.worldgen.dimension.ModDimensions;
import net.chenjiaf.catcherorbmod.worldgen.portal.ModTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.chenjiaf.catcherorbmod.item.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

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
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level().isClientSide){
            Entity entityHit = pResult.getEntity();
            if (! (entityHit instanceof Player)) {
                ItemEntity orbEntity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), this.getItem());
                ItemStack filledOrb = orbEntity.getItem();

                CompoundTag mobData = new CompoundTag();
                entityHit.save(mobData);
                filledOrb.set(ModDataComponentTypes.MOB.get(), mobData);

                entityHit.discard();
                this.discard();
                this.level().addFreshEntity(orbEntity);
            } else {
                ResourceLocation structureLocation = new ResourceLocation(CatcherOrbMod.MODID, "orb_cage");
                ServerLevel serverLevel = (ServerLevel) this.level();

                if (serverLevel.dimension() != ModDimensions.COCODIM_LEVEL_KEY) {
                    StructureTemplate structureTemplate = serverLevel.getStructureManager().get(structureLocation).orElse(null);

                    BlockPos zero = new BlockPos(0, 0, 0);
                    ServerLevel newDimension = serverLevel.getServer().getLevel(ModDimensions.COCODIM_LEVEL_KEY);
                    StructurePlaceSettings settings = new StructurePlaceSettings();
                    structureTemplate.placeInWorld(newDimension, zero, zero, settings, StructureBlockEntity.createRandom(0), 2);

                    entityHit.changeDimension(newDimension, new ModTeleporter(new BlockPos(3, 1, 3), false));
                }

                ItemEntity orbEntity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), this.getItem());
                this.level().addFreshEntity(orbEntity);

                this.discard();
            }
        }
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
    }

}
