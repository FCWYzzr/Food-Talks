package indi.muxin.food_talks.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import indi.muxin.food_talks.common.registries.sandwichCover
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.Holder
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

internal const val ITEM_FRAME_HEIGHT        = 1.0 / 16
object SandwichRenderer {
    private const val LAYER_HEIGHT          = 1.0 / 16

    fun renderSandwich(
        layer: List<Holder<Item>>,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
        notOnFrame: Boolean
    ){
        renderLayers(
            if (notOnFrame) ITEM_FRAME_HEIGHT else ITEM_FRAME_HEIGHT,
            layer,
            poseStack,
            buffer,
            light,
            overlay)
    }

    private fun renderLayers(
        startOffset: Double,
        layer: List<Holder<Item>>,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int
    ){
        if (layer.isEmpty())
            return

        poseStack.pushPose()
        poseStack.translate(0.5, startOffset, 0.5)

        val layerItem = layer
            .map(Holder<Item>::value)
            .map(Item::getDefaultInstance)

        renderSingleLayer(
            0.25 * LAYER_HEIGHT ,
            layerItem.first(),
            true,
            poseStack, buffer, light, overlay)

        layerItem
            .drop(1)
            .dropLast(1)
            .forEachIndexed { index, item ->
                renderSingleLayer(
                    (index + 1) * LAYER_HEIGHT,
                    item,
                    false,
                    poseStack, buffer, light, overlay)
            }

        if (layerItem.size > 1)
            renderSingleLayer(
                LAYER_HEIGHT * (layerItem.size - 1.0),
                layerItem.last(),
                layerItem.last().`is`(sandwichCover),
                poseStack, buffer, light, overlay)
        poseStack.popPose()
    }

    private fun renderSingleLayer(startOffset: Double,
                                  item: ItemStack,
                                  isCover: Boolean,
                                  poseStack: PoseStack,
                                  buffer: MultiBufferSource,
                                  light: Int,
                                  overlay: Int){
        val itemRenderer = Minecraft
            .getInstance()
            .itemRenderer
        poseStack.pushPose()

        poseStack.translate(0.0, startOffset, 0.0)
        poseStack.mulPose(Axis.XP.rotationDegrees(90F))

        if (isCover)
            poseStack.scale(1F, 1F, 0.5F)
        else
            poseStack.scale(0.95F, 0.95F, 1F)

        itemRenderer.renderStatic(
            item,
            ItemDisplayContext.FIXED,
            light,
            overlay,
            poseStack,
            buffer,
            null,
            0
        )
        poseStack.popPose()
    }
}