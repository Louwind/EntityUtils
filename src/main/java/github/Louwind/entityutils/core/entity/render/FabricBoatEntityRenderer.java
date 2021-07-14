package github.Louwind.entityutils.core.entity.render;

import com.mojang.datafixers.util.Pair;
import github.Louwind.entityutils.core.entity.FabricBoatEntity;
import github.Louwind.entityutils.core.util.FabricBoatType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.minecraft.client.render.OverlayTexture.DEFAULT_UV;
import static net.minecraft.util.math.Vec3f.POSITIVE_X;
import static net.minecraft.util.math.Vec3f.POSITIVE_Y;

public class FabricBoatEntityRenderer<T extends FabricBoatEntity> extends EntityRenderer<T> {

    private final Map<FabricBoatType, Pair<Identifier, BoatEntityModel>> texturesAndModels;

    public FabricBoatEntityRenderer(Context context) {
        super(context);

        this.shadowRadius = 0.8F;
        this.texturesAndModels = FabricBoatType.stream().collect(Collectors.toMap(Function.identity(), type -> {
            var layer = EntityModelLayers.create(type.getLayer(), "main");
            var model = new BoatEntityModel(context.getPart(layer));

            return Pair.of(type.getTexture(), model);
        }));
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(0, 0.375D, 0);
        matrices.multiply(POSITIVE_Y.getDegreesQuaternion(180 - yaw));

        var h = (float) entity.getDamageWobbleTicks() - tickDelta;
        var j = entity.getDamageWobbleStrength() - tickDelta;

        if (j < 0)
            j = 0;

        if (h > 0)
            matrices.multiply(POSITIVE_X.getDegreesQuaternion(MathHelper.sin(h) * h * j / 10.0F * (float)entity.getDamageWobbleSide()));

        var k = entity.interpolateBubbleWobble(tickDelta);

        if (!MathHelper.approximatelyEquals(k, 0.0F))
            matrices.multiply(new Quaternion(new Vec3f(1.0F, 0.0F, 1.0F), entity.interpolateBubbleWobble(tickDelta), true));

        var boatType = entity.getFabricBoatType();
        var pair = this.texturesAndModels.get(boatType);
        var texture = pair.getFirst();
        var entityModel = pair.getSecond();

        matrices.scale(-1, -1, 1);
        matrices.multiply(POSITIVE_Y.getDegreesQuaternion(90.0F));
        entityModel.setAngles(entity, tickDelta, 0.0F, -0.1F, 0.0F, 0.0F);

        var vertexConsumer = vertexConsumers.getBuffer(entityModel.getLayer(texture));

        entityModel.render(matrices, vertexConsumer, light, DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);

        if (!entity.isSubmergedInWater())
            entityModel.getWaterPatch().render(matrices, vertexConsumers.getBuffer(RenderLayer.getWaterMask()), light, DEFAULT_UV);

        matrices.pop();

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(T entity) {
        var boatType = entity.getFabricBoatType();

        return this.texturesAndModels.get(boatType).getFirst();
    }

}