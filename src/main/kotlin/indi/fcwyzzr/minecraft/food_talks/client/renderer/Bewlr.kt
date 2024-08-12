package indi.fcwyzzr.minecraft.food_talks.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import indi.fcwyzzr.minecraft.food_talks.client.renderer.item.SandwichRenderer
import indi.fcwyzzr.minecraft.food_talks.common.data_component.SimpleDataComponents
import indi.fcwyzzr.minecraft.food_talks.common.item.Sandwich
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

object Bewlr: BlockEntityWithoutLevelRenderer(
    Minecraft.getInstance().blockEntityRenderDispatcher,
    Minecraft.getInstance().entityModels
) {
    private val renderDispatcher = Minecraft.getInstance().blockEntityRenderDispatcher

    override fun renderByItem(
        stack: ItemStack,
        displayContext: ItemDisplayContext,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        when(stack.item){
            Sandwich -> renderSandwich(stack, poseStack, buffer, packedLight, packedOverlay)
            else -> super.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay)
        }
    }

    private fun renderSandwich(
        stack: ItemStack,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ){
        val layers = stack.components[SimpleDataComponents.SandwichLayer]!!

        SandwichRenderer.renderLayersWithCenter(
            layers,
            poseStack,
            buffer,
            packedLight,
            packedOverlay
        )
    }
}