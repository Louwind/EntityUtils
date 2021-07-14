package github.Louwind.entityutils.core.item;

import github.Louwind.entityutils.core.entity.FabricBoatEntity;
import github.Louwind.entityutils.core.util.FabricBoatType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.function.Predicate;

import static net.minecraft.stat.Stats.USED;
import static net.minecraft.util.hit.HitResult.Type.BLOCK;
import static net.minecraft.util.hit.HitResult.Type.MISS;
import static net.minecraft.world.RaycastContext.FluidHandling.ANY;

public class FabricBoatItem extends Item {

    protected static final Predicate<Entity> RIDERS = EntityPredicates.EXCEPT_SPECTATOR.and(Entity::collides);

    protected final EntityType<? extends FabricBoatEntity> entityType;
    protected final FabricBoatType type;

    public FabricBoatItem(EntityType<? extends FabricBoatEntity> entityType, FabricBoatType type, Settings settings) {
        super(settings);

        this.entityType = entityType;
        this.type = type;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var itemStack = user.getStackInHand(hand);
        var hitResult = raycast(world, user, ANY);

        if (hitResult.getType() != MISS) {
            var vec3d = user.getRotationVec(1.0F);
            var entities = world.getOtherEntities(user, user.getBoundingBox().stretch(vec3d.multiply(5)).expand(1), RIDERS);

            if (!entities.isEmpty()) {
                var vec3d2 = user.getEyePos();

                for (Entity entity : entities) {
                    var box = entity.getBoundingBox().expand(entity.getTargetingMargin());

                    if (box.contains(vec3d2))
                        return TypedActionResult.pass(itemStack);
                }

            }

            if (hitResult.getType() == BLOCK) {
                var boat = new FabricBoatEntity(this.entityType, world, hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z);

                boat.setFabricBoatType(this.type);
                boat.setYaw(user.getYaw());

                if (!world.isSpaceEmpty(boat, boat.getBoundingBox().expand(-0.1D)))
                    return TypedActionResult.fail(itemStack);

                if (!world.isClient) {
                    world.spawnEntity(boat);
                    world.emitGameEvent(user, GameEvent.ENTITY_PLACE, new BlockPos(hitResult.getPos()));

                    if (!user.getAbilities().creativeMode)
                        itemStack.decrement(1);
                }

                user.incrementStat(USED.getOrCreateStat(this));

                return TypedActionResult.success(itemStack, world.isClient());
            }

        }

        return TypedActionResult.pass(itemStack);
    }

}