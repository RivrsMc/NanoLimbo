package ua.nanit.limbo.protocol.registry;

import lombok.Getter;
import net.kyori.adventure.key.Key;

@Getter
public enum EntityType {
    ACACIA_BOAT(0, "minecraft:acacia_boat"),
    ACACIA_BOAT_CHEST(1, "minecraft:acacia_boat_chest"),
    ALLAY(2, "minecraft:allay"),
    AREA_EFFECT_CLOUD(3, "minecraft:area_effect_cloud"),
    ARMADILLO(4, "minecraft:armadillo"),
    ARMOR_STAND(5, "minecraft:armor_stand"),
    ARROW(6, "minecraft:arrow"),
    AXOLOTL(7, "minecraft:axolotl"),
    ;

    private final int id;
    private final Key key;

    EntityType(int id, String key) {
        this.id = id;
        this.key = Key.key(key);
    }
}
