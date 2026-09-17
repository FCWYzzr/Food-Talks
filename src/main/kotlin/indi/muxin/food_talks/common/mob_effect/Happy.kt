package indi.muxin.food_talks.common.mob_effect

import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object Happy: MobEffect(
    MobEffectCategory.BENEFICIAL, 0x993333
){
    override fun shouldApplyEffectTickThisTick(duration: Int, amplifier: Int): Boolean {
        return duration % 10 == 0
    }

    override fun applyEffectTick(livingEntity: LivingEntity, amplifier: Int): Boolean {
        if (livingEntity !is Player)
            return false

        if (livingEntity.foodData.foodLevel <= 6)
            return false
        livingEntity.heal(1.0F)
        livingEntity.foodData.addExhaustion(6.0f)
        return true
    }
}