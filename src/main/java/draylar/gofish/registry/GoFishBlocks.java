package draylar.gofish.registry;

import draylar.gofish.GoFish;
import draylar.gofish.block.CrateBlock;
import draylar.gofish.block.WoodenCrateBlock;
import draylar.gofish.item.CrateItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import java.util.function.Function;

public class GoFishBlocks {

    // The Wooden crate is padded with junk and cobwebs, but will often contain minimal resources, and a rare special item drop.
    //   Junk: Cobwebs, String, Kelp, Sticks, Planks, Seaweed
    //   Resources: coal, iron ore, iron nuggets, gold nuggets, flint, gold ingots
    //   Food: carrots, wheat, potatoes, beetroots
    //   Special: enchanting bottle, low-level enchanted book, emerald, bucket / bucket with fish, more materials
    //   Weapons: damaged crossbows, arrows, bows, stone tools
    //   Fish: all types of vanilla fish
    public static Block WOODEN_CRATE = registerCrate("wooden_crate", Block.Properties.ofFullCopy(Blocks.OAK_WOOD), WoodenCrateBlock::new, new Item.Properties().stacksTo(8), GoFish.id("gameplay/fishing/wooden_crate"));

    // The Iron Crate provides less junk, a chance for iron tools, and better rare loot.
    //   Junk: Oak Planks, sticks, Oak Logs, String, Seaweed, Kelp, Bones
    //   Resources: coal, iron ore, iron nuggets, gold nuggets, gold ingots, lapis, redstone
    //   Food: carrots, wheat, potatoes, beetroots, cooked potatoes
    //   Special: mid-level enchanted book, emerald, more materials
    //   Weapons: damaged crossbows, arrows, bows, stone tools
    //   Fish: all types of vanilla fish
    public static Block IRON_CRATE = registerCrate("iron_crate", Block.Properties.ofFullCopy(Blocks.IRON_BLOCK), CrateBlock::new, new Item.Properties().stacksTo(8), GoFish.id("gameplay/fishing/iron_crate"));

    // The Gold Crate is a rare crate that drops gold items and materials.
    public static Block GOLDEN_CRATE = registerCrate("golden_crate", Block.Properties.ofFullCopy(Blocks.GOLD_BLOCK), CrateBlock::new, new Item.Properties().stacksTo(8).rarity(Rarity.UNCOMMON), GoFish.id("gameplay/fishing/golden_crate"));

    // The Diamond Crate provides good materials
    public static Block DIAMOND_CRATE = registerCrate("diamond_crate", Block.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK), CrateBlock::new, new Item.Properties().stacksTo(8).rarity(Rarity.RARE), GoFish.id("gameplay/fishing/diamond_crate"));
    public static Block FROSTED_CRATE = registerCrate("frosted_crate", Block.Properties.ofFullCopy(Blocks.BLUE_ICE), CrateBlock::new, new Item.Properties().stacksTo(8).rarity(Rarity.RARE), GoFish.id("gameplay/fishing/frosted_crate"));
    public static Block SLIMEY_CRATE = registerCrate("slimey_crate", Block.Properties.ofFullCopy(Blocks.SLIME_BLOCK), CrateBlock::new, new Item.Properties().stacksTo(8), GoFish.id("gameplay/fishing/slimey_crate"));
    public static Block SUPPLY_CRATE = registerCrate("supply_crate", Block.Properties.ofFullCopy(Blocks.OAK_WOOD), CrateBlock::new, new Item.Properties().stacksTo(8), GoFish.id("gameplay/fishing/supply_crate"));
    public static Block FIERY_CRATE = registerCrate("fiery_crate", Block.Properties.ofFullCopy(Blocks.NETHER_BRICKS), CrateBlock::new, new Item.Properties().fireResistant().stacksTo(8), GoFish.id("gameplay/fishing/fiery_crate"));
    public static Block SOUL_CRATE = registerCrate("soul_crate", Block.Properties.ofFullCopy(Blocks.STONE), CrateBlock::new, new Item.Properties().fireResistant().stacksTo(8).rarity(Rarity.RARE), GoFish.id("gameplay/fishing/soul_crate"));
    public static Block GILDED_BLACKSTONE_CRATE = registerCrate("gilded_blackstone_crate", Block.Properties.ofFullCopy(Blocks.GILDED_BLACKSTONE), CrateBlock::new, new Item.Properties().fireResistant().stacksTo(8).rarity(Rarity.UNCOMMON), GoFish.id("gameplay/fishing/gilded_blackstone_crate"));
    public static Block ASTRAL_CRATE = registerCrate("astral_crate", Block.Properties.ofFullCopy(Blocks.END_STONE).noOcclusion(), CrateBlock::new, new Item.Properties().fireResistant().stacksTo(8).rarity(Rarity.EPIC), GoFish.id("gameplay/fishing/astral_crate"));
    public static Block END_CRATE = registerCrate("end_crate", Block.Properties.ofFullCopy(Blocks.END_STONE), CrateBlock::new, new Item.Properties().fireResistant().stacksTo(8).rarity(Rarity.EPIC), GoFish.id("gameplay/fishing/end_crate"));

    public static <T extends CrateBlock> T registerCrate(String name, BlockBehaviour.Properties blockSettings, Function<BlockBehaviour.Properties, T> blockFunc, Item.Properties settings, Identifier id) {
        var block = blockFunc.apply(blockSettings.setId(ResourceKey.create(Registries.BLOCK, GoFish.id(name))).noOcclusion());
        T registeredBlock = Registry.register(BuiltInRegistries.BLOCK, GoFish.id(name), block);
        var item = Registry.register(BuiltInRegistries.ITEM, GoFish.id(name), new CrateItem(block, settings.setId(ResourceKey.create(Registries.ITEM, GoFish.id(name))).useBlockDescriptionPrefix(), id));
        GoFishItems.ITEMS.add(item);
        return registeredBlock;
    }

    public static void init() {
        // NO-OP
    }
}
