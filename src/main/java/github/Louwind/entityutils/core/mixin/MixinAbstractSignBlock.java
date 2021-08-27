package github.Louwind.entityutils.core.mixin;

import github.Louwind.entityutils.core.util.FabricSignType;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.SignType;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.util.registry.Registry.BLOCK_ENTITY_TYPE;

@Mixin(AbstractSignBlock.class)
public class MixinAbstractSignBlock {

    @Shadow
    @Final
    public SignType type;

    @Inject(method = "createBlockEntity", at = @At("HEAD"), cancellable = true)
    private void createBlockEntity(BlockPos pos, BlockState state, CallbackInfoReturnable<BlockEntity> cir) {

        if(this.type instanceof FabricSignType type) {
            var id = type.getEntityType();
            var blockEntityType = BLOCK_ENTITY_TYPE.get(id);

            if(blockEntityType != null)
                cir.setReturnValue(blockEntityType.instantiate(pos, state));
        }

    }

}