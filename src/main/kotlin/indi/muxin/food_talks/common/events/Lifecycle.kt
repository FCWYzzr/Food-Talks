package indi.muxin.food_talks.common.events

import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.common.block.BottleBlock
import indi.muxin.food_talks.common.block.BottleBlockEntity
import indi.muxin.food_talks.common.block.PlateBlock
import indi.muxin.food_talks.common.block.PlateBlockEntity
import indi.muxin.food_talks.common.data_components.registerFoodItemProperties
import indi.muxin.food_talks.common.data_components.registerFoodStackProperties
import indi.muxin.food_talks.common.data_components.registerSimpleDataComponents
import indi.muxin.food_talks.common.item.Cocktail
import indi.muxin.food_talks.common.item.Plate
import indi.muxin.food_talks.common.item.Sandwich
import indi.muxin.food_talks.common.mob_effect.Anorexia
import indi.muxin.food_talks.common.mob_effect.DarknessInfused
import indi.muxin.food_talks.common.mob_effect.EndlessTreasure
import indi.muxin.food_talks.common.mob_effect.Gout
import indi.muxin.food_talks.common.mob_effect.Happy
import indi.muxin.food_talks.common.mob_effect.Overweight
import indi.muxin.food_talks.common.mob_effect.PoisonResistance
import indi.muxin.food_talks.common.mob_effect.ProjectileImmune
import indi.muxin.food_talks.common.mob_effect.ScapeGoat
import indi.muxin.food_talks.common.mob_effect.Smelly
import indi.muxin.food_talks.common.mob_effect.Starving
import indi.muxin.food_talks.common.mob_effect.Toothache
import indi.muxin.food_talks.common.mob_effect.Treasure
import indi.muxin.food_talks.common.mob_effect.Vomit
import indi.muxin.food_talks.common.registries.FoodItemReward
import indi.muxin.food_talks.common.registries.FoodTagPunishment
import net.minecraft.core.registries.BuiltInRegistries
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.registries.RegisterEvent
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent

@EventBusSubscriber(
    modid = FoodTalks.MOD_ID,
    value = [Dist.CLIENT, Dist.DEDICATED_SERVER]
)
object Lifecycle {
    @SubscribeEvent
    fun registerDataMaps(event: RegisterDataMapTypesEvent){
        FoodItemReward      registerTo event
        FoodTagPunishment   registerTo event
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
    fun registerEffect(event: RegisterEvent){
        arrayOf(
            Anorexia,
            Gout,
            Toothache,
            Vomit,
            Overweight,
            PoisonResistance,
            DarknessInfused,
            Treasure,
            EndlessTreasure,
            Smelly,
            Happy,
            ProjectileImmune,
            Starving,
            ScapeGoat
        ).forEach {
            it registerTo event
        }
    }

    @SubscribeEvent
    fun registerBlocks(event: RegisterEvent){
        BottleBlock registerTo event
        PlateBlock  registerTo event

        BottleBlockEntity registerTo event
        PlateBlockEntity  registerTo event

        Cocktail    registerTo event
        Plate       registerTo event
        Sandwich    registerTo event
    }
}