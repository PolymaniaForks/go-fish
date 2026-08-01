package draylar.gofish.loot;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import draylar.gofish.registry.GoFishLoot;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public record MatchFishingRodCondition(List<Identifier> rods) implements LootItemCondition {

    public static final MapCodec<MatchFishingRodCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Identifier.CODEC.listOf().fieldOf("rods").forGetter(MatchFishingRodCondition::rods)
                    )
                    .apply(instance, MatchFishingRodCondition::new)
    );

    @Override
    public MapCodec<? extends LootItemCondition> codec() {
        return GoFishLoot.MATCH_FISHING_ROD;
    }

    @Override
    public Set<net.minecraft.util.context.ContextKey<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.TOOL);
    }

    @Override
    public boolean test(LootContext lootContext) {
        return Optional.ofNullable(lootContext.getOptionalParameter(LootContextParams.TOOL))
                .map(ItemInstance::typeHolder)
                .map(holder -> rods.stream().anyMatch(holder::is))
                .orElse(false);
    }

    public static LootItemCondition.Builder builder(Identifier... rods) {
        return () -> new MatchFishingRodCondition(List.of(rods));
    }
}