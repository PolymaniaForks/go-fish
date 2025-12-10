package draylar.gofish.mixin;

import draylar.gofish.item.ExtendedFishingRodItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingHook.class)
public abstract class FishingBobberValidityMixin extends Entity {

    @Shadow public abstract Player getPlayerOwner();

    private FishingBobberValidityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    /**
     * Patches {@link FishingHook#shouldStopFishing(Player)} to work for all items of the type {@link FishingRodItem}.
     *
     * @param playerEntity  owner of this {@link FishingHook}
     * @param cir  mixin callback info
     */
    @Inject(
            method = "shouldStopFishing",
            at = @At("HEAD"),
            cancellable = true
    )
    private void removeIfInvalid(Player playerEntity, CallbackInfoReturnable<Boolean> cir) {
        ItemStack mainHandStack = playerEntity.getMainHandItem();
        ItemStack offHandStack = playerEntity.getOffhandItem();

        boolean mainHandHasRod = mainHandStack.getItem() instanceof ExtendedFishingRodItem;
        boolean offHandHasRod = offHandStack.getItem() instanceof ExtendedFishingRodItem;

        if (!playerEntity.isRemoved() && playerEntity.isAlive() && (mainHandHasRod || offHandHasRod) && this.distanceToSqr(playerEntity) <= 1024.0D) {
            cir.setReturnValue(false);
        }
    }
}
