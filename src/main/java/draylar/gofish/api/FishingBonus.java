package draylar.gofish.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface FishingBonus {

    default int getLure() {
        return 0;
    }

    default int getLuckOfTheSea() {
        return 0;
    }

    default int getBaseExperience() {
        return 0;
    }

    default boolean providesAutosmelt() {
        return false;
    }

    default boolean shouldApply(Level world, Player player) {
        return true;
    }
}
