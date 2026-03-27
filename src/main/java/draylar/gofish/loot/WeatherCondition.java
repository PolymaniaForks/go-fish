package draylar.gofish.loot;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import draylar.gofish.registry.GoFishLoot;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

public record WeatherCondition(Optional<Boolean> raining, Optional<Boolean> thundering, Optional<Boolean> snowing) implements LootItemCondition {

    public static final MapCodec<WeatherCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codec.BOOL.optionalFieldOf("raining").forGetter(WeatherCondition::raining),
                            Codec.BOOL.optionalFieldOf("thundering").forGetter(WeatherCondition::thundering),
                            Codec.BOOL.optionalFieldOf("snowing").forGetter(WeatherCondition::snowing)
                    )
                    .apply(instance, WeatherCondition::new)
    );

    @Override
    public MapCodec<? extends LootItemCondition> codec() {
        return GoFishLoot.WEATHER;
    }


    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.THIS_ENTITY, LootContextParams.ORIGIN);
    }

    @Override
    public boolean test(LootContext lootContext) {
        @Nullable Entity entity = lootContext.getOptionalParameter(LootContextParams.THIS_ENTITY);
        @Nullable Vec3 pos = lootContext.getOptionalParameter(LootContextParams.ORIGIN);

        if(entity != null && pos != null) {
            Level world = entity.level();

            // If raining is required and the world is not raining, return false.
            if (raining.isPresent() && raining.get() && !world.isRaining()) {
                return false;
            }

            // If thundering is required and the world is not raining, return false.
            if (thundering.isPresent() && thundering.get() && !world.isThundering()) {
                return false;
            }

            // same check for snowing
            if (snowing.isPresent() && snowing.get()) {
                // >= .15 = no snow
                if(world.getBiome(entity.blockPosition()).value().warmEnoughToRain(new BlockPos((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z)), world.getSeaLevel())) {
                    return false;
                }

                return world.isRaining();
            }

            // Both conditions match, return true.
            return true;
        }

        return false;
    }

    public static LootItemCondition.Builder builder(boolean raining, boolean thundering, boolean snowing) {
        return () -> new WeatherCondition(Optional.of(raining), Optional.of(thundering), Optional.of(snowing));
    }
}
