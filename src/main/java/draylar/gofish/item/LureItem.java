package draylar.gofish.item;

import draylar.gofish.api.FishingBonus;
import eu.pb4.polymer.core.api.item.PolymerItem;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class LureItem extends Item implements FishingBonus, PolymerItem {

    private final int lure;

    public LureItem(Properties settings, int lure) {
        super(settings);
        this.lure = lure;
    }

    @Override
    public int getLure() {
        return lure;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        super.appendHoverText(stack, context, displayComponent, textConsumer, type);

        for(int i = 1; i <= 2; i++) {
            textConsumer.accept(Component.translatable(String.format("gofish.lure.tooltip_%d", i), lure).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
        return Items.TRIAL_KEY;
    }
}
