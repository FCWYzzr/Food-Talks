@file:Suppress("unused")

package indi.muxin.food_talks.client

import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.client.ponder.PonderForFT
import indi.muxin.food_talks.client.renderer.Bewlr
import indi.muxin.food_talks.client.renderer.PlateRenderer
import indi.muxin.food_talks.common.block.PlateBlockEntity
import indi.muxin.food_talks.common.item.Sandwich
import net.createmod.ponder.foundation.PonderIndex
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModList
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent


@EventBusSubscriber(
    modid = FoodTalks.MOD_ID,
    value = [Dist.CLIENT]
)
object FTClientLifeCycle {

    @SubscribeEvent
    fun registerPonder(ev: FMLClientSetupEvent) {
        if (ModList.get().isLoaded("ponder")) {
            PonderIndex.addPlugin(PonderForFT)
        }
    }

    @SubscribeEvent
    fun registerBlockEntityRenderer(event: RegisterRenderers){
        event.registerBlockEntityRenderer(PlateBlockEntity.type){ PlateRenderer }
    }

    @SubscribeEvent
    fun registerItemRenderer(event: RegisterClientExtensionsEvent){
        event.registerItem(object : IClientItemExtensions {
            override fun getCustomRenderer() = Bewlr
        }, Sandwich) // 注册你的物品
    }
}