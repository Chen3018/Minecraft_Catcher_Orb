package net.chenjiaf.catcherorbmod.worldgen.dimension;

import net.chenjiaf.catcherorbmod.CatcherOrbMod;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.Optional;
import java.util.OptionalLong;

public class ModDimensions {
    public static final ResourceKey<LevelStem> COCODIM_KEY = ResourceKey.create(Registries.LEVEL_STEM,
            new ResourceLocation(CatcherOrbMod.MODID, "cocodim"));
    public static final ResourceKey<Level> COCODIM_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(CatcherOrbMod.MODID, "cocodim"));
    public static final ResourceKey<DimensionType> COCODIM_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
            new ResourceLocation(CatcherOrbMod.MODID, "cocodim_type"));

    public static void bootstrapType(BootstrapContext<DimensionType> context) {
        context.register(COCODIM_DIM_TYPE, new DimensionType(
                OptionalLong.of(12000), //fixedTime
                false, //hasSkyLight
                false, //hasCeiling
                false, //ultrawarm
                false, //natural
                1.0, //coordinateScale
                true, //bedWorks
                false, //respawnAnchorWorks
                0, //minY
                256, //height
                256, //logicalHeight
                BlockTags.INFINIBURN_OVERWORLD, //infiniburn
                BuiltinDimensionTypes.OVERWORLD_EFFECTS, //effectsLocation
                0.1f, //ambientLight
                new DimensionType.MonsterSettings(false, false, ConstantInt.of(0), 0)));
    }

    public static void bootstrapStem(BootstrapContext<LevelStem> context) {
        HolderGetter<Biome> biomeRegistry = context.lookup(Registries.BIOME);
        HolderGetter<DimensionType> dimTypes = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> noiseGenSettings = context.lookup(Registries.NOISE_SETTINGS);

        //NoiseBasedChunkGenerator wrappedChunkGenerator = new NoiseBasedChunkGenerator(
        //        new FixedBiomeSource(biomeRegistry.getOrThrow(Biomes.THE_VOID)),
       //         noiseGenSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD));

        FlatLevelSource wrappedChunkGenerator = new FlatLevelSource(
                new FlatLevelGeneratorSettings(Optional.empty(), biomeRegistry.getOrThrow(Biomes.THE_VOID), new ArrayList<>()));

        //LevelStem stem = new LevelStem(dimTypes.getOrThrow(ModDimensions.COCODIM_DIM_TYPE), wrappedChunkGenerator);
        LevelStem stem = new LevelStem(dimTypes.getOrThrow(BuiltinDimensionTypes.OVERWORLD), wrappedChunkGenerator);

        context.register(COCODIM_KEY, stem);
    }
}
