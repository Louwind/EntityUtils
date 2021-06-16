package github.Louwind.entityutils.core.block.entity.render;

import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.SignType;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.minecraft.block.SignBlock.ROTATION;
import static net.minecraft.block.WallSignBlock.FACING;
import static net.minecraft.util.math.Vec3f.POSITIVE_Y;

public class FabricSignBlockEntityRenderer<T extends SignBlockEntity> implements BlockEntityRenderer<T> {

    private final TextRenderer textRenderer;
    private final Map<SignType, SignBlockEntityRenderer.SignModel> typeToModel;

    public FabricSignBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
        this.typeToModel = SignType.stream().collect(Collectors.toMap(Function.identity(), signType -> {
            var layer = EntityModelLayers.createSign(signType);
            var modelPart = ctx.getLayerModelPart(layer);

            return new SignBlockEntityRenderer.SignModel(modelPart);
        }));
    }

    @Override
    public void render(SignBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var state = entity.getCachedState();

        matrices.push();
        var type = SignBlockEntityRenderer.getSignType(state.getBlock());
        var model = this.typeToModel.get(type);

        if (state.getBlock() instanceof SignBlock) {
            var angle = -((float)(state.get(ROTATION) * 360) / 16.0F);

            matrices.translate(0.5D, 0.5D, 0.5D);
            matrices.multiply(POSITIVE_Y.getDegreesQuaternion(angle));
            model.stick.visible = true;
        } else {
            var angle = -state.get(FACING).asRotation();

            matrices.translate(0.5D, 0.5D, 0.5D);
            matrices.multiply(POSITIVE_Y.getDegreesQuaternion(angle));
            matrices.translate(0.0D, -0.3125D, -0.4375D);
            model.stick.visible = false;
        }

        matrices.push();
        var scale = 0.6666667F;
        var translate = 0.010416667F;
        var spriteIdentifier = TexturedRenderLayers.getSignTextureId(type);
        var vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, model::getLayer);

        matrices.scale(scale, -scale, -scale);
        model.root.render(matrices, vertexConsumer, light, overlay);
        matrices.pop();

        matrices.translate(0.0D, 0.3333333432674408D, 0.046666666865348816D);
        matrices.scale(translate, -translate, translate);

        var orderedTexts = entity.updateSign(MinecraftClient.getInstance().shouldFilterText(), text -> {
            var list = this.textRenderer.wrapLines(text, 90);

            return list.isEmpty() ? OrderedText.EMPTY : list.get(0);
        });

        var outlineColor = SignBlockEntityRenderer.getColor(entity);
        var color = entity.isGlowingText() ? entity.getTextColor().getSignColor() : outlineColor;
        var textLight = entity.isGlowingText() ? 15728880 : light;

        for(int i = 0; i < 4; ++i) {
            var orderedText = orderedTexts[i];
            var x = -this.textRenderer.getWidth(orderedText) / 2;

            if (entity.isGlowingText() && SignBlockEntityRenderer.shouldRender(entity, color))
                this.textRenderer.drawWithOutline(orderedText, x, i * 10 - 20, color, outlineColor, matrices.peek().getModel(), vertexConsumers, textLight);
            else
                this.textRenderer.draw(orderedText, x, i * 10 - 20, color, false, matrices.peek().getModel(), vertexConsumers, false, 0, textLight);
        }

        matrices.pop();
    }

}