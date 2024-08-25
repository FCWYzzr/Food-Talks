package indi.fcwyzzr.minecraft.food_talks.client.renderer.item

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import indi.fcwyzzr.minecraft.food_talks.common.registries.sandwichCover
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.Holder
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemDisplayContext

object SandwichRenderer {
    private const val LAYER_HEIGHT = 1.0 / 16

    fun renderLayersWithCenter(
        layer: List<Holder<Item>>,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int
    ){
        val size = layer.size
        val startOffsetLayer = size / 2
        if (size % 2 == 0)
            renderLayersWithLayerOffset(
                8 - startOffsetLayer,
                layer,
                poseStack,
                buffer,
                light,
                overlay)
        else
            renderLayers((8.0 - startOffsetLayer) * LAYER_HEIGHT, layer, poseStack, buffer, light, overlay)
    }

    fun renderLayersWithLayerOffset(
        layerOffset: Int,
        layer: List<Holder<Item>>,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int
    ) = renderLayers(
        layerOffset * LAYER_HEIGHT,
        layer, poseStack, buffer, light, overlay
    )

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

        val itemRenderer = Minecraft
            .getInstance()
            .itemRenderer

        poseStack.pushPose()
        poseStack.translate(0.5, startOffset, 0.5)
        layer
            .map(Holder<Item>::value)
            .map(Item::getDefaultInstance)
            .forEachIndexed { offset, item ->
                poseStack.pushPose()

                poseStack.translate(0.0, offset * LAYER_HEIGHT, 0.0)
                poseStack.mulPose(Axis.XP.rotationDegrees(90F))

                if (item.`is`(sandwichCover))
                    poseStack.scale(1F, 1F, 1F)
                else
                    poseStack.scale(0.8F, 1F, 0.8F)

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
        poseStack.popPose()
    }
}