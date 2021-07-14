package github.Louwind.entityutils.core.entity;

import github.Louwind.entityutils.core.util.FabricBoatType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

import static net.minecraft.entity.damage.DamageSource.FALL;
import static net.minecraft.entity.vehicle.BoatEntity.Location.ON_LAND;
import static net.minecraft.loot.context.LootContextParameters.LAST_DAMAGE_PLAYER;
import static net.minecraft.tag.FluidTags.WATER;
import static net.minecraft.world.GameRules.DO_ENTITY_DROPS;

public class FabricBoatEntity extends BoatEntity {

    public static final LootContextType BOAT_LOOT = new LootContextType.Builder()
            .allow(LAST_DAMAGE_PLAYER)
            .build();

    protected static final TrackedData<String> BOAT_TYPE = DataTracker.registerData(FabricBoatEntity.class, TrackedDataHandlerRegistry.STRING);

    public FabricBoatEntity(EntityType<? extends FabricBoatEntity> entityType, World world) {
        super(entityType, world);
    }

    public FabricBoatEntity(EntityType<? extends FabricBoatEntity> entityType, World world, double x, double y, double z) {
        this(entityType, world);
        this.setPosition(x, y, z);

        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {

        if (this.isInvulnerableTo(source))
            return false;

        if (!this.world.isClient && this.isAlive()) {
            var attacker = source.getAttacker();
            var trueSource = source.getSource();

            if (source instanceof ProjectileDamageSource && attacker != null && this.hasPassenger(attacker))
                return false;

            this.setDamageWobbleSide(-this.getDamageWobbleSide());
            this.setDamageWobbleTicks(10);
            this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0F);
            this.scheduleVelocityUpdate();

            if (trueSource instanceof PlayerEntity) {
                var gameRules = this.world.getGameRules();
                var player = (PlayerEntity) trueSource;

                if (!player.isCreative() && gameRules.getBoolean(DO_ENTITY_DROPS)) {
                    var server = (ServerWorld) this.world;
                    var ctx = new LootContext.Builder(server).optionalParameter(LAST_DAMAGE_PLAYER, player);

                    if (this.getDamageWobbleStrength() > 40) {
                        this.dropLoot(ctx);
                        this.discard();
                    }

                }

                if (player.isCreative())
                    this.discard();

            }
        }

        return true;
    }

    public void dropLoot(LootContext.Builder context) {
        var server = this.world.getServer();

        if(server != null) {
            var manager = this.world.getServer().getLootManager();

            var id = this.getFabricBoatType().getLootTableId();
            var table = manager.getTable(id);

            table.generateLoot(context.build(BOAT_LOOT), this::dropStack);
        }

    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(BOAT_TYPE, StringUtils.EMPTY);
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
        this.fallVelocity = this.getVelocity().y;

        if (!this.hasVehicle()) {

            if (onGround) {

                if (this.fallDistance > 3) {

                    if (this.location != ON_LAND) {
                        this.fallDistance = 0;
                        return;
                    }

                    this.handleFallDamage(this.fallDistance, 1.0F, FALL);

                    if (!this.world.isClient && !this.isRemoved()) {
                        this.kill();

                        if (this.world.getGameRules().getBoolean(DO_ENTITY_DROPS)) {
                            var server = (ServerWorld) this.world;
                            var context = new LootContext
                                    .Builder(server)
                                    .optionalParameter(LAST_DAMAGE_PLAYER, null);

                            this.dropLoot(context);
                        }

                    }

                }

                this.fallDistance = 0;
            } else if (!this.world.getFluidState(this.getBlockPos().down()).isIn(WATER) && heightDifference < 0)
                this.fallDistance = (float)((double)this.fallDistance - heightDifference);

        }

    }

    public FabricBoatType getFabricBoatType() {
        return FabricBoatType.getBoatType(this.dataTracker.get(BOAT_TYPE));
    }

    public void setFabricBoatType(FabricBoatType boatType) {
        this.dataTracker.set(BOAT_TYPE, boatType.toString());
    }

}