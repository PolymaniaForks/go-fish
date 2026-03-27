package draylar.gofish.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import draylar.gofish.GoFish;
import draylar.gofish.item.ExtendedFishingRodItem;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import it.unimi.dsi.fastutil.ints.IntList;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

@Mixin(FishingHook.class)
public abstract class FishingBobberLavaFishingMixin extends Entity {

    @Shadow public abstract Player getPlayerOwner();
    @Shadow public abstract void remove(Entity.RemovalReason reason);

    @Shadow private FishingHook.FishHookState currentState;

    private FishingBobberLavaFishingMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    private ElementHolder holder = null;
    @Nullable
    private ItemDisplayElement bobber = null;

    /*@Override
    public boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> tag, double speed) {
        if (tag == FluidTags.LAVA && !this.level().isClientSide()) {
            return super.updateFluidHeightAndDoFluidPushing(tag, 0.014 * 2);
        }
        return super.updateFluidHeightAndDoFluidPushing(tag, speed);
    }*/

    @Inject(
        method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;II)V",
        at = @At("RETURN")
    )
    public void onInit(EntityType<? extends FishingHook> type, Level world, int luckBonus, int waitTimeReductionTicks, CallbackInfo ci) {
        if (world.isClientSide()) {
            return;
        }

        this.holder = new ElementHolder();
        this.bobber = new ItemDisplayElement() {
            @Override
            public void startWatching(ServerPlayer player, Consumer<Packet<ClientGamePacketListener>> packetConsumer) {
                super.startWatching(player, packetConsumer);
                packetConsumer.accept(VirtualEntityUtils.createClientboundSetPassengersPacket(this.getEntityId(), IntList.of(getId())));
            }
        };
        new EntityAttachment(this.holder, this, true);
    }

    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"
        )
    )
    public void onTick(CallbackInfo ci, @Local FluidState fluidState) {
        if (this.level().isClientSide()) {
            return;
        }
        if (fluidState.is(FluidTags.LAVA)) {
            if (this.currentState == FishingHook.FishHookState.BOBBING) {
                if (this.bobber != null && this.bobber.getHolder() == null) {
                    this.holder.addElement(bobber);
                }
            }
            return;
        }

        if (this.bobber != null) {
            this.holder.removeElement(bobber);
        }
    }

    // this mixin is used to determine whether a bobber is actually bobbing for fish
    @ModifyVariable(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 0),
            index = 2
    )
    private float bobberInLava(float value) {
        if (this.level().isClientSide()) {
            return value;
        }

        BlockPos blockPos = this.blockPosition();
        FluidState fluidState = this.level().getFluidState(blockPos);

        if (!fluidState.is(FluidTags.LAVA)) {
            return value;
        }
        // Fishing rod doesn't set active hand, and can be used in either, so we check both
        Item mainHandItem = getPlayerOwner().getMainHandItem().getItem();
        Item offHandItem = getPlayerOwner().getOffhandItem().getItem();

        // Player is holding extended fishing rod, check if it can be in lava.
        // Otherwise, fallback to default behavior.
        if (mainHandItem instanceof ExtendedFishingRodItem) {
            ExtendedFishingRodItem usedRod = (ExtendedFishingRodItem) mainHandItem;

            if (usedRod.canFishInLava()) {
                return fluidState.getHeight(this.level(), blockPos);
            }
        } else if (offHandItem instanceof ExtendedFishingRodItem) {
            ExtendedFishingRodItem usedRod = (ExtendedFishingRodItem) offHandItem;

            if (usedRod.canFishInLava()) {
                return fluidState.getHeight(this.level(), blockPos);
            }
        }

        if (!getPlayerOwner().isCreative()) {
            getPlayerOwner().getItemInHand(InteractionHand.MAIN_HAND).hurtAndBreak(5, getPlayerOwner(), EquipmentSlot.MAINHAND);
        }

        if (level() instanceof ServerLevel) {
            ((ServerLevel) level()).sendParticles(ParticleTypes.LAVA, getX(), getY(), getZ(), 5, 0, 1, 0, 0);
        }

        getPlayerOwner().playSound(SoundEvents.GENERIC_BURN, .5f, 1f);
        remove(RemovalReason.KILLED);

        return 0;
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 1)
    )
    private boolean fallOutsideLiquid(FluidState instance, TagKey<Fluid> tag, Operation<Boolean> original) {
        return original.call(instance, tag) || (!this.level().isClientSide() && instance.is(FluidTags.LAVA));
    }

    @WrapOperation(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private boolean replaceLava(BlockState instance, Object o, Operation<Boolean> original) {
        return original.call(instance, o) || (!this.level().isClientSide() && instance.is(Blocks.LAVA));
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
    private ParticleOptions replaceLavaParticle(ParticleOptions particle, @Local ServerLevel world, @Local(argsOnly = true) BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getFluidState().is(FluidTags.LAVA)) {
            return ParticleTypes.LAVA;
        }
        return particle;
    }

    @WrapOperation(
        method = "catchingFish",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;canSeeSky(Lnet/minecraft/core/BlockPos;)Z"
        )
    )
    public boolean isSkyVisible(Level instance, BlockPos pos, Operation<Boolean> original) {
        // The sky is never visible, dont punish players for not fishing in a sky visible spot
        if (!instance.dimensionType().hasSkyLight()) {
            return true;
        }
        return original.call(instance, pos);
    }

    @WrapOperation(
            method = "getOpenWaterTypeForBlock(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/entity/projectile/FishingHook$OpenWaterType;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean isInValidLiquid(FluidState instance, TagKey<Fluid> tag, Operation<Boolean> original) {
        return instance.is(FluidTags.LAVA) || original.call(instance, tag);
    }
}
