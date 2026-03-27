package draylar.gofish.item;

import draylar.gofish.api.FishingBonus;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;

public class SoulLureItem extends Item implements FishingBonus, PolymerItem {

    public SoulLureItem(Properties settings) {
        super(settings);
    }

    @Override
    public int getLuckOfTheSea() {
        return 1;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        super.appendHoverText(stack, context, displayComponent, textConsumer, type);

        textConsumer.accept(Component.translatable(String.format("gofish.lure.tooltip_%d", 1)).withStyle(ChatFormatting.GRAY));
        textConsumer.accept(Component.translatable(String.format("gofish.lots.tooltip_%d", 2), 1, " in Soul Sand Valley").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean shouldApply(Level world, Player player) {
        return world.getBiome(player.blockPosition()).is(Biomes.SOUL_SAND_VALLEY);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
        return Items.TRIAL_KEY;
    }
}
