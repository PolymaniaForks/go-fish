package draylar.gofish.block;

import eu.pb4.factorytools.api.block.CustomBreakingParticleBlock;
import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import xyz.nucleoid.packettweaker.PacketContext;

public class CrateBlock extends Block implements FactoryBlock, CustomBreakingParticleBlock {

    private ParticleOptions breakingParticle;

    public CrateBlock(Properties settings) {
        super(settings);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        return Blocks.BARRIER.defaultBlockState();
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return new Model(pos, initialBlockState);
    }

    @Override
    public ParticleOptions getBreakingParticle(BlockState state) {
        if (this.breakingParticle == null) {
            this.breakingParticle = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(this.asItem()));
        }
        return this.breakingParticle;
    }


    public static final class Model extends BlockModel {
        public Model(BlockPos pos, BlockState state) {
            var main = ItemDisplayElementUtil.createSolid(state.getBlock().asItem());
            main.setScale(new Vector3f(2));
            this.addElement(main);
        }
    }
}
