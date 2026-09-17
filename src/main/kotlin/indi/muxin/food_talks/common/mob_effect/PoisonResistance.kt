package indi.muxin.food_talks.common.mob_effect

import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity

object PoisonResistance: MobEffect(MobEffectCategory.BENEFICIAL, 0xff0000) {
    override fun shouldApplyEffectTickThisTick(pDuration: Int, pAmplifier: Int): Boolean {
        return pDuration % 10 == 0
    }
    override fun applyEffectTick(livingEntity: LivingEntity, amplifier: Int): Boolean {
        val poison = livingEntity.activeEffectsMap[MobEffects.POISON] ?: return true
        if (poison.amplifier > amplifier)
            return false
        livingEntity.removeEffect(MobEffects.POISON)
        return true
    }
}