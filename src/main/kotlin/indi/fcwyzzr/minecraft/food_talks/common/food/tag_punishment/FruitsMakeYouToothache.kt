package indi.fcwyzzr.minecraft.food_talks.common.food.tag_punishment

import indi.fcwyzzr.minecraft.food_talks.api.common.registry.FoodTagPunishment
import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.common.effects.Toothache
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation

object FruitsMakeYouToothache: FoodTagPunishment(
    "fruits".toResourceLocation(),
    Toothache.holder
) {
    private const val BASE_TIME = 30 * FoodTalks.TPS

    override fun extendTime(punishLevel: Int): Int = BASE_TIME * punishLevel

    override fun amplifier(punishLevel: Int) = 1
}