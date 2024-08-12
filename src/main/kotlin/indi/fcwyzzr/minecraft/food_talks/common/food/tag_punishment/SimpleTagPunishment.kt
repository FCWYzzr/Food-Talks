package indi.fcwyzzr.minecraft.food_talks.common.food.tag_punishment

import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.api.common.registry.FoodTagPunishment
import indi.fcwyzzr.minecraft.food_talks.common.effects.harmful.*
import net.minecraft.world.effect.MobEffects

val drinksMakeYouDizzy = FoodTagPunishment.levelBased(
    "DrinksMakeYouDizzy", 
    MobEffects.CONFUSION, 10 * FoodTalks.TPS, 1
    , multiplyTime = true, multiplyAmplifier = false
)

val drugsPoisonYou = FoodTagPunishment.levelBased(
    "DrugsPoisonYou", 
    MobEffects.POISON, 10 * FoodTalks.TPS, 1
    , multiplyTime = true, multiplyAmplifier = true
)

val fleshesMakeYouVomit = FoodTagPunishment.levelBased(
    "FleshMakesYouVomit", 
    Vomit.holder, 2 * FoodTalks.TPS, 1
    , multiplyTime = true, multiplyAmplifier = false
)

val fruitsMakeYouToothache = FoodTagPunishment.levelBased(
    "FruitsMakesYouToothache", 
        Toothache.holder, 30 * FoodTalks.TPS, 1
    , multiplyTime = true, multiplyAmplifier = false
)

val meatsMakeYouGout = FoodTagPunishment.levelBased(
    "MeatsMakeYouGout", 
        Gout.holder, 5 * FoodTalks.TPS, 1
    , multiplyTime = true, multiplyAmplifier = false
)

val snacksMakeYouOverweight = FoodTagPunishment.levelBased(
    "SnacksMakeYouOverweight", 
        Overweight.holder, 30 * FoodTalks.TPS, 1
    , multiplyTime = true, multiplyAmplifier = false
)

val soupMakeYouHungry = FoodTagPunishment.levelBased(
    "SoupMakeYouHungry", 
        MobEffects.HUNGER, 30 * FoodTalks.TPS, 1
    , multiplyTime = true, multiplyAmplifier = false
)

val staplesGiveYouAnorexia = FoodTagPunishment.levelBased(
    "StaplesGiveYouAnorexia", 
        Anorexia.holder, 10 * FoodTalks.TPS, 1
    , multiplyTime = true, multiplyAmplifier = false
)

val vegetablesMakeYouWeak = FoodTagPunishment.levelBased(
    "VegetablesMakeYouWeak", 
        MobEffects.WEAKNESS, 10 * FoodTalks.TPS, 1
    , multiplyTime = true, multiplyAmplifier = true
)