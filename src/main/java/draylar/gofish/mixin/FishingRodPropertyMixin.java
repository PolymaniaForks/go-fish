package draylar.gofish.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import draylar.gofish.api.ExperienceBobber;
import draylar.gofish.api.FireproofEntity;
import draylar.gofish.api.FishingBonus;
import draylar.gofish.api.SmeltingBobber;
import draylar.gofish.item.ExtendedFishingRodItem;
import draylar.gofish.registry.GoFishEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

@Mixin(FishingRodItem.class)
public class FishingRodPropertyMixin {

    @Unique private Player player;
    @Unique private ItemStack heldStack;

    @Inject(method = "use", at = @At("HEAD"))
    private void storeContext(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        this.heldStack = user.getItemInHand(hand);
        this.player = user;
    }

    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectile(Lnet/minecraft/world/entity/projectile/Projectile;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/projectile/Projectile;"))
    private Projectile modifyBobber(Projectile entity, ServerLevel world, ItemStack projectileStack, Operation<Projectile> operation) {
        if(entity instanceof FishingHook bobber) {
            modifyBobber(world, bobber);
        }

        return operation.call(entity, world, projectileStack);
    }

    @Unique
    private void modifyBobber(Level world, FishingHook bobber) {
        boolean smeltBuff = false;
        int bonusLure = 0;
        int bonusLuck = 0;
        int bonusExperience = 0;

        // Find buffing items in player inventory
        List<FishingBonus> found = new ArrayList<>();
        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            Item item = stack.getItem();

            if (item instanceof FishingBonus bonus) {
                if (!found.contains(bonus)) {
                    if(bonus.shouldApply(world, player)) {
                        found.add(bonus);
                        smeltBuff = bonus.providesAutosmelt() || smeltBuff;
                        bonusLure += bonus.getLure();
                        bonusLuck += bonus.getLuckOfTheSea();
                        bonusExperience += bonus.getBaseExperience();
                    }
                }
            }
        }

        // Check if this rod autosmelts
        boolean hasDeepfryEnchantment = EnchantmentHelper.has(heldStack, GoFishEnchantments.DEEPFRY);
        boolean rodAutosmelts = heldStack.getItem() instanceof ExtendedFishingRodItem && ((ExtendedFishingRodItem) heldStack.getItem()).autosmelts();
        boolean smelts = hasDeepfryEnchantment || rodAutosmelts || smeltBuff;

        // Modify bobber statistics
        ((FireproofEntity) bobber).gf_setFireproof(false);
        ((SmeltingBobber) bobber).gf_setSmelts(smelts);
        ((ExperienceBobber) bobber).gf_setBaseExperience(1 + bonusExperience);
        FishingBobberEntityAccessor accessor = (FishingBobberEntityAccessor) bobber;
        accessor.setLureSpeed(Math.min((accessor.getLureSpeed() + bonusLure),5));
        accessor.setLuck(accessor.getLuck() + bonusLuck);
    }
}
