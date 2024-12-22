package net.chenjiaf.catcherorbmod.entity.custom;

import net.chenjiaf.catcherorbmod.CatcherOrbMod;
import net.chenjiaf.catcherorbmod.component.ModDataComponentTypes;
import net.chenjiaf.catcherorbmod.entity.ModEntities;
import net.chenjiaf.catcherorbmod.item.custom.OrbItem;
import net.chenjiaf.catcherorbmod.worldgen.dimension.ModDimensions;
import net.chenjiaf.catcherorbmod.worldgen.portal.ModTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
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
            ItemEntity orbEntity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), this.getItem());
            ItemStack filledOrb = orbEntity.getItem();

            if (! (entityHit instanceof Player)) {
                CompoundTag mobData = new CompoundTag();
                entityHit.save(mobData);
                filledOrb.set(ModDataComponentTypes.MOB.get(), mobData);

                entityHit.discard();
                this.discard();
                this.level().addFreshEntity(orbEntity);
            } else {
                ServerLevel serverLevel = (ServerLevel) this.level();

                if (serverLevel.dimension() != ModDimensions.COCODIM_LEVEL_KEY) {
                    ResourceLocation structureLocation = new ResourceLocation(CatcherOrbMod.MODID, "orb_cage");
                    StructureTemplate structureTemplate = serverLevel.getStructureManager().get(structureLocation).orElse(null);

                    OrbItem orb = (OrbItem) filledOrb.getItem();
                    int cageNum = orb.getAvailable();
                    BlockPos cagePos = orb.getBlockPos(cageNum);
                    CompoundTag cageTag = new CompoundTag();
                    cageTag.putInt("cageNum", cageNum);
                    cageTag.putString("playerId", entityHit.getUUID().toString());
                    filledOrb.set(ModDataComponentTypes.MOB.get(), cageTag);

                    ServerLevel newDimension = serverLevel.getServer().getLevel(ModDimensions.COCODIM_LEVEL_KEY);
                    StructurePlaceSettings settings = new StructurePlaceSettings();
                    structureTemplate.placeInWorld(newDimension, cagePos, cagePos, settings, StructureBlockEntity.createRandom(0), 2);

                    entityHit.changeDimension(newDimension, new ModTeleporter(orb.getSpawnPos(cagePos), false));
                }

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
