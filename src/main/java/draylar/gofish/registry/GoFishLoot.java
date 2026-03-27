package draylar.gofish.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import draylar.gofish.GoFish;
import draylar.gofish.loot.WeatherCondition;
import draylar.gofish.loot.biome.MatchBiomeLootCondition;
import draylar.gofish.loot.moon.FullMoonCondition;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class GoFishLoot {

    public static final MapCodec<? extends LootItemCondition> MATCH_BIOME = register("match_biome", MatchBiomeLootCondition.CODEC);
    public static final MapCodec<? extends LootItemCondition> FULL_MOON = register("full_moon", FullMoonCondition.CODEC);
    public static final MapCodec<? extends LootItemCondition> WEATHER = register("weather", WeatherCondition.CODEC);

    private static MapCodec<? extends LootItemCondition> register(String id, MapCodec<? extends LootItemCondition> codec) {
        return Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, GoFish.id(id), codec);
    }

    public static void init() {

    }

    private GoFishLoot() {

    }
}
