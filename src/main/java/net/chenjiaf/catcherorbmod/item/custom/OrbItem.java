package net.chenjiaf.catcherorbmod.item.custom;

import com.jcraft.jorbis.Block;
import net.chenjiaf.catcherorbmod.CatcherOrbMod;
import net.chenjiaf.catcherorbmod.component.ModDataComponentTypes;
import net.chenjiaf.catcherorbmod.entity.custom.ThrownOrb;
import net.chenjiaf.catcherorbmod.worldgen.dimension.ModDimensions;
import net.chenjiaf.catcherorbmod.worldgen.portal.ModTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrbItem extends Item {
    private static final int SPACE_INBETWEEN = 5;
    private static final int NUM_WIDTH = 3;
    private static final int NUM_HEIGHT = 2;
    private static final int BLOCK_SIZE = 7;

    // Array list of intervals that are available for storing players.
    // Intervals are inclusive on both ends and -1 means infinity.
    private ArrayList<int[]> availableIntervals = new ArrayList<>();

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
                if (entity.contains("cageNum")) {
                    String playerId = entity.getString("playerId");
                    Player player = level.getPlayerByUUID(UUID.fromString(playerId));
                    player.changeDimension((ServerLevel) level, new ModTeleporter(pContext.getClickedPos(), false));

                    int cageNum = entity.getInt("cageNum");
                    ServerLevel newDimension = level.getServer().getLevel(ModDimensions.COCODIM_LEVEL_KEY);
                    freeSpace(cageNum, newDimension);
                } else {
                    Entity mob = EntityType.loadEntityRecursive(entity, level, (entity1) -> {
                        entity1.moveTo(pContext.getClickLocation().x(), pContext.getClickLocation().y(), pContext.getClickLocation().z());
                        return entity1;
                    });

                    level.addFreshEntity(mob);
                }
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

    public int getAvailable() {
        if (availableIntervals.isEmpty()) {
            int[] all = {0, -1};
            availableIntervals.add(all);
        }

        int[] first = availableIntervals.removeFirst();
        if (first[0] != first[1]) {
            int[] newAvailable = {first[0] + 1, first[1]};
            availableIntervals.addFirst(newAvailable);
        }

        return first[0];
    }

    public void freeSpace(int block, ServerLevel level) {
        int start = 0;
        int end = availableIntervals.size() - 1;
        int mid = 0;

        while (start <= end) {
            mid = (start + end) / 2;
            int[] current = availableIntervals.get(mid);
            if (block < current[0]) {
                end = mid - 1;
            } else {
                if (start == end) {
                    mid += 1;
                    break;
                }

                start = mid + 1;
            }
        }

        int[] newInterval = {block, block};
        if (mid != 0 && availableIntervals.get(mid - 1)[1] == block - 1) {
            newInterval[0] = availableIntervals.get(mid - 1)[0];
            availableIntervals.remove(mid - 1);
            mid -= 1;
        } else if (availableIntervals.get(mid)[0] == block + 1) {
            newInterval[1] = availableIntervals.get(mid)[1];
            availableIntervals.remove(mid);
        }
        availableIntervals.add(mid, newInterval);

        ResourceLocation structureLocation = new ResourceLocation(CatcherOrbMod.MODID, "empty_orb_cage");
        StructureTemplate structureTemplate = level.getStructureManager().get(structureLocation).orElse(null);
        BlockPos cagePos = getBlockPos(block);
        StructurePlaceSettings settings = new StructurePlaceSettings();
        structureTemplate.placeInWorld(level, cagePos, cagePos, settings, StructureBlockEntity.createRandom(0), 2);
    }

    public BlockPos getBlockPos(int block) {
        int numStacks = block / NUM_HEIGHT;
        int xBlock = numStacks % NUM_WIDTH;
        int xPos = xBlock * (BLOCK_SIZE + SPACE_INBETWEEN);

        int yBlock = block % NUM_HEIGHT;
        int yPos = yBlock * (BLOCK_SIZE + SPACE_INBETWEEN);

        int zBlock = numStacks / NUM_WIDTH;
        int zPos = zBlock * (BLOCK_SIZE + SPACE_INBETWEEN);

        return new BlockPos(xPos, yPos, zPos);
    }

    public BlockPos getSpawnPos(BlockPos cagePos) {
        return cagePos.above(1).east(BLOCK_SIZE / 2).south(BLOCK_SIZE / 2);
    }
}
