package indi.fcwyzzr.minecraft.food_talks.client.event

import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.client.renderer.block.PlateRenderer
import indi.fcwyzzr.minecraft.food_talks.common.block.entity.PlateBlockEntity
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers


@EventBusSubscriber(
    modid = FoodTalks.MOD_ID,
    value = [Dist.CLIENT],
    bus = EventBusSubscriber.Bus.MOD
)
object FTClientLifeCycle {
    @SubscribeEvent
    fun registerBlockEntityRenderer(event: RegisterRenderers){
        event.registerBlockEntityRenderer(PlateBlockEntity.type){ PlateRenderer }
    }
}