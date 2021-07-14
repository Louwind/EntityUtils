package github.Louwind.entityutils.core.mixin;

import github.Louwind.entityutils.core.util.FabricSignType;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.SignType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityModelLayers.class)
public abstract class MixinEntityModelLayers {

    @Inject(method = "createSign", at = @At("HEAD"), cancellable = true)
    private static void createSign(SignType type, CallbackInfoReturnable<EntityModelLayer> cir) {

        if(type instanceof FabricSignType) {
            var signType = (FabricSignType) type;

            cir.setReturnValue(new EntityModelLayer(signType.getLayer(), "main"));
        }

    }

}
