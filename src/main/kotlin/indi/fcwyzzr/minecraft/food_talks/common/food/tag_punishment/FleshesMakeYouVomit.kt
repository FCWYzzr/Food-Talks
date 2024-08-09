package indi.fcwyzzr.minecraft.food_talks.common.food.tag_punishment

import indi.fcwyzzr.minecraft.food_talks.api.common.registry.FoodTagPunishment
import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.common.effects.Vomit
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation

object FleshesMakeYouVomit: FoodTagPunishment(
    "fleshes".toResourceLocation(),
    Vomit.holder
) {
    private const val BASE_TIME = FoodTalks.TPS * 2

    override fun extendTime(punishLevel: Int): Int = punishLevel * BASE_TIME
    override fun amplifier(punishLevel: Int) = 1
}