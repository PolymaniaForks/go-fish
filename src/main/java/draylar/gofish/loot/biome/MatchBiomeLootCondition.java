package draylar.gofish.loot.biome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import draylar.gofish.registry.GoFishLoot;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.phys.Vec3;

public record MatchBiomeLootCondition(Optional<BiomeTagPredicate> category, Optional<BiomePredicate> biome) implements LootItemCondition {

    public static final MapCodec<MatchBiomeLootCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            BiomeTagPredicate.CODEC.optionalFieldOf("category").forGetter(MatchBiomeLootCondition::category),
                           BiomePredicate.CODEC.optionalFieldOf("biome").forGetter(MatchBiomeLootCondition::biome)
                    )
                    .apply(instance, MatchBiomeLootCondition::new)
    );

    @Override
    public LootItemConditionType getType() {
        return GoFishLoot.MATCH_BIOME;
    }

    @Override
    public boolean test(LootContext lootContext) {
        Vec3 origin = lootContext.getOptionalParameter(LootContextParams.ORIGIN);

        if(origin != null) {
            Holder<Biome> fisherBiome = lootContext.getLevel().getBiome(new BlockPos((int) Math.floor(origin.x), (int) Math.floor(origin.y), (int) Math.floor(origin.z)));

            // Category predicate is null, check exact biome
            if (category.isEmpty() || category.get().getValid().isEmpty()) {
                if (biome.isPresent() && !biome.get().getValid().isEmpty()) {
                    return biome.get().test(fisherBiome);
                }
            }

            // Category predicate is not null, check it
            else {
                return category.get().test(fisherBiome);
            }
        }

        return false;
    }

    public static LootItemCondition.Builder builder(ResourceKey<Biome>... biomes) {
        return builder(Collections.emptyList(), List.of(biomes));
    }

    public static LootItemCondition.Builder builderTag(TagKey<Biome>... categories) {
        return builder(Arrays.asList(categories), Collections.emptyList());
    }

    public static LootItemCondition.Builder builder(List<TagKey<Biome>> categories, List<ResourceKey<Biome>> biomes) {
        List<String> stringCats = new ArrayList<>();
        List<String> stringBiomes = new ArrayList<>();

        categories.forEach(category -> stringCats.add(category.location().toString()));
        biomes.forEach(biome -> stringBiomes.add(biome.identifier().toString()));

        return builder(BiomeTagPredicate.Builder.create().setValidByString(stringCats), BiomePredicate.Builder.create().setValidFromString(stringBiomes));
    }

    public static LootItemCondition.Builder builder(String category, String biome) {
        return builder(BiomeTagPredicate.Builder.create().add(category), BiomePredicate.Builder.create().add(biome));
    }

    public static LootItemCondition.Builder builder(BiomeTagPredicate.Builder categoryBuilder) {
        return builder(categoryBuilder, BiomePredicate.Builder.create());
    }

    public static LootItemCondition.Builder builder(BiomePredicate.Builder biomeBuilder) {
        return builder(BiomeTagPredicate.Builder.create(), biomeBuilder);
    }

    public static LootItemCondition.Builder builder(BiomeTagPredicate.Builder categoryBuilder, BiomePredicate.Builder biomeBuilder) {
        return () -> new MatchBiomeLootCondition(Optional.of(categoryBuilder.build()), Optional.of(biomeBuilder.build()));
    }
}
