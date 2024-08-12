package indi.fcwyzzr.minecraft.food_talks.common.ai.goal

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.ai.util.DefaultRandomPos

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