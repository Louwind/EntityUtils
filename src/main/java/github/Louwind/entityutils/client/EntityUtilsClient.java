package github.Louwind.entityutils.client;

import github.Louwind.entityutils.core.util.FabricBoatType;
import github.Louwind.entityutils.core.util.FabricSignType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.impl.client.renderer.registry.EntityModelLayerImpl;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.SignType;

import static net.minecraft.client.render.TexturedRenderLayers.SIGNS_ATLAS_TEXTURE;

public class EntityUtilsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientSpriteRegistryCallback.event(SIGNS_ATLAS_TEXTURE).register((atlas, registry) -> SignType.stream()
                .filter(FabricSignType.class::isInstance)
                .map(FabricSignType.class::cast)
                .map(FabricSignType::getTexture)
                .forEach(registry::register));

        FabricBoatType.stream()
                .map(FabricBoatType::getLayer)
                .map(layer -> new EntityModelLayer(layer, "main"))
                .forEach(layer -> EntityModelLayerImpl.PROVIDERS.put(layer, BoatEntityModel::getTexturedModelData));
    }

}