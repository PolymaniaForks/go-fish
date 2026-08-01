package draylar.gofish.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import draylar.gofish.impl.GoFishLootTables;
import draylar.gofish.registry.GoFishTags;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import java.util.List;

public class FishCommand {

    public static void register() {
        LiteralCommandNode<CommandSourceStack> root = Commands
                .literal("fish")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(context -> {
                    fish(context, 1);
                    return 1;
                })
                .then(Commands.argument("count", IntegerArgumentType.integer(1, 1000))
                .executes(context -> {
                    fish(context, IntegerArgumentType.getInteger(context, "count"));
                    return 1;
                }))
                .build();

        CommandRegistrationCallback.EVENT.register((dispatcher, access, dedicated) -> {
            dispatcher.getRoot().addChild(root);
        });
    }

    private static void fish(CommandContext<CommandSourceStack> context, int times) throws CommandSyntaxException {
        CommandSourceStack serverCommandSource = context.getSource();
        ServerPlayer player = context.getSource().getPlayer();
        ServerLevel world = context.getSource().getLevel();

        var lootContext = new LootParams.Builder(serverCommandSource.getLevel())
                .withParameter(LootContextParams.ORIGIN, player.position())
                .withParameter(LootContextParams.TOOL, player.getItemInHand(player.getUsedItemHand()))
                .withOptionalParameter(LootContextParams.THIS_ENTITY, serverCommandSource.getEntity())
                .create(LootContextParamSets.FISHING);

        LootTable table;
        final var dimension = world.dimensionTypeRegistration();
        if (dimension.is(GoFishTags.NETHER_FISHING)) {
            table = world.getServer().reloadableRegistries().getLootTable(GoFishLootTables.NETHER_FISHING);
        } else if (dimension.is(GoFishTags.END_FISHING)) {
            table = world.getServer().reloadableRegistries().getLootTable(GoFishLootTables.END_FISHING);
        } else {
            table = world.getServer().reloadableRegistries().getLootTable(BuiltInLootTables.FISHING);
        }

        for(int z = 0; z < times; z++){
            List<ItemStack> list = table.getRandomItems(lootContext);
            list.forEach(player::addItem);
        }
    }
}
