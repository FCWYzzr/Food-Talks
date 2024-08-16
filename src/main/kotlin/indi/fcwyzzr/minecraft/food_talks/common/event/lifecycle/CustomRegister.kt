package indi.fcwyzzr.minecraft.food_talks.common.event.lifecycle

import indi.fcwyzzr.minecraft.f_lib.registry.doRegister
import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.common.data_component.SimpleDataComponents
import indi.fcwyzzr.minecraft.food_talks.common.data_component.compound_food.FoodItemProperties
import indi.fcwyzzr.minecraft.food_talks.common.data_component.compound_food.FoodStackProperties
import indi.fcwyzzr.minecraft.food_talks.common.food.item_reward.*
import indi.fcwyzzr.minecraft.food_talks.common.food.tag_punishment.*
import indi.fcwyzzr.minecraft.food_talks.common.registries.foodItemRewardRegistry
import indi.fcwyzzr.minecraft.food_talks.common.registries.foodTagPunishmentRegistry
import indi.fcwyzzr.minecraft.food_talks.toRegistryName
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.core.registries.BuiltInRegistries
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.registries.NewRegistryEvent
import net.neoforged.neoforge.registries.RegisterEvent

@Suppress("DuplicatedCode")
@EventBusSubscriber(
    modid = FoodTalks.MOD_ID,
    value = [Dist.CLIENT, Dist.DEDICATED_SERVER],
    bus = EventBusSubscriber.Bus.MOD
)
object CustomRegister {
    @SubscribeEvent
    fun registerRegistry(event: NewRegistryEvent){
        event.register(foodItemRewardRegistry)
        event.register(foodTagPunishmentRegistry)
    }

    @SubscribeEvent
    fun registerComponents(event: RegisterEvent){
        event.register(BuiltInRegistries.DATA_COMPONENT_TYPE.key()){
            it.register(FoodItemProperties.location, FoodItemProperties.type)
            it.register(FoodStackProperties.location, FoodStackProperties.type)

            it.register(
                "SandwichLayer".toRegistryName().toResourceLocation(),
                SimpleDataComponents.SandwichLayer
            )

            it.register(
                "PossibleEffectList".toRegistryName().toResourceLocation(),
                SimpleDataComponents.PossibleEffectList
            )
        }
    }

    @SubscribeEvent
    fun registerFoodItemReward(event: RegisterEvent){
        // drinks
        event doRegister honeyResistToPoison

        // drugs
        event doRegister ominousBottleIsAnIllWind
        event doRegister goldenCarrotMakesYouSeeBetter
        event doRegister goldenAppleIsForGoddess
        event doRegister enchantedGoldenAppleIsForRealGod

        // fleshes & its cooked version
        event doRegister beefMakesYouStrong
        event doRegister cookedBeefMakesYouStronger
        event doRegister salmonMakesYouSwimFast
        event doRegister cookedSalmonMakesYouSwimFaster
        event doRegister codMakesYouBreathEasy
        event doRegister cookedCodMakesYouBreathEasier
        event doRegister chickenMakesYouLightweight
        event doRegister cookedChickenMakesYouFly
        event doRegister porkchopMakesYouBearing
        event doRegister cookedPorkchopMakesYouEvenMoreBearing
        event doRegister muttonMakesYouSmelly
        event doRegister cookedMuttonMakesYouMoreSmelly
        event doRegister rabbitMakesYouJumpHigh
        event doRegister cookedRabbitMakesYouJumpHigher

        // fruits
        event doRegister appleMakesYouHappy
        event doRegister chorusFruitProtectYouFromProjectile
        event doRegister sweetBerryMakesYouFast
        event doRegister melonMakesYouHeal

        // snacks
        event doRegister cookieMakesYouHappy

        // staples
        event doRegister breadMakesYouFull
        event doRegister bakedPotatoMakesYouHot
        event doRegister pumpkinPieMakesYouSweet

        // vegetables
        event doRegister carrotMakesYouSeeBetter
//        event doRegister potatoMakesYouDirty
//        event doRegister beetrootMakesYouRed
        event doRegister driedKelpMakesYouStarving




    }

    @SubscribeEvent
    fun registerFoodTagPunishment(event: RegisterEvent){
        // event doRegister drinksMakeYouDizzy
        event doRegister fleshesMakeYouVomit
        event doRegister fruitsMakeYouToothache
        event doRegister meatsMakeYouGout
        event doRegister snacksMakeYouOverweight
        // event doRegister soupMakeYouHungry
        event doRegister staplesGiveYouAnorexia
        event doRegister vegetablesMakeYouWeak
        event doRegister drugsPoisonYou
    }
}