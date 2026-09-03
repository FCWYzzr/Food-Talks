package indi.muxin.food_talks.common.world_reaction.food

import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.common.registries.FoodTagPunishment
import indi.muxin.food_talks.common.mob_effect.Anorexia
import indi.muxin.food_talks.common.mob_effect.Gout
import indi.muxin.food_talks.common.mob_effect.Overweight
import indi.muxin.food_talks.common.mob_effect.Toothache
import indi.muxin.food_talks.common.mob_effect.Vomit
import indi.muxin.food_talks.toResourceLocation
import net.minecraft.world.effect.MobEffects

@Deprecated("drinks can't be add to sandwich")
val drinksMakeYouDizzy = FoodTagPunishment.levelBased(
    "food_category/drinks".toResourceLocation(),
    MobEffects.CONFUSION, 30 * FoodTalks.TPS, 0
    , multiplyTime = true, multiplyAmplifier = false
)

val drugsPoisonYou = FoodTagPunishment.levelBased(
    "food_category/drugs".toResourceLocation(),
    MobEffects.POISON, 60 * FoodTalks.TPS, 2
    , multiplyTime = true, multiplyAmplifier = true
)

val fleshesMakeYouVomit = FoodTagPunishment.levelBased(
    "food_category/fleshes".toResourceLocation(),
    Vomit.holder, 5 * FoodTalks.TPS, 0
    , multiplyTime = true, multiplyAmplifier = false
)

val fruitsMakeYouToothache = FoodTagPunishment.levelBased(
    "food_category/fruits".toResourceLocation(),
        Toothache.holder, 60 * FoodTalks.TPS, 0
    , multiplyTime = true, multiplyAmplifier = false
)

val meatsMakeYouGout = FoodTagPunishment.levelBased(
    "food_category/meats".toResourceLocation(),
        Gout.holder, 60 * FoodTalks.TPS, 0
    , multiplyTime = true, multiplyAmplifier = false
)

val snacksMakeYouOverweight = FoodTagPunishment.levelBased(
    "food_category/snacks".toResourceLocation(),
        Overweight.holder, 60 * FoodTalks.TPS, 0
    , multiplyTime = true, multiplyAmplifier = false
)

@Deprecated("soup can't be add to sandwich")
val soupMakeYouHungry = FoodTagPunishment.levelBased(
    "food_category/soup".toResourceLocation(),
        MobEffects.HUNGER, 60 * FoodTalks.TPS, 0
    , multiplyTime = true, multiplyAmplifier = false
)

val staplesGiveYouAnorexia = FoodTagPunishment.levelBased(
    "food_category/staples".toResourceLocation(),
        Anorexia.holder, 60 * FoodTalks.TPS, 0
    , multiplyTime = true, multiplyAmplifier = false
)

val vegetablesMakeYouWeak = FoodTagPunishment.levelBased(
    "food_category/vegetables".toResourceLocation(),
        MobEffects.WEAKNESS, 60 * FoodTalks.TPS, 0
    , multiplyTime = true, multiplyAmplifier = true
)