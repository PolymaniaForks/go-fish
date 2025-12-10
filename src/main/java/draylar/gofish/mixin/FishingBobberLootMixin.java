package draylar.gofish.mixin;

import draylar.gofish.api.FireproofEntity;
import draylar.gofish.impl.GoFishLootTables;
import draylar.gofish.registry.GoFishTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;

@Mixin(FishingHook.class)
public abstract class FishingBobberLootMixin extends Entity {

    private FishingBobberLootMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @ModifyArg(
            method = "retrieve",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerRegistries$Holder;getLootTable(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/storage/loot/LootTable;"))
    private ResourceKey<LootTable> getTable(ResourceKey<LootTable> key) {
        assert level().getServer() != null;

        final var dimension = level().dimensionTypeRegistration();
        if (dimension.is(GoFishTags.NETHER_FISHING)) {
            return GoFishLootTables.NETHER_FISHING;
        } else if (dimension.is(GoFishTags.END_FISHING)) {
            return GoFishLootTables.END_FISHING;
        }

        // Default
        return BuiltInLootTables.FISHING;
    }

    @Inject(
            method = "retrieve",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void setFireproof(ItemStack usedItem, CallbackInfoReturnable<Integer> cir, Player playerEntity, int i, LootParams lootWorldContext, LootTable lootTable, List list, Iterator var7, ItemStack itemStack, ItemEntity itemEntity, double d, double e, double f, double g) {
        // If the user is fishing in the nether, tell the dropped loot to ignore lava/fire burning until pickup
        if(level().dimensionTypeRegistration().is(GoFishTags.NETHER_FISHING)) {
            ((FireproofEntity) itemEntity).gf_setFireproof(true);
        }
    }
}
