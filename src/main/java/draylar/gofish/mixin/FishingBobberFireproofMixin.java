package draylar.gofish.mixin;

import draylar.gofish.api.FireproofEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingHook.class)
public abstract class FishingBobberFireproofMixin extends Entity implements FireproofEntity {

    @Unique
    private boolean fireproof = false;

    private FishingBobberFireproofMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(
            method = "defineSynchedData",
            at = @At("RETURN"))
    private void registerFireImmuneTracker(CallbackInfo ci) {
    }

    // todo: inject into super instead
    @Override
    public boolean isOnFire() {
        if(this.fireproof) {
            return false;
        }

        return super.isOnFire();
    }

    @Override
    public boolean gf_isFireproof() {
        return this.fireproof;
    }

    @Override
    public void gf_setFireproof(boolean value) {
        this.fireproof = value;
    }
}
