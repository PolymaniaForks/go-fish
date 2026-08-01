package draylar.gofish.loot.biome;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.level.biome.Biome;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record BiomePredicate(List<ResourceKey<Biome>> valid) {

    public static final Codec<BiomePredicate> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ResourceKey.codec(Registries.BIOME).listOf().fieldOf("valid").forGetter(BiomePredicate::valid)
                    )
                    .apply(instance, BiomePredicate::new)
    );
    public static final BiomePredicate EMPTY = new BiomePredicate(Collections.emptyList());

    public static BiomePredicate create(List<ResourceKey<Biome>> valid) {
        return new BiomePredicate(valid);
    }

    public List<ResourceKey<Biome>> getValid() {
        return valid;
    }

    public boolean test(Holder<Biome> biome) {
        for (ResourceKey<Biome> key : valid) {
            if (biome.is(key)) {
                return true;
            }
        }
        return false;
    }

    public JsonElement toJson() {
        return CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow(IllegalStateException::new);
    }

    public static BiomePredicate fromJson(JsonElement json) {
        return CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(IllegalStateException::new);
    }

    public static class Builder {

        private List<ResourceKey<Biome>> valid;

        public static Builder create() {
            return new BiomePredicate.Builder();
        }

        public Builder setValid(List<ResourceKey<Biome>> valid) {
            this.valid = valid;
            return this;
        }

        public Builder setValidFromString(List<String> valid) {
            List<ResourceKey<Biome>> rKeys = new ArrayList<>();
            for (String str : valid) {
                if (!valid.isEmpty()) {
                    rKeys.add(ResourceKey.create(Registries.BIOME, Identifier.parse(str)));
                }
            }

            return setValid(rKeys);
        }

        public Builder add(ResourceKey<Biome> biome) {
            valid.add(biome);
            return this;
        }

        public Builder add(String biome) {
            if(!biome.isEmpty()) {
                valid.add(ResourceKey.create(Registries.BIOME, Identifier.parse(biome)));
            }

            return this;
        }

        public Builder of(BiomePredicate biomePredicate) {
            this.valid = biomePredicate.valid;
            return this;
        }

        public BiomePredicate build() {
            return new BiomePredicate(this.valid);
        }
    }
}

