package draylar.gofish.impl;

import draylar.gofish.GoFish;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class GoFishLootTables {

    public static final ResourceKey<LootTable> NETHER_FISHING = of("gameplay/fishing/nether/fishing");
    public static final ResourceKey<LootTable> END_FISHING = of("gameplay/fishing/end/fishing");
    public static final ResourceKey<LootTable> CRATES = of("gameplay/fishing/crates");

    private static ResourceKey<LootTable> of(String s) {
        return ResourceKey.create(Registries.LOOT_TABLE, GoFish.id(s));
    }
}
