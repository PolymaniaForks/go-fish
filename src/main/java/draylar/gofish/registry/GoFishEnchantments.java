package draylar.gofish.registry;

import draylar.gofish.GoFish;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Unit;

public class GoFishEnchantments {

    public static final DataComponentType<Unit> DEEPFRY = register("deepfry", DataComponentType.<Unit>builder().persistent(Unit.CODEC).build());

    public static <A, T extends DataComponentType<A>> T register(String name, T enchantment) {
        PolymerComponent.registerEnchantmentEffectComponent(enchantment);
        return Registry.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, GoFish.id(name), enchantment);
    }

    public static void init() {
        // NO-OP
    }

    private GoFishEnchantments() {
        // NO-OP
    }
}
