package github.Louwind.entityutils.core.util;

import com.google.common.collect.Maps;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.stream.Stream;

public record FabricBoatType(String name, Identifier lootTableId, Identifier layer, Identifier texture) {

    private static final Map<Identifier, FabricBoatType> VALUES = Maps.newHashMap();

    public static FabricBoatType create(String name) {
        var id = new Identifier(name);
        var namespace = id.getNamespace();
        var path = id.getPath();

        var layer = new Identifier(namespace + ":boat/" + path);
        var lootTableId = new Identifier(namespace + ":entities/boat/" + path);
        var texture = new Identifier(namespace + ":textures/entity/boat/" + path + ".png");

        var boatType = new FabricBoatType(name, lootTableId, layer, texture);

        VALUES.put(id, boatType);

        return boatType;
    }

    public static FabricBoatType getBoatType(String id) {
        return VALUES.get(new Identifier(id));
    }

    public static Stream<FabricBoatType> stream() {
        return VALUES.values().stream();
    }

    @Override
    public String toString() {
        return this.name;
    }

}