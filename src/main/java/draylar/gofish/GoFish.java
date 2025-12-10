package draylar.gofish;

import com.google.common.hash.HashCode;
import draylar.gofish.command.FishCommand;
import draylar.gofish.registry.*;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class GoFish implements ModInitializer {

    public static final ResourceKey<CreativeModeTab> ITEM_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB, id("group"));
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        PolymerItemGroupUtils.registerPolymerItemGroup(ITEM_GROUP, PolymerItemGroupUtils.builder()
                .icon(() -> new ItemStack(GoFishItems.GOLDEN_FISH))
                .title(Component.translatable("itemGroup.gofish.group"))
                .displayItems((a, b) -> GoFishItems.ITEMS.forEach(b::accept))
                .build());

        GoFishBlocks.init();
        GoFishItems.init();
        GoFishEnchantments.init();
        GoFishLoot.init();
        GoFishLootHandler.init();
        GoFishEntities.init();

        FishCommand.register();

        FuelRegistryEvents.BUILD.register((builder, context) -> {
            builder.add(GoFishItems.OAKFISH, 3 * context.baseSmeltTime() / 2);
            builder.add(GoFishItems.CHARFISH, 8 * context.baseSmeltTime());
        });

        FabricBrewingRecipeRegistryBuilder.BUILD.register(this::registerBrewingRecipes);

        PolymerResourcePackUtils.addModAssets("go-fish");

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            //PatboxLazyModelGen.run();
        }
    }

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath("gofish", name);
    }

    public void registerBrewingRecipes(PotionBrewing.Builder builder) {
        builder.addMix(Potions.AWKWARD, GoFishItems.CLOUDY_CRAB, Potions.SLOW_FALLING);
        builder.addMix(Potions.AWKWARD, GoFishItems.CHARFISH, Potions.WEAKNESS);
        builder.addMix(Potions.AWKWARD, GoFishItems.RAINY_BASS, Potions.WATER_BREATHING);
        builder.addMix(Potions.AWKWARD, GoFishItems.MAGMA_COD, Potions.FIRE_RESISTANCE);
    }

}
