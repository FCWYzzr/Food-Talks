package indi.fcwyzzr.minecraft.food_talks.common.event.lifecycle

import indi.fcwyzzr.minecraft.f_lib.registry.doRegister
import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.common.block.BottleBlock
import indi.fcwyzzr.minecraft.food_talks.common.block.PlateBlock
import indi.fcwyzzr.minecraft.food_talks.common.block.entity.BottleBlockEntity
import indi.fcwyzzr.minecraft.food_talks.common.block.entity.PlateBlockEntity
import indi.fcwyzzr.minecraft.food_talks.common.item.Cocktail
import indi.fcwyzzr.minecraft.food_talks.common.item.Plate
import indi.fcwyzzr.minecraft.food_talks.common.item.Sandwich
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.registries.RegisterEvent

@EventBusSubscriber(
    modid = FoodTalks.MOD_ID,
    value = [Dist.CLIENT, Dist.DEDICATED_SERVER],
    bus = EventBusSubscriber.Bus.MOD
)
object PrimaryElementRegister {
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
        event doRegister Sandwich
    }
}