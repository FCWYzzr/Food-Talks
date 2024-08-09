package indi.fcwyzzr.minecraft.food_talks.common.event

import indi.fcwyzzr.minecraft.f_lib.registry.doRegister
import indi.fcwyzzr.minecraft.food_talks.common.block.BottleBlock
import indi.fcwyzzr.minecraft.food_talks.common.block.PlateBlock
import indi.fcwyzzr.minecraft.food_talks.common.block.entity.BottleBlockEntity
import indi.fcwyzzr.minecraft.food_talks.common.block.entity.PlateBlockEntity
import indi.fcwyzzr.minecraft.food_talks.common.data_component.compound_food.FoodItemProperties
import indi.fcwyzzr.minecraft.food_talks.common.data_component.compound_food.FoodStackProperties
import indi.fcwyzzr.minecraft.food_talks.common.data_component.compound_food.SimpleProperties
import indi.fcwyzzr.minecraft.food_talks.common.effects.*
import indi.fcwyzzr.minecraft.food_talks.common.food.item_reward.BeefMakesYouStrong
import indi.fcwyzzr.minecraft.food_talks.common.food.tag_punishment.*
import indi.fcwyzzr.minecraft.food_talks.common.item.Cocktail
import indi.fcwyzzr.minecraft.food_talks.common.item.Plate
import indi.fcwyzzr.minecraft.food_talks.common.registries.foodItemRewardRegistry
import indi.fcwyzzr.minecraft.food_talks.common.registries.foodTagPunishmentRegistry
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.core.registries.BuiltInRegistries
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.registries.NewRegistryEvent
import net.neoforged.neoforge.registries.RegisterEvent

object FTLifecycle {
    @SubscribeEvent
    fun registerRegistry(event: NewRegistryEvent){
        event.register(foodItemRewardRegistry)
        event.register(foodTagPunishmentRegistry)
    }

    @SubscribeEvent
    fun registerFoodItemReward(event: RegisterEvent){
        event doRegister BeefMakesYouStrong
    }

    /**
     * register all from tag_punishment
      */
    @SubscribeEvent
    fun registerFoodTagPunishment(event: RegisterEvent){
        event doRegister DrinksMakeYouDizzy
        event doRegister FleshesMakeYouVomit
        event doRegister FruitsMakeYouToothache
        event doRegister MeatsMakeYouGout
        event doRegister SnacksMakeYouOverweight
        event doRegister SoupMakeYouHungry
        event doRegister StaplesGiveYouAnorexia
        event doRegister VegetablesMakeYouWeak
    }

    @SubscribeEvent
    fun registerEffect(event: RegisterEvent){
        event doRegister Anorexia
        event doRegister Gout
        event doRegister Toothache
        event doRegister Vomit
        event doRegister Overweight
    }

    @SubscribeEvent
    fun registerBlocks(event: RegisterEvent){
        event doRegister BottleBlock
        event doRegister PlateBlock
    }

    @SubscribeEvent
    fun registerBlockEntities(event: RegisterEvent){
        event doRegister BottleBlockEntity
        event doRegister PlateBlockEntity
    }

    @SubscribeEvent
    fun registerItems(event: RegisterEvent){
        event doRegister Cocktail
        event doRegister Plate
    }

    @SubscribeEvent
    fun registerComponents(event: RegisterEvent){
        event.register(BuiltInRegistries.DATA_COMPONENT_TYPE.key()){
            it.register(FoodItemProperties.location, FoodItemProperties.type)
            it.register(FoodStackProperties.location, FoodStackProperties.type)
            it.register("use_tick".toResourceLocation(), SimpleProperties.useTick)
        }
    }
}