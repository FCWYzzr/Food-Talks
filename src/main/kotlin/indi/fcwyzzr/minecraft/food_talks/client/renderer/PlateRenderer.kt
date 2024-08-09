package indi.fcwyzzr.minecraft.food_talks.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import indi.fcwyzzr.minecraft.food_talks.common.block.entity.PlateBlockEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack


object PlateRenderer: BlockEntityRenderer<PlateBlockEntity> {

    override fun render(
        entity: PlateBlockEntity,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        combinedLight: Int,
        combinedOverlay: Int) {

        if (entity.size == 0)
            return

        val itemRenderer = Minecraft
            .getInstance()
            .itemRenderer


        entity.readOnlyIngredient
            .forEachIndexed { offset, item ->
                poseStack.pushPose()

                poseStack.translate(0.5, 0.08 + offset, 0.5)
                poseStack.mulPose(Axis.XP.rotationDegrees(90F))
                poseStack.scale(1F, 1F, 1F)

                itemRenderer.renderStatic(
                    item,
                    ItemDisplayContext.FIXED,
                    combinedLight,
                    combinedOverlay,
                    poseStack,
                    buffer,
                    entity.level,
                    entity.blockPos.asLong().toInt()
                )
                poseStack.popPose()
            }
    }


}
