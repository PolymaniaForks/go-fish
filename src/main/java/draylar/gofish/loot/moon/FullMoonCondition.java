package draylar.gofish.loot.moon;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import draylar.gofish.registry.GoFishLoot;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import java.util.Set;

public record FullMoonCondition() implements LootItemCondition {

    public static final FullMoonCondition INSTANCE = new FullMoonCondition();
    public static final MapCodec<FullMoonCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<? extends LootItemCondition> codec() {
        return GoFishLoot.FULL_MOON;
    }

    @Override
    public boolean test(LootContext lootContext) {
        Entity entity = lootContext.getOptionalParameter(LootContextParams.THIS_ENTITY);

        if(entity != null) {
            //var angle = entity.getEntityWorld().getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.MOON_ANGLE_VISUAL, entity.getEntityPos());
            var phase = entity.level().environmentAttributes().getValue(EnvironmentAttributes.MOON_PHASE, entity.position());
            return phase == MoonPhase.FULL_MOON && entity.level().isDarkOutside();
        }

        return false;
    }

    public static LootItemCondition.Builder builder() {
        return () -> INSTANCE;
    }
}