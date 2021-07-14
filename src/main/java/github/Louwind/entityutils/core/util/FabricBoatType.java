package github.Louwind.entityutils.core.util;

import com.google.common.collect.Maps;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.stream.Stream;

public class FabricBoatType {

    private static final Map<Identifier, FabricBoatType> VALUES = Maps.newHashMap();

    public static FabricBoatType create(String name) {
        var id = new Identifier(name);
        var namespace = id.getNamespace();
        var path = id.getPath();

        var layer = namespace + ":boat/" + path;
        var lootTableId = new Identifier(namespace + ":entities/boat/" + path);
        var texture = new Identifier(namespace + ":textures/entities/boat/" + path + ".png");

        var boatType = new FabricBoatType(name, lootTableId, layer, texture);

        return VALUES.put(id, boatType);
    }

    public static FabricBoatType getBoatType(String id) {
        return VALUES.get(new Identifier(id));
    }

    public static Stream<FabricBoatType> stream() {
        return VALUES.values().stream();
    }

    private final String layer;
    private final String name;

    private final Identifier lootTableId;
    private final Identifier texture;

    private FabricBoatType(String name, Identifier lootTableId, String layer, Identifier texture) {
        this.lootTableId = lootTableId;
        this.layer = layer;
        this.name = name;
        this.texture = texture;
    }

    public String getLayer() {
        return this.layer;
    }

    public Identifier getLootTableId() {
        return this.lootTableId;
    }

    public Identifier getTexture() {
        return this.texture;
    }

    @Override
    public String toString() {
        return this.name;
    }

}