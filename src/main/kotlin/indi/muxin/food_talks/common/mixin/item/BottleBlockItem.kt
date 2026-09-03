package indi.muxin.food_talks.common.mixin.item

import indi.muxin.food_talks.common.block.BottleBlock.Companion.instance
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BottleItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Unique
import javax.annotation.Nonnull


@Mixin(BottleItem::class)
class BottleBlockItem(pProperties: Properties) : Item(pProperties) {
    @Nonnull
    override fun useOn(@Nonnull pContext: UseOnContext): InteractionResult {
        val interactionResult = placeMixin(BlockPlaceContext(pContext))
        if (!interactionResult.consumesAction() && pContext.itemInHand.has(DataComponents.FOOD)) {
            val interactionResult1 = super.use(
                pContext.level,
                pContext.player!!,
                pContext.hand
            ).result
            return if (interactionResult1 == InteractionResult.CONSUME) InteractionResult.CONSUME_PARTIAL else interactionResult1
        } else {
            return interactionResult
        }
    }

    @Unique
    fun placeMixin(context: BlockPlaceContext): InteractionResult {
        if (!context.canPlace()) return InteractionResult.FAIL

        val blockState = instance.defaultBlockState()
        if (!this.placeBlockMixin(context, blockState)) return InteractionResult.FAIL

        val blockPos = context.clickedPos
        val level = context.level
        val player = context.player
        val itemStack = context.itemInHand
        val currentState = level.getBlockState(blockPos)


        if (currentState.`is`(blockState.block)) {
            currentState.block.setPlacedBy(level, blockPos, currentState, player, itemStack)
            if (player is ServerPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger(player, blockPos, itemStack)
            }
        }

        val soundType = currentState.getSoundType(level, blockPos, context.player)
        level.playSound(
            player,
            blockPos,
            placeSoundMixin(level, blockPos, context.player),
            SoundSource.BLOCKS,
            (soundType.getVolume() + 1.0f) / 2.0f,
            soundType.getPitch() * 0.8f
        )
        level.gameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Context.of(player, currentState))
        itemStack.consume(1, player)
        return InteractionResult.sidedSuccess(level.isClientSide)
    }


    @Unique
    private fun placeSoundMixin(world: Level, pos: BlockPos, entity: Player?): SoundEvent {
        val glass = Blocks.GLASS
        return glass.getSoundType(glass.defaultBlockState(), world, pos, entity).placeSound
    }

    @Unique
    private fun placeBlockMixin(pContext: BlockPlaceContext, pState: BlockState): Boolean {
        return pContext.level.setBlock(pContext.clickedPos, pState, 11)
    }
}