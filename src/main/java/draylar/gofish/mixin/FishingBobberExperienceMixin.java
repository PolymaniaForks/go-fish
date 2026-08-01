package draylar.gofish.mixin;

import draylar.gofish.api.ExperienceBobber;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This mixin is responsible for allowing Fishing Bobbers to have a customizable amount of base experience gain per catch.
 * For usage, cast a {@link FishingHook} to {@link ExperienceBobber}, and manipulate the base experience through the setter provided.
 */
@Mixin(FishingHook.class)
public abstract class FishingBobberExperienceMixin extends Entity implements ExperienceBobber {

    @Shadow public abstract Player getPlayerOwner();

    @Unique
    private int gf_baseExperience = 1;

    private FishingBobberExperienceMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Override
    public int gf_getBaseExperience() {
        return gf_baseExperience;
    }

    @Override
    public void gf_setBaseExperience(int experience) {
        this.gf_baseExperience = experience;
    }

    @Redirect(
            method = "retrieve",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", ordinal = 1)
    )
    private boolean modifyExperience(Level world, Entity entity) {
        ServerPlayer player = (ServerPlayer) getPlayerOwner();
        return player.level().addFreshEntity(new ExperienceOrb(player.level(), player.getX(), player.getY() + 0.5D, player.getZ() + 0.5D, this.random.nextInt(6) + gf_baseExperience));
    }
}
