package indi.fcwyzzr.minecraft.food_talks.common.effects

import indi.fcwyzzr.minecraft.f_lib.registry.FMobEffect
import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import net.minecraft.world.Difficulty
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

/**
 * apply to non-peaceful player
 * <milk irremovable>
 *
 * upon eat, player lose 1 hp (2 & 5 heart, ignore difficulty)
 */
object Overweight: FMobEffect(
    MobEffectCategory.HARMFUL,
    0xffffff
) {

    override fun shouldApplyEffectTickThisTick(duration: Int, amplifier: Int): Boolean {
        return duration % (5 * FoodTalks.TPS) == 0
    }

    override fun applyEffectTick(pLivingEntity: LivingEntity, pAmplifier: Int): Boolean {
        pLivingEntity.isSprinting = false
        return (pLivingEntity is Player) && ((pLivingEntity.level().difficulty
            ?: Difficulty.PEACEFUL) != Difficulty.PEACEFUL)
    }
}