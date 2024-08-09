package indi.fcwyzzr.minecraft.food_talks.common.food.tag_punishment

import indi.fcwyzzr.minecraft.food_talks.api.common.registry.FoodTagPunishment
import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.common.effects.Anorexia
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation

object StaplesGiveYouAnorexia: FoodTagPunishment(
    "staples".toResourceLocation(),
    Anorexia.holder
) {
    private const val BASE_TIME = 10 * FoodTalks.TPS

    override fun extendTime(punishLevel: Int) = BASE_TIME * punishLevel * punishLevel

    override fun amplifier(punishLevel: Int) = 1
}