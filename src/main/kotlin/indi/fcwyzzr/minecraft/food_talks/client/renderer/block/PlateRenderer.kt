package indi.fcwyzzr.minecraft.food_talks.client.renderer.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import indi.fcwyzzr.minecraft.food_talks.client.renderer.item.SandwichRenderer
import indi.fcwyzzr.minecraft.food_talks.common.block.entity.PlateBlockEntity
import indi.fcwyzzr.minecraft.food_talks.common.item.Sandwich
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

        if (entity.size != 0)
            SandwichRenderer.renderLayersWithLayerOffset(
                1,
                entity.readOnlyIngredient.map(ItemStack::getItemHolder),
                poseStack,
                buffer,
                combinedLight,
                combinedOverlay
            )
        else if (entity.displayItem != null){
            val item = entity.displayItem!!
            if (item.`is`(Sandwich))
                SandwichRenderer.renderLayersWithLayerOffset(
                    1,
                    Sandwich.layers(item),
                    poseStack,
                    buffer,
                    combinedLight,
                    combinedOverlay
                )
            else{
                poseStack.pushPose()
                poseStack.translate(0.5, 2.0 / 16, 0.5)
                poseStack.mulPose(Axis.XP.rotationDegrees(90F))
                poseStack.scale(1F, 1F, 3F)
                Minecraft.getInstance().itemRenderer.renderStatic(
                    item,
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


}
