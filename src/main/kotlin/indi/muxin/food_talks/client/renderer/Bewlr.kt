package indi.muxin.food_talks.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import indi.muxin.food_talks.common.item.Sandwich
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

object Bewlr: BlockEntityWithoutLevelRenderer(
    Minecraft.getInstance().blockEntityRenderDispatcher,
    Minecraft.getInstance().entityModels
) {

    override fun renderByItem(
        stack: ItemStack,
        displayContext: ItemDisplayContext,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        when(stack.item){
            Sandwich -> SandwichRenderer.renderSandwich(
                Sandwich.layers(stack),
                poseStack,
                buffer,
                packedLight,
                packedOverlay
            )
            else -> super.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay)
        }
    }
}