package draylar.gofish.mixin;

import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FishingHook.class)
public interface FishingBobberEntityAccessor {
    @Accessor
    int getLuck();

    @Mutable
    @Accessor
    void setLuck(int luckBonus);

    @Accessor
    int getLureSpeed();

    @Mutable
    @Accessor
    void setLureSpeed(int ticks);
}
