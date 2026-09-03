package indi.muxin.food_talks.common.world_reaction.event.lifecycle

import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.common.block.BottleBlock
import indi.muxin.food_talks.common.block.PlateBlock
import indi.muxin.food_talks.common.block.entity.BottleBlockEntity
import indi.muxin.food_talks.common.block.entity.PlateBlockEntity
import indi.muxin.food_talks.common.item.Cocktail
import indi.muxin.food_talks.common.item.Plate
import indi.muxin.food_talks.common.item.Sandwich
import indi.muxin.neoforged.registry.doRegister
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.registries.RegisterEvent

@EventBusSubscriber(
    modid = FoodTalks.MOD_ID,
    value = [Dist.CLIENT, Dist.DEDICATED_SERVER]
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