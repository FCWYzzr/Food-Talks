package indi.muxin.food_talks.common.world_reaction.event.lifecycle

import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.common.data_components.registerFoodItemProperties
import indi.muxin.food_talks.common.data_components.registerFoodStackProperties
import indi.muxin.food_talks.common.data_components.registerSimpleDataComponents
import indi.muxin.food_talks.common.registries.foodItemRewardRegistry
import indi.muxin.food_talks.common.registries.foodTagPunishmentRegistry
import indi.muxin.food_talks.common.world_reaction.food.*
import indi.muxin.neoforged.registry.doRegister
import net.minecraft.core.registries.BuiltInRegistries
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.registries.NewRegistryEvent
import net.neoforged.neoforge.registries.RegisterEvent

@EventBusSubscriber(
    modid = FoodTalks.MOD_ID,
    value = [Dist.CLIENT, Dist.DEDICATED_SERVER]
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
            it.registerFoodItemProperties()
            it.registerFoodStackProperties()
            it.registerSimpleDataComponents()
        }
    }

    @SubscribeEvent
    fun registerFoodItemReward(event: RegisterEvent){

        arrayOf(
            // drinks
            honeyResistToPoison,

            // drugs,
            ominousBottleIsAnIllWind,
            goldenCarrotMakesYouSeeBetter,
            goldenAppleMeansTreasure,
            enchantedGoldenAppleMeansEndlessTreasure,


            // fleshes & its cooked version
            beefMakesYouStrong,
            cookedBeefMakesYouStronger,
            salmonMakesYouSwimFast,
            cookedSalmonMakesYouSwimFaster,
            codMakesYouBreathEasy,
            cookedCodMakesYouBreathEasier,
            chickenMakesYouFly,
            cookedChickenMakesYouFly,
            porkChopMakesYouBearing,
            cookedPorkChopMakesYouEvenMoreBearing,
            muttonMakesYouSmelly,
            cookedMuttonMakesYouMoreSmelly,
            rabbitMakesYouJumpHigh,
            cookedRabbitMakesYouJumpHigher,

            // fruits
            appleMakesYouHappy,
            chorusFruitProtectYouFromProjectile,
            sweetBerryMakesYouFast,
            melonMakesYouHeal,

            // snacks
            cookieMakesYouHappy,

            // staples
            breadMakesYouFull,
            bakedPotatoMakesYouHot,
            pumpkinPieMakesYouSweet,

            // vegetables
            carrotMakesYouSeeBetter,
//          potatoMakesYouDirty,
//          beetrootMakesYouRed,
            driedKelpMakesYouStarving

        ).forEach {
            event doRegister it
        }
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