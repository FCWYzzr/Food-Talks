package indi.fcwyzzr.minecraft.food_talks.common.food.tag_punishment

import indi.fcwyzzr.minecraft.food_talks.api.common.registry.FoodTagPunishment
import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.world.effect.MobEffects

object DrinksMakeYouDizzy: FoodTagPunishment(
    "drinks".toResourceLocation(),
    MobEffects.CONFUSION
) {
    private const val BASE_TIME = 10 * FoodTalks.TPS

    override fun extendTime(punishLevel: Int) = BASE_TIME * punishLevel

    override fun amplifier(punishLevel: Int) = 1
}