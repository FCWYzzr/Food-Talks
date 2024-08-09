package indi.fcwyzzr.minecraft.food_talks.common.food.tag_punishment

import indi.fcwyzzr.minecraft.food_talks.api.common.registry.FoodTagPunishment
import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.world.effect.MobEffects

object DrugsPoisonYou: FoodTagPunishment(
    "drugs".toResourceLocation(),
    MobEffects.POISON
) {
    private const val BASE_TIME = 10 * FoodTalks.TPS
    private const val BASE_AMPLIFIER = 1

    override fun extendTime(punishLevel: Int) = BASE_TIME * punishLevel

    override fun amplifier(punishLevel: Int) = BASE_AMPLIFIER * punishLevel
}