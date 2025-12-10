package draylar.gofish.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import draylar.gofish.api.FireproofEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityFireproofMixin extends Entity implements FireproofEntity {

    @Unique
    private boolean fireImmune = false;

    public ItemEntityFireproofMixin(EntityType<?> type, Level world) {
        super(type, world);
    }


    @ModifyReturnValue(
            method = "fireImmune",
            at = @At("RETURN"))
    private boolean isLavaFishingLoot(boolean original) {
        return original || this.fireImmune;
    }

    @Override
    public boolean isOnFire() {
        if(gf_isFireproof()) {
            return false;
        }

        return super.isOnFire();
    }

    @Override
    public boolean gf_isFireproof() {
        return this.fireImmune;
    }

    @Override
    public void gf_setFireproof(boolean value) {
        this.fireImmune = value;
    }
}
