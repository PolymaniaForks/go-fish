package draylar.gofish.registry;

import draylar.gofish.impl.GoFishLootTables;
import draylar.gofish.loot.WeatherCondition;
import draylar.gofish.loot.biome.MatchBiomeLootCondition;
import draylar.gofish.loot.moon.FullMoonCondition;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.predicates.entity.FishingHookPredicate;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import org.apache.commons.lang3.mutable.MutableBoolean;

@SuppressWarnings("unchecked")
public class GoFishLootHandler {
    public static void init() {
        registerFishHandler();
    }

    private static void registerFishHandler() {
        LootTableEvents.MODIFY.register((key, tableBuilder, _, _) -> {
            if (BuiltInLootTables.FISHING.equals(key)) {
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
                                            EntityPredicate.Builder.entity().fishingHook(FishingHookPredicate.inOpenWater(true))
                                    )
                            )
                    );
                });
            } else if (BuiltInLootTables.FISHING_FISH.equals(key)) {
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
                    lpb.add(LootItem.lootTableItem(GoFishItems.ICICLE_FISH).setWeight(18).when(MatchBiomeLootCondition.builderTag(ConventionalBiomeTags.IS_ICY)).build());
                    lpb.add(LootItem.lootTableItem(GoFishItems.SNOWBALL_FISH).setWeight(18).when(MatchBiomeLootCondition.builderTag(ConventionalBiomeTags.IS_ICY)).build());

                    // Swamp
                    lpb.add(LootItem.lootTableItem(GoFishItems.SLIMEFISH).setWeight(18).when(MatchBiomeLootCondition.builderTag(ConventionalBiomeTags.IS_SWAMP)).build());
                    lpb.add(LootItem.lootTableItem(GoFishItems.LILYFISH).setWeight(18).when(MatchBiomeLootCondition.builderTag(ConventionalBiomeTags.IS_SWAMP)).build());

                    // Ocean
                    lpb.add(LootItem.lootTableItem(GoFishItems.SEAWEED_EEL).setWeight(18).when(MatchBiomeLootCondition.builderTag(ConventionalBiomeTags.IS_OCEAN)).build());

                    // Mesa
                    lpb.add(LootItem.lootTableItem(GoFishItems.TERRAFISH).setWeight(18).when(MatchBiomeLootCondition.builderTag(ConventionalBiomeTags.IS_BADLANDS)).build());

                    // General Plains
                    lpb.add(LootItem.lootTableItem(GoFishItems.CARROT_CARP).setWeight(15).when(MatchBiomeLootCondition.builderTag(ConventionalBiomeTags.IS_PLAINS)).build());
                    lpb.add(LootItem.lootTableItem(GoFishItems.OAKFISH).setWeight(15).when(MatchBiomeLootCondition.builderTag(ConventionalBiomeTags.IS_PLAINS)).build());
                    lpb.add(LootItem.lootTableItem(GoFishItems.CARROT_CARP).setWeight(15).when(MatchBiomeLootCondition.builderTag(ConventionalBiomeTags.IS_FOREST)).build());
                    lpb.add(LootItem.lootTableItem(GoFishItems.OAKFISH).setWeight(15).when(MatchBiomeLootCondition.builderTag(ConventionalBiomeTags.IS_FOREST)).build());

                    // Misc
                    lpb.add(LootItem.lootTableItem(GoFishItems.LUNARFISH).setWeight(50).when(FullMoonCondition.builder()).build());
                    lpb.add(LootItem.lootTableItem(GoFishItems.GALAXY_STARFISH).setWeight(25).when(FullMoonCondition.builder()).build());
                    lpb.add(LootItem.lootTableItem(GoFishItems.STARRY_SALMON).setWeight(50).when(FullMoonCondition.builder()).build());
                    lpb.add(LootItem.lootTableItem(GoFishItems.NEBULA_SWORDFISH).setWeight(25).when(FullMoonCondition.builder()).build());

                    // weather
                    lpb.add(LootItem.lootTableItem(GoFishItems.RAINY_BASS).setWeight(80).when(WeatherCondition.builder(true, false, false)).build());
                    lpb.add(LootItem.lootTableItem(GoFishItems.THUNDERING_BASS).setWeight(50).when(WeatherCondition.builder(false, true, false)).build());
                    lpb.add(LootItem.lootTableItem(GoFishItems.CLOUDY_CRAB).setWeight(50).when(LocationCheck.checkLocation(LocationPredicate.Builder.atYLocation(MinMaxBounds.Doubles.atLeast(150)))).build());
                    lpb.add(LootItem.lootTableItem(GoFishItems.BLIZZARD_BASS).setWeight(80).when(WeatherCondition.builder(false, false, true)).build());
                });
            }
        });
    }
}
