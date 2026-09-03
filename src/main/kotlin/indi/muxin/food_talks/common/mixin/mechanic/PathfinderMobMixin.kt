package indi.muxin.food_talks.common.mixin.mechanic

import indi.muxin.food_talks.common.world_reaction.ConditionallyAvoidEntityGoal
import indi.muxin.food_talks.common.mob_effect.Smelly
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.level.Level
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
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