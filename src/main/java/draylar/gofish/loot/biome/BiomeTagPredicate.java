package draylar.gofish.loot.biome;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record BiomeTagPredicate(List<TagKey<Biome>> valid) {

    public static final Codec<BiomeTagPredicate> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            TagKey.codec(Registries.BIOME).listOf().fieldOf("valid").forGetter(BiomeTagPredicate::valid)
                    )
                    .apply(instance, BiomeTagPredicate::new)
    );
    public static final BiomeTagPredicate EMPTY = new BiomeTagPredicate(Collections.emptyList());

    public static BiomeTagPredicate create(List<TagKey<Biome>> valid) {
        return new BiomeTagPredicate(valid);
    }

    public List<TagKey<Biome>> getValid() {
        return valid;
    }

    public boolean test(Holder<Biome> biome) {

        for(TagKey<Biome> tag : valid) {
            if(biome.is(tag)) {
                return true;
            }
        }

        return false;
    }

    public JsonElement toJson() {
        return CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow(IllegalStateException::new);
    }

    public static BiomeTagPredicate fromJson(@Nullable JsonElement json) {
        return CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(IllegalStateException::new);
    }

    public static class Builder {

        private List<TagKey<Biome>> valid = new ArrayList<>();

        public static Builder create() {
            return new BiomeTagPredicate.Builder();
        }

        public Builder setValid(List<TagKey<Biome>> valid) {
            this.valid = valid;
            return this;
        }

        public Builder setValidByString(List<String> valid) {
            List<TagKey<Biome>> tagKeys = new ArrayList<>();
            for (String str : valid) {
                tagKeys.add(TagKey.create(Registries.BIOME, Identifier.parse(str)));
            }
            return setValid(tagKeys);
        }

        public Builder add(String tag) {
            if(!tag.isEmpty()) {
                this.valid.add(TagKey.create(Registries.BIOME, Identifier.parse(tag)));
            }

            return this;
        }

        public Builder of(BiomeTagPredicate biomePredicate) {
            this.valid = biomePredicate.valid;
            return this;
        }

        public BiomeTagPredicate build() {
            return new BiomeTagPredicate(this.valid);
        }
    }
}
