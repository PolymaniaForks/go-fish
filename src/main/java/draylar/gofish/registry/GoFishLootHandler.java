package draylar.gofish.registry;

import draylar.gofish.impl.GoFishLootTables;
import draylar.gofish.loot.WeatherCondition;
import draylar.gofish.loot.biome.MatchBiomeLootCondition;
import draylar.gofish.loot.moon.FullMoonCondition;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.FishingHookPredicate;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class GoFishLootHandler {
    public static void init() {
        registerFishHandler();
    }

    private static void registerFishHandler() {
        LootTableEvents.MODIFY.register((ResourceKey<LootTable> key, LootTable.Builder tableBuilder, LootTableSource source) -> {
            if(BuiltInLootTables.FISHING.equals(key) && source.isBuiltin()) {
                var canModify = new MutableBoolean(true);
                tableBuilder.modifyPools(lpb -> {
                    if (canModify.booleanValue()) {
                        canModify.setFalse();
                    } else {
                        return;
                    }
                    lpb.add(NestedLootTable.lootTableReference(GoFishLootTables.CRATES)
                            .setWeight(5)
                            .setQuality(2)
                            .when(
                                    LootItemEntityPropertyCondition.hasProperties(
                                            LootContext.EntityTarget.THIS,
                                            EntityPredicate.Builder.entity().subPredicate(FishingHookPredicate.inOpenWater(true))
                                    )
                            )
                    );
                });
            } else if(BuiltInLootTables.FISHING_FISH.equals(key) && source.isBuiltin()) {
                var canModify = new MutableBoolean(true);
                tableBuilder.modifyPools(lpb -> {
                    if (canModify.booleanValue()) {
                        canModify.setFalse();
                    } else {
                        return;
                    }
                    // The default fish loot table has a total weight of 100.
                    // An entry with a weight of 10 represents a 10% chance to get that fish compared to the standard 4, but the percentage goes down as more custom fish are added.
                    // In most situations, only 1-2 fish are added per biome or area, so the chance for that fish is still ~5-10%.

                    // Cold Fish in Icy biomes
                    lpb.with(LootItem.lootTableItem(GoFishItems.ICICLE_FISH).setWeight(10).when(MatchBiomeLootCondition.builder(ConventionalBiomeTags.ICY)).build());
                    lpb.with(LootItem.lootTableItem(GoFishItems.SNOWBALL_FISH).setWeight(10).when(MatchBiomeLootCondition.builder(ConventionalBiomeTags.ICY)).build());

                    // Swamp
                    lpb.with(LootItem.lootTableItem(GoFishItems.SLIMEFISH).setWeight(10).when(MatchBiomeLootCondition.builder(ConventionalBiomeTags.SWAMP)).build());
                    lpb.with(LootItem.lootTableItem(GoFishItems.LILYFISH).setWeight(10).when(MatchBiomeLootCondition.builder(ConventionalBiomeTags.SWAMP)).build());

                    // Ocean
                    lpb.with(LootItem.lootTableItem(GoFishItems.SEAWEED_EEL).setWeight(10).when(MatchBiomeLootCondition.builder(ConventionalBiomeTags.OCEAN)).build());

                    // Mesa
                    lpb.with(LootItem.lootTableItem(GoFishItems.TERRAFISH).setWeight(10).when(MatchBiomeLootCondition.builder(ConventionalBiomeTags.MESA)).build());

                    // General Plains
                    lpb.with(LootItem.lootTableItem(GoFishItems.CARROT_CARP).setWeight(10).when(MatchBiomeLootCondition.builder(ConventionalBiomeTags.PLAINS)).build());
                    lpb.with(LootItem.lootTableItem(GoFishItems.OAKFISH).setWeight(10).when(MatchBiomeLootCondition.builder(ConventionalBiomeTags.PLAINS)).build());
                    lpb.with(LootItem.lootTableItem(GoFishItems.CARROT_CARP).setWeight(10).when(MatchBiomeLootCondition.builder(ConventionalBiomeTags.FOREST)).build());
                    lpb.with(LootItem.lootTableItem(GoFishItems.OAKFISH).setWeight(10).when(MatchBiomeLootCondition.builder(ConventionalBiomeTags.FOREST)).build());

                    // Misc
                    lpb.with(LootItem.lootTableItem(GoFishItems.LUNARFISH).setWeight(50).when(FullMoonCondition.builder()).build());
                    lpb.with(LootItem.lootTableItem(GoFishItems.GALAXY_STARFISH).setWeight(25).when(FullMoonCondition.builder()).build());
                    lpb.with(LootItem.lootTableItem(GoFishItems.STARRY_SALMON).setWeight(50).when(FullMoonCondition.builder()).build());
                    lpb.with(LootItem.lootTableItem(GoFishItems.NEBULA_SWORDFISH).setWeight(25).when(FullMoonCondition.builder()).build());

                    // weather
                    lpb.with(LootItem.lootTableItem(GoFishItems.RAINY_BASS).setWeight(100).when(WeatherCondition.builder(true, false, false)).build());
                    lpb.with(LootItem.lootTableItem(GoFishItems.THUNDERING_BASS).setWeight(50).when(WeatherCondition.builder(false, true, false)).build());
                    lpb.with(LootItem.lootTableItem(GoFishItems.CLOUDY_CRAB).setWeight(50).when(LocationCheck.checkLocation(LocationPredicate.Builder.atYLocation(MinMaxBounds.Doubles.atLeast(150)))).build());
                    lpb.with(LootItem.lootTableItem(GoFishItems.BLIZZARD_BASS).setWeight(100).when(WeatherCondition.builder(false, false, true)).build());
                });
            }
        });
    }
}
