package indi.muxin.food_talks.common.mixin.mechanic

import indi.muxin.food_talks.common.mob_effect.Smelly
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.ai.util.DefaultRandomPos
import net.minecraft.world.level.Level
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

class ConditionallyAvoidEntityGoal <entityT: LivingEntity> (
    mob: PathfinderMob,
    entity: Class<entityT>,
    maxDistance: Float,
    walkSpeedModifier: Double,
    sprintSpeedModifier: Double,
    private val filter: (entityT) -> Boolean
): AvoidEntityGoal<entityT>(mob, entity, maxDistance, walkSpeedModifier, sprintSpeedModifier) {

    private val avoidEntityTargeting = TargetingConditions
        .forCombat()
        .range(maxDistance.toDouble())
        .selector(predicateOnAvoidEntity.and(avoidPredicate))

    override fun canUse(): Boolean {

        val filteredEntity = mob.level().getEntitiesOfClass(
            this.avoidClass,
            mob.boundingBox.inflate(maxDist.toDouble(), 3.0, maxDist.toDouble()),
            filter
        )

        toAvoid = mob.level().getNearestEntity(
            filteredEntity,
            avoidEntityTargeting,
            this.mob,
            mob.x,
            mob.y,
            mob.z
        )
        if (this.toAvoid == null) {
            return false
        } else {
            val vec3 = DefaultRandomPos.getPosAway(this.mob, 16, 7, toAvoid!!.position())
            if (vec3 == null) {
                return false
            } else if (toAvoid!!.distanceToSqr(vec3.x, vec3.y, vec3.z) < toAvoid!!.distanceToSqr(this.mob)) {
                return false
            } else {
                this.path = pathNav.createPath(vec3.x, vec3.y, vec3.z, 0)
                return this.path != null
            }
        }
    }
}




@Mixin(PathfinderMob::class)
class PathfinderMobMixin(entityType: EntityType<out Mob?>, level: Level) :
    Mob(entityType, level) {
    @Suppress("unused")
    @Inject(method = ["<init>"], at = [At("RETURN")])
    fun constructorMixin(entityType: EntityType<out Mob?>?, level: Level?, ci: CallbackInfo?) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        val self = this as PathfinderMob

        if (level != null && !level.isClientSide) {
            self.goalSelector.addGoal(
                10, ConditionallyAvoidEntityGoal(
                    self,
                    LivingEntity::class.java,
                    16f,
                    1.2,
                    1.5
                ) { e: LivingEntity? -> e!!.hasEffect(Smelly.holder) })
        }
    }
}