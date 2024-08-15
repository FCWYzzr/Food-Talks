package indi.fcwyzzr.minecraft.food_talks.common.effects.harmful

import indi.fcwyzzr.minecraft.f_lib.registry.FMobEffect
import indi.fcwyzzr.minecraft.food_talks.api.common.item.CompoundFood
import indi.fcwyzzr.minecraft.food_talks.common.data_component.compound_food.FoodItemProperties
import indi.fcwyzzr.minecraft.food_talks.common.effects.beneficial.Starving
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

        if (entity.hasEffect(Anorexia.holder))
            return false

        return (entity !is Player)
                || entity.hasEffect(Starving.holder)
                || entity.canEat(
            itemStack.components[DataComponents.FOOD]
                        ?.canAlwaysEat
                        ?: itemStack.components[FoodItemProperties.type]
                            ?.canAlwaysEat
                            ?: false
        )
    }
}