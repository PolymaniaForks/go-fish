package draylar.gofish.block;

import draylar.gofish.GoFish;
import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class WoodenCrateBlock extends CrateBlock {

    public WoodenCrateBlock(Properties settings) {
        super(settings);
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return new draylar.gofish.block.WoodenCrateBlock.Model(pos, initialBlockState);
    }


    public static final class Model extends BlockModel {
        private static final List<Tuple<ItemStack, Float>> MODELS = List.of(
                entry(GoFish.id("wooden_crate"), 0f),
                entry(GoFish.id("wooden_crate"), 90f),
                entry(GoFish.id("wooden_crate_0"), 0f),
                entry(GoFish.id("wooden_crate_1"), 0f)
        );

        private static Tuple<ItemStack, Float> entry(Identifier id, float yaw) {
            var stack = new ItemStack(Items.TRIAL_KEY);
            stack.set(DataComponents.ITEM_MODEL, id);
            return new Tuple<>(stack, yaw);
        }

        public Model(BlockPos pos, BlockState state) {
            var model = Util.getRandom(MODELS, RandomSource.create(pos.asLong()));
            var main = ItemDisplayElementUtil.createSimple(model.getA());
            main.setYaw(model.getB());
            main.setScale(new Vector3f(2));
            this.addElement(main);
        }
    }
}
