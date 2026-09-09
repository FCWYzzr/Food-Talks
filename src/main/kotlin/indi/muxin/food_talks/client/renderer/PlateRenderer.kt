package indi.muxin.food_talks.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import indi.muxin.food_talks.common.block.PlateBlockEntity
import indi.muxin.food_talks.common.item.Sandwich
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
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

        if (entity.ingredients.isNotEmpty() || entity.displayItem.`is`(Sandwich))
            SandwichRenderer.renderSandwich(
                if (entity.displayItem.isEmpty)
                    entity.ingredients.map(ItemStack::getItemHolder)
                else
                    Sandwich.layers(entity.displayItem),
                poseStack,
                buffer,
                combinedLight,
                combinedOverlay,
                false
            )
        else {
            poseStack.pushPose()
            poseStack.translate(0.5, ITEM_FRAME_HEIGHT * 2, 0.5)
            poseStack.mulPose(Axis.XP.rotationDegrees(90F))
            poseStack.scale(1F, 1F, 1.5F)
            Minecraft.getInstance().itemRenderer.renderStatic(
                entity.displayItem,
                ItemDisplayContext.FIXED,
                combinedLight,
                combinedOverlay,
                poseStack,
                buffer,
                null,
                0
            )
            poseStack.popPose()
        }
    }


}
