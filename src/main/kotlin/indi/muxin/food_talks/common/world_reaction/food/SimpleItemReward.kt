package indi.muxin.food_talks.common.world_reaction.food

import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.common.registries.FoodItemReward
import indi.muxin.food_talks.common.mob_effect.DarknessInfused
import indi.muxin.food_talks.common.mob_effect.EndlessTreasure
import indi.muxin.food_talks.common.mob_effect.Treasure
import indi.muxin.food_talks.common.mob_effect.Happy
import indi.muxin.food_talks.common.mob_effect.ScapeGoat
import indi.muxin.food_talks.common.mob_effect.PoisonResistance
import indi.muxin.food_talks.common.mob_effect.ProjectileImmune
import indi.muxin.food_talks.common.mob_effect.Smelly
import indi.muxin.food_talks.common.mob_effect.Starving
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import kotlin.jvm.optionals.getOrNull


private fun Item.toResourceKey() = BuiltInRegistries
    .ITEM
    .getResourceKey(this)
    .getOrNull()!!

// <drinks>
val honeyResistToPoison = FoodItemReward.countBased(
    Items.HONEY_BOTTLE.toResourceKey(),
    PoisonResistance.holder, 10 * FoodTalks.TPS, 0,
    multiplyTime = true, multiplyAmplifier = true
)
// </drinks>


// <drugs>
val ominousBottleIsAnIllWind = FoodItemReward.countBased(
    Items.OMINOUS_BOTTLE.toResourceKey(),
    DarknessInfused.holder, 10 * FoodTalks.TPS
)

val goldenCarrotMakesYouSeeBetter = FoodItemReward.countBased(
    Items.GOLDEN_CARROT.toResourceKey(),
    MobEffects.NIGHT_VISION, 10 * FoodTalks.TPS
)

val goldenAppleMeansTreasure = FoodItemReward.countBased(
    Items.GOLDEN_APPLE.toResourceKey(),
    Treasure.holder, 10 * FoodTalks.TPS
)

val enchantedGoldenAppleMeansEndlessTreasure = FoodItemReward.countBased(
    Items.ENCHANTED_GOLDEN_APPLE.toResourceKey(),
    EndlessTreasure.holder, 10 * FoodTalks.TPS
)
// gives you nothing: poisonous potato; rotten_flesh; spider_eye
// </drugs>



// <fleshes & its cooked version>
val beefMakesYouStrong = FoodItemReward.countBased(
    Items.BEEF.toResourceKey(),
    MobEffects.DAMAGE_BOOST, 10 * FoodTalks.TPS
)

val cookedBeefMakesYouStronger = FoodItemReward.countBased(
    Items.COOKED_BEEF.toResourceKey(),
    MobEffects.DAMAGE_BOOST, 10 * FoodTalks.TPS, 1
)

val salmonMakesYouSwimFast = FoodItemReward.countBased(
    Items.SALMON.toResourceKey(),
    MobEffects.DOLPHINS_GRACE, 5 * FoodTalks.TPS
)

val cookedSalmonMakesYouSwimFaster = FoodItemReward.countBased(
    Items.COOKED_SALMON.toResourceKey(),
    MobEffects.DOLPHINS_GRACE, 10 * FoodTalks.TPS, 1
)

val codMakesYouBreathEasy = FoodItemReward.countBased(
    Items.COD.toResourceKey(),
    MobEffects.WATER_BREATHING, 5 * FoodTalks.TPS
)

val cookedCodMakesYouBreathEasier = FoodItemReward.countBased(
    Items.COOKED_COD.toResourceKey(),
    MobEffects.WATER_BREATHING, 20 * FoodTalks.TPS
)

val porkChopMakesYouBearing = FoodItemReward.countBased(
    Items.PORKCHOP.toResourceKey(),
    MobEffects.ABSORPTION, 15 * FoodTalks.TPS
)

val cookedPorkChopMakesYouEvenMoreBearing = FoodItemReward.countBased(
    Items.COOKED_PORKCHOP.toResourceKey(),
    MobEffects.ABSORPTION, 30 * FoodTalks.TPS, 2
)

val chickenMakesYouFly = FoodItemReward.countBased(
    Items.CHICKEN.toResourceKey(),
    MobEffects.SLOW_FALLING, 1 * FoodTalks.TPS
)

val cookedChickenMakesYouFly = FoodItemReward.countBased(
    Items.COOKED_CHICKEN.toResourceKey(),
    MobEffects.SLOW_FALLING, 5 * FoodTalks.TPS
)

val muttonMakesYouSmelly = FoodItemReward.countBased(
    Items.MUTTON.toResourceKey(),
    Smelly.holder, 5 * FoodTalks.TPS
)

val cookedMuttonMakesYouMoreSmelly = FoodItemReward.countBased(
    Items.COOKED_MUTTON.toResourceKey(),
    Smelly.holder, 10 * FoodTalks.TPS, 1
)

val rabbitMakesYouJumpHigh = FoodItemReward.countBased(
    Items.RABBIT.toResourceKey(),
    MobEffects.JUMP, 5 * FoodTalks.TPS
)

val cookedRabbitMakesYouJumpHigher = FoodItemReward.countBased(
    Items.COOKED_RABBIT.toResourceKey(),
    MobEffects.JUMP, 10 * FoodTalks.TPS, 1
)

// gives you nothing: pufferfish; tropical_fish
// </fleshes & its cooked version>

// <fruits>
val appleMakesYouHappy = FoodItemReward.countBased(
    Items.APPLE.toResourceKey(),
    Happy.holder, 5 * FoodTalks.TPS
)

val chorusFruitProtectYouFromProjectile = FoodItemReward.countBased(
    Items.CHORUS_FRUIT.toResourceKey(),
    ProjectileImmune.holder, 10 * FoodTalks.TPS
)

val sweetBerryMakesYouFast = FoodItemReward.countBased(
    Items.SWEET_BERRIES.toResourceKey(),
    MobEffects.MOVEMENT_SPEED, 10 * FoodTalks.TPS
)

val melonMakesYouHeal = FoodItemReward.countBased(
    Items.MELON_SLICE.toResourceKey(),
    MobEffects.HEAL, 1
)
// </fruits>

// <snacks>

val cookieMakesYouHappy = FoodItemReward.countBased(
    Items.COOKIE.toResourceKey(),
    Happy.holder, 1 * FoodTalks.TPS
)

// </snacks>

// <soup>
// not implemented
// </soup>

// <staples>

val breadMakesYouFull = FoodItemReward.countBased(
    Items.BREAD.toResourceKey(),
    MobEffects.SATURATION, FoodTalks.TPS / 2
)

val bakedPotatoMakesYouHot = FoodItemReward.countBased(
    Items.BAKED_POTATO.toResourceKey(),
    MobEffects.FIRE_RESISTANCE, 5 * FoodTalks.TPS
)

val pumpkinPieMakesYouSweet = FoodItemReward.countBased(
    Items.PUMPKIN_PIE.toResourceKey(),
    ScapeGoat.holder, 30 * FoodTalks.TPS
)

// </staples>

// <vegetables>

val carrotMakesYouSeeBetter = FoodItemReward.countBased(
    Items.CARROT.toResourceKey(),
    MobEffects.NIGHT_VISION, 1 * FoodTalks.TPS
)


val driedKelpMakesYouStarving = FoodItemReward.countBased(
    Items.DRIED_KELP.toResourceKey(),
    Starving.holder, 30 * FoodTalks.TPS
)

// gives you nothing: potato; beetroot
// </vegetables>
