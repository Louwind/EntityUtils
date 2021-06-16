package github.Louwind.entityutils.client;

import github.Louwind.entityutils.core.util.FabricSignType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
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
    }

}