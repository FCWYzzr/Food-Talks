package indi.muxin.food_talks.common.mob_effect


import net.minecraft.world.Difficulty
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player


/**
 * apply to non-peaceful player
 *
 * <milk irremovable>
 *
 * player with this effect can not eat anything, even in hunger
 */
object Anorexia: MobEffect(
    MobEffectCategory.HARMFUL,
    0x590400
) {
    override fun applyEffectTick(entity: LivingEntity, amplifier: Int): Boolean {
        if (entity !is Player)
            return false

        if (entity.level().difficulty == Difficulty.PEACEFUL)
            return false

        return true
    }

    override fun shouldApplyEffectTickThisTick(tickCount: Int, amplifier: Int): Boolean {
        return tickCount % 30 == 0
    }

    override fun onEffectAdded(entity: LivingEntity, amplifier: Int){
        if (entity is Player)
            entity.foodData.setExhaustion(0F)
    }
}