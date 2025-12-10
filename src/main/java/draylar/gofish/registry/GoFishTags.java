package draylar.gofish.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;

public class GoFishTags {
    public static final TagKey<Biome> ICY = TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("gofish", "icy_biomes"));
    public static final TagKey<Biome> PLAINS = TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("gofish", "plains_biomes"));
    public static final TagKey<Biome> SWAMP = TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("gofish", "swamp_biomes"));


    public static final TagKey<DimensionType> NETHER_FISHING = TagKey.create(Registries.DIMENSION_TYPE, Identifier.fromNamespaceAndPath("gofish", "nether_fishing"));
    public static final TagKey<DimensionType> END_FISHING = TagKey.create(Registries.DIMENSION_TYPE, Identifier.fromNamespaceAndPath("gofish", "end_fishing"));

}
