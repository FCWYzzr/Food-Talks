package indi.muxin.food_talks.client.event

import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.client.renderer.PlateRenderer
import indi.muxin.food_talks.common.block.entity.PlateBlockEntity
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers


@EventBusSubscriber(
    modid = FoodTalks.MOD_ID,
    value = [Dist.CLIENT]
)
object FTClientLifeCycle {
    @SubscribeEvent
    fun registerBlockEntityRenderer(event: RegisterRenderers){
        event.registerBlockEntityRenderer(PlateBlockEntity.type){ PlateRenderer }
    }
}