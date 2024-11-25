package net.chenjiaf.catcherorbmod.item.custom;

import net.chenjiaf.catcherorbmod.component.ModDataComponentTypes;
import net.chenjiaf.catcherorbmod.entity.custom.ThrownOrb;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import java.util.List;

public class OrbItem extends Item {
    public OrbItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack $$3 = pPlayer.getItemInHand(pHand);
        CompoundTag entity = $$3.get(ModDataComponentTypes.MOB.get());
        if (entity == null) {
            pLevel.playSound((Player) null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.EGG_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!pLevel.isClientSide) {
                ThrownOrb $$4 = new ThrownOrb(pLevel, pPlayer);
                $$4.setItem($$3);
                $$4.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.5F, 1.0F);
                pLevel.addFreshEntity($$4);
            }

            $$3.consume(1, pPlayer);
        }
        return InteractionResultHolder.sidedSuccess($$3, pLevel.isClientSide());
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();

        if (!level.isClientSide()) {
            ItemStack orb = pContext.getItemInHand();
            CompoundTag entity = orb.get(ModDataComponentTypes.MOB.get());
            if (entity != null) {
                // Spawn the entity inside the orb

                Entity mob = EntityType.loadEntityRecursive(entity, level, (entity1) -> {
                    entity1.moveTo(pContext.getClickLocation().x(), pContext.getClickLocation().y(), pContext.getClickLocation().z());
                    return entity1;
                });

                level.addFreshEntity(mob);

                orb.consume(1, pContext.getPlayer());
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if(pStack.get(ModDataComponentTypes.MOB.get()) != null) {
            pTooltipComponents.add(Component.literal("Mob Caught " + pStack.get(ModDataComponentTypes.MOB.get())));
        }

        super.appendHoverText(pStack, pContext, pTooltipComponents, pIsAdvanced);
    }


}
