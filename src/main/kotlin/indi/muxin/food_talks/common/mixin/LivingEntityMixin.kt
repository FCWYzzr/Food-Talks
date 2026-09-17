package indi.muxin.food_talks.common.mixin

import indi.muxin.food_talks.common.mob_effect.FTMobEffectHolders
import net.minecraft.core.Holder
import net.minecraft.util.RandomSource
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.entity.projectile.ProjectileDeflection
import net.minecraft.world.level.Level
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(LivingEntity::class)
abstract class LivingEntityMixin(entityType: EntityType<*>, level: Level) : Entity(entityType, level) {
    @Shadow
    abstract fun hasEffect(effect: Holder<MobEffect>): Boolean

    override fun deflection(projectile: Projectile): ProjectileDeflection {
        if (hasEffect(FTMobEffectHolders.PROJECTILE_IMMUNE))
            return ProjectileDeflection { arrow: Projectile, _: Entity?, _: RandomSource ->
                arrow.setDeltaMovement(0.0, 0.0, 0.0)
            }
        return super.deflection(projectile)
    }

    @Inject(method = ["setSprinting"], at = [At("HEAD")], cancellable = true)
    fun setSprintingMixin(sprinting: Boolean, ci: CallbackInfo) {
        if (!sprinting)
            return
        @Suppress("CAST_NEVER_SUCCEEDS")
        val self = this as LivingEntity
        if (!self.hasEffect(FTMobEffectHolders.OVERWEIGHT))
            return
        if (sprinting)
            ci.cancel()
    }
}