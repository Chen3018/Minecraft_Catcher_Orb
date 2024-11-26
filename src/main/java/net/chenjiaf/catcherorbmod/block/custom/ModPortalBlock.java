package net.chenjiaf.catcherorbmod.block.custom;

import net.chenjiaf.catcherorbmod.worldgen.dimension.ModDimensions;
import net.chenjiaf.catcherorbmod.worldgen.portal.ModTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ModPortalBlock extends Block {
    public ModPortalBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        if (pPlayer.canChangeDimensions()) {
            handleCocoPortal(pPlayer, pPos);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }

    private void handleCocoPortal(Entity player, BlockPos pPos) {
        if (player.level() instanceof ServerLevel serverLevel) {
            MinecraftServer minecraftServer = serverLevel.getServer();
            ResourceKey<Level> resourceKey = player.level().dimension() == ModDimensions.COCODIM_LEVEL_KEY ?
                    Level.OVERWORLD : ModDimensions.COCODIM_LEVEL_KEY;

            ServerLevel portalDimension = minecraftServer.getLevel(resourceKey);
            if (portalDimension != null && !player.isPassenger()) {
                if (resourceKey == ModDimensions.COCODIM_LEVEL_KEY) {
                    player.changeDimension(portalDimension, new ModTeleporter(pPos, true));
                } else {
                    player.changeDimension(portalDimension, new ModTeleporter(pPos, false));
                }
            }
        }
    }
}
