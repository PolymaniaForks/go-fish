package draylar.gofish.mixin;

import draylar.gofish.api.SmeltingBobber;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;

@Mixin(FishingHook.class)
public abstract class FishingBobberAutosmeltMixin extends Entity implements SmeltingBobber {

    private FishingBobberAutosmeltMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Unique
    private boolean gf_smelts = false;

    @Override
    public boolean gf_canSmelt() {
        return gf_smelts;
    }

    @Override
    public void gf_setSmelts(boolean value) {
        this.gf_smelts = value;
    }

    @ModifyVariable(
            method = "retrieve",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V",
                    shift = At.Shift.AFTER
            ),
            index = 9
    )
    private ItemEntity processOutput(ItemEntity itemEntity) {
        if (this.level() instanceof ServerLevel world) {
            if (gf_smelts) {
                Optional<RecipeHolder<SmeltingRecipe>> cooked = world.recipeAccess().getRecipeFor(
                        RecipeType.SMELTING,
                        new SingleRecipeInput(itemEntity.getItem()),
                        level()
                );

                cooked.ifPresent(smeltingRecipe -> itemEntity.setItem(smeltingRecipe.value().assemble(
                        new SingleRecipeInput(itemEntity.getItem()))));
            }
        }

        return itemEntity;
    }
}
