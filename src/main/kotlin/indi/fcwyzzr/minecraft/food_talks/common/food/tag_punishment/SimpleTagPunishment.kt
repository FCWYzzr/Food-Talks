package indi.fcwyzzr.minecraft.food_talks.common.food.tag_punishment

import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.api.common.registry.FoodTagPunishment
import indi.fcwyzzr.minecraft.food_talks.common.effects.harmful.*
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.world.effect.MobEffects

@Deprecated("drinks can't be add to sandwich")
val drinksMakeYouDizzy = FoodTagPunishment.levelBased(
    "food_category.drinks".toResourceLocation(),
    MobEffects.CONFUSION, 10 * FoodTalks.TPS, 1
    , multiplyTime = true, multiplyAmplifier = false
)

val drugsPoisonYou = FoodTagPunishment.levelBased(
    "food_category.drugs".toResourceLocation(),
    MobEffects.POISON, 10 * FoodTalks.TPS, 1
    , multiplyTime = true, multiplyAmplifier = true
)

val fleshesMakeYouVomit = FoodTagPunishment.levelBased(
    "food_category.fleshes".toResourceLocation(),
    Vomit.holder, 2 * FoodTalks.TPS, 1
    , multiplyTime = true, multiplyAmplifier = false
)

val fruitsMakeYouToothache = FoodTagPunishment.levelBased(
    "food_category.fruits".toResourceLocation(),
        Toothache.holder, 30 * FoodTalks.TPS, 1
    , multiplyTime = true, multiplyAmplifier = false
)

val meatsMakeYouGout = FoodTagPunishment.levelBased(
    "food_category.meats".toResourceLocation(),
        Gout.holder, 5 * FoodTalks.TPS, 1
    , multiplyTime = true, multiplyAmplifier = false
)

val snacksMakeYouOverweight = FoodTagPunishment.levelBased(
    "food_category.snacks".toResourceLocation(),
        Overweight.holder, 30 * FoodTalks.TPS, 1
    , multiplyTime = true, multiplyAmplifier = false
)

@Deprecated("soup can't be add to sandwich")
val soupMakeYouHungry = FoodTagPunishment.levelBased(
    "food_category.soup".toResourceLocation(),
        MobEffects.HUNGER, 30 * FoodTalks.TPS, 1
    , multiplyTime = true, multiplyAmplifier = false
)

val staplesGiveYouAnorexia = FoodTagPunishment.levelBased(
    "food_category.staples".toResourceLocation(),
        Anorexia.holder, 10 * FoodTalks.TPS, 1
    , multiplyTime = true, multiplyAmplifier = false
)

val vegetablesMakeYouWeak = FoodTagPunishment.levelBased(
    "food_category.vegetables".toResourceLocation(),
        MobEffects.WEAKNESS, 10 * FoodTalks.TPS, 1
    , multiplyTime = true, multiplyAmplifier = true
)