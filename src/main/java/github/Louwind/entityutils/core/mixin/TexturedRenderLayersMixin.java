package github.Louwind.entityutils.core.mixin;

import github.Louwind.entityutils.core.util.FabricSignType;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.SignType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TexturedRenderLayers.class)
public abstract class TexturedRenderLayersMixin {

    @Shadow
    @Final
    public static Identifier SIGNS_ATLAS_TEXTURE;

    @Inject(method = "getSignTextureId", at = @At("HEAD"), cancellable = true)
    private static void createSignTextureId(SignType type, CallbackInfoReturnable<SpriteIdentifier> cir) {

        if(type instanceof FabricSignType) {
            var signType = (FabricSignType) type;
            var texture = signType.getTexture();

            cir.setReturnValue(new SpriteIdentifier(SIGNS_ATLAS_TEXTURE, texture));
        }

    }

}