package indi.fcwyzzr.minecraft.food_talks.common.registries

import indi.fcwyzzr.minecraft.food_talks.api.common.registry.FoodItemReward
import indi.fcwyzzr.minecraft.food_talks.api.common.registry.FoodTagPunishment
import indi.fcwyzzr.minecraft.food_talks.toRegistryKey
import indi.fcwyzzr.minecraft.food_talks.toRegistryName
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.core.Registry
import net.neoforged.neoforge.registries.RegistryBuilder


val foodItemRewardKey = "FoodReward".toRegistryName()
    .toResourceLocation()
    .toRegistryKey<FoodItemReward>()

val foodItemRewardRegistry: Registry<FoodItemReward> = RegistryBuilder(
    foodItemRewardKey
).sync(true).create()


val foodTagPunishmentKey = "foodPunishment".toRegistryName()
    .toResourceLocation()
    .toRegistryKey<FoodTagPunishment>()

val foodTagPunishmentRegistry: Registry<FoodTagPunishment> = RegistryBuilder(
    foodTagPunishmentKey
).sync(true).create()
