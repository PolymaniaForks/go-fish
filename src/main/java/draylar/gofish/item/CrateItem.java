package draylar.gofish.item;

import eu.pb4.factorytools.api.item.FactoryBlockItem;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.*;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class CrateItem extends FactoryBlockItem {

    private final ResourceKey<LootTable> loot;

    public <T extends Block & PolymerBlock> CrateItem(T block, Properties settings) {
        super(block, settings);
        loot = BuiltInLootTables.PILLAGER_OUTPOST;
    }

    public <T extends Block & PolymerBlock> CrateItem(T block, Properties settings, Identifier loot) {
        super(block, settings);
        this.loot = ResourceKey.create(Registries.LOOT_TABLE, loot);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();

        if(player != null && player.isShiftKeyDown()) {
            return InteractionResult.FAIL;
        }

        return super.useOn(context);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        // only open crate if user is sneaking
        if(user.isShiftKeyDown()) {
            if(!world.isClientSide()) {
                // drop loot
                getDrops((ServerLevel) world, loot, user.position()).forEach(stack -> {
                    Containers.dropItemStack(world, user.getX(), user.getY(), user.getZ(), stack);
                });
            }

            // remove 1x from inventory for non-creative players
            if(!user.isCreative()) {
                user.getItemInHand(hand).shrink(1);
            }

            return InteractionResult.SUCCESS_SERVER;
        }

        return super.use(world, user, hand);
    }

    /**
     * Retrieves the loot table drops for the given Identifier.
     * If no loot table exists at the given Identifier, an empty list is returned.
     * @param identifier loot table Identifier
     * @return list of drops generated from the loot table
     */
    private List<ItemStack> getDrops(ServerLevel world, ResourceKey<LootTable> identifier, Vec3 pos) {
        List<ItemStack> output = new ArrayList<>();

        if (world != null && !world.isClientSide()) {
            // set up loot objects
            LootTable supplier = Objects.requireNonNull(world.getServer()).reloadableRegistries().getLootTable(identifier);
            LootParams.Builder builder =
                    new LootParams.Builder(world)
                            .withParameter(LootContextParams.ORIGIN, pos);

            // build & add loot to output
            List<ItemStack> stacks = supplier.getRandomItems(builder.create(LootContextParamSets.CHEST));
            output.addAll(stacks);
        }

        return output;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        super.appendHoverText(stack, context, displayComponent, textConsumer, type);
        textConsumer.accept(Component.translatable("gofish.crate_tooltip").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
    }
}
