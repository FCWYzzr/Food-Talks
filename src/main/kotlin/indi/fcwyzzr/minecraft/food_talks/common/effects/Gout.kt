package indi.fcwyzzr.minecraft.food_talks.common.effects

import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.f_lib.registry.FMobEffect
import indi.fcwyzzr.minecraft.food_talks.common.registries.GoutDamage
import indi.fcwyzzr.minecraft.food_talks.common.registries.from
import net.minecraft.util.Mth
import net.minecraft.world.Difficulty
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

/**
 * apply to non-peaceful player
 *
 * <milk irremovable>
 *
 * every 20 tick, player has 1/5 chance to receive 2 damage, effected by difficulty
 */
object Gout: FMobEffect(MobEffectCategory.HARMFUL, 0x801400) {

    override fun shouldApplyEffectTickThisTick(duration: Int, amplifier: Int): Boolean {
        if (duration % 20 != 0)
            return false

        return Mth.randomBetween(FoodTalks.random, 0F, 5F) < 1
    }

    override fun applyEffectTick(livingEntity: LivingEntity, amplifier: Int): Boolean {
        if (livingEntity !is Player)
            return false
        if (livingEntity.level().difficulty == Difficulty.PEACEFUL)
            return false

        livingEntity.hurt(GoutDamage from livingEntity.level(), 2F)
        return true
    }
}