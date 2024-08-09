package indi.fcwyzzr.minecraft.food_talks.common.food.item_reward

import indi.fcwyzzr.minecraft.food_talks.api.common.registry.FoodItemReward
import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.toMinecraftResourceLocation
import net.minecraft.world.effect.MobEffects

object BeefMakesYouStrong: FoodItemReward(
    "beef".toMinecraftResourceLocation(),
    MobEffects.DAMAGE_BOOST
) {
    private const val BASE_TIME = 20 * FoodTalks.TPS
    private const val BASE_AMPLIFIER = 1

    override fun extendTime(ingredientCount: Int) = BASE_TIME * ingredientCount
    override fun amplifier(ingredientCount: Int) = BASE_AMPLIFIER * ingredientCount
}