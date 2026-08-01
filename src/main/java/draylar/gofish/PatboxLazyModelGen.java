package draylar.gofish;

import com.google.common.hash.HashCode;
import com.mojang.math.Transformation;
import draylar.gofish.registry.GoFishBlocks;
import draylar.gofish.registry.GoFishItems;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.extras.api.format.item.ItemAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.BasicItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.CompositeItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.ConditionItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.SpecialItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.bool.FishingRodCastProperty;
import eu.pb4.polymer.resourcepack.extras.api.format.item.special.EndCubeSpecialModel;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.FishingRodItem;
import org.joml.Matrix4f;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class PatboxLazyModelGen {

    public static void run() {
        BiConsumer<Identifier, String> assetWriter = (path, data) -> {
            try {
                Files.writeString(FabricLoader.getInstance().getGameDir().resolve("../src/main/resources/").resolve(AssetPaths.itemAsset(path)), data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        for (var item : BuiltInRegistries.ITEM) {
            var id = BuiltInRegistries.ITEM.getKey(item);
            if (!id.getNamespace().equals("gofish")) {
                continue;
            }

            if (item == GoFishBlocks.ASTRAL_CRATE.asItem()) {
                assetWriter.accept(id, new ItemAsset(new CompositeItemModel(List.of(
                        new BasicItemModel(id.withPrefix("item/")),
                        new SpecialItemModel(id.withPrefix("item/"), new EndCubeSpecialModel(EndCubeSpecialModel.Type.PORTAL),
                                Optional.of(new Transformation(new Matrix4f().translate(0.01f, 0.01f, 0.01f).scale(0.98f))))
                ), Optional.empty()), ItemAsset.Properties.DEFAULT).toJson());
            } else if (item instanceof FishingRodItem) {
                assetWriter.accept(id, new ItemAsset(new ConditionItemModel(new FishingRodCastProperty(),
                        new BasicItemModel(id.withPrefix("item/").withSuffix("_cast")), new BasicItemModel(id.withPrefix("item/"))),
                        ItemAsset.Properties.DEFAULT).toJson());
            } else {
                assetWriter.accept(id, new ItemAsset(new BasicItemModel(id.withPrefix("item/")), ItemAsset.Properties.DEFAULT).toJson());
            }
        }
    }
}
