package indi.muxin.food_talks.common.mob_effect

import indi.muxin.neoforged.registry.FMobEffect
import indi.muxin.food_talks.common.item.CompoundFood
import indi.muxin.food_talks.common.data_components.FoodItemPropertiesDCType
import net.minecraft.core.component.DataComponents
import net.minecraft.world.Difficulty
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack


/**
 * apply to non-peaceful player
 *
 * <milk irremovable>
 *
 * player with this effect can not eat anything, even in hunger
 */
object Anorexia: FMobEffect(
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

    fun canEat(itemStack: ItemStack, entity: LivingEntity): Boolean {
        if (!CompoundFood.isFood(itemStack))
            return false

        if (entity.hasEffect(holder))
            return false

        return (entity !is Player)
                || entity.hasEffect(Starving.holder)
                || entity.canEat(
            itemStack.components[DataComponents.FOOD]
                        ?.canAlwaysEat
                        ?: itemStack.components[FoodItemPropertiesDCType]
                            ?.canAlwaysEat
                            ?: false
        )
    }
}