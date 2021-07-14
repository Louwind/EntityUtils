package github.Louwind.entityutils.core.util;

import github.Louwind.entityutils.core.mixin.InvokerSignType;
import net.minecraft.util.Identifier;
import net.minecraft.util.SignType;

public class FabricSignType extends SignType {

    public static SignType create(String id, String entityType) {
        var identifier = new Identifier(id);
        var namespace = identifier.getNamespace();
        var name = identifier.getPath();

        var layer = new Identifier(namespace + ":signs/" + name);
        var texture = new Identifier(namespace + ":entity/signs/" + name);

        return InvokerSignType.register(new FabricSignType(namespace, new Identifier(entityType), layer, texture));
    }

    private final Identifier entityType;
    private final Identifier layer;
    private final Identifier texture;

    private FabricSignType(String name, Identifier entityType, Identifier layer, Identifier texture) {
        super(name);

        this.entityType = entityType;
        this.layer = layer;
        this.texture = texture;
    }

    public Identifier getEntityType() {
        return this.entityType;
    }

    public Identifier getLayer() {
        return this.layer;
    }

    public Identifier getTexture() {
        return this.texture;
    }

}