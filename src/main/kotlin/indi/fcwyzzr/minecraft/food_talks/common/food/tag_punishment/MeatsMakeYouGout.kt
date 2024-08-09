package indi.fcwyzzr.minecraft.food_talks.common.food.tag_punishment

import indi.fcwyzzr.minecraft.food_talks.api.common.registry.FoodTagPunishment
import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.common.effects.Gout
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation


object MeatsMakeYouGout: FoodTagPunishment(
    "meats".toResourceLocation(),
    Gout.holder
) {
    private const val BASE_TIME = FoodTalks.TPS * 5

    override fun extendTime(punishLevel: Int): Int = BASE_TIME * punishLevel

    override fun amplifier(punishLevel: Int) = 1
}