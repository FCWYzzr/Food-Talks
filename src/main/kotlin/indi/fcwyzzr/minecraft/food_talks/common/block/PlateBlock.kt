package indi.fcwyzzr.minecraft.food_talks.common.block

import indi.fcwyzzr.minecraft.f_lib.registry.FRegistry
import indi.fcwyzzr.minecraft.food_talks.common.block.entity.PlateBlockEntity
import indi.fcwyzzr.minecraft.food_talks.common.item.Plate
import indi.fcwyzzr.minecraft.food_talks.common.item.Sandwich
import indi.fcwyzzr.minecraft.food_talks.toRegistryName
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.RegisterEvent

class PlateBlock private constructor(): Block(Properties.of().apply {
    instabreak()
    explosionResistance(0F)
    sound(SoundType.WOOD)
    noCollission()
    noOcclusion()
    isViewBlocking { _, _, _ -> false }
    isValidSpawn{_, _, _, _-> false}
    pushReaction(PushReaction.DESTROY)
}), EntityBlock{
    private val outLine = box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0)

    override fun newBlockEntity(p0: BlockPos, p1: BlockState): BlockEntity =
        PlateBlockEntity(p0, p1)

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return outLine
    }

    override fun canSurvive(pState: BlockState, pLevel: LevelReader, pPos: BlockPos): Boolean {
        return canSupportCenter(pLevel, pPos.below(), Direction.UP)
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighbor: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return if (direction == Direction.DOWN && !this.canSurvive(
                state,
                level,
                pos
            )
        ) Blocks.AIR.defaultBlockState() else
            super.updateShape(
                state,
                direction,
                neighbor,
                level,
                pos,
                neighborPos
            )
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        val entity = level.getBlockEntity(pos) as PlateBlockEntity
        if (player.isShiftKeyDown){
            if (!player.hasInfiniteMaterials()
                && level is ServerLevel)
                entity.forEachFoldedContent { popResource(level, pos, this) }

            entity.clear()

            return InteractionResult.SUCCESS
        }

        if (!entity.canAssembly)
            return InteractionResult.PASS

        val stack = Sandwich.assemblyFromPlate(entity)

        if (!player.hasInfiniteMaterials())
            entity.clear()

        level.addFreshEntity(ItemEntity(
            level,
            pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(),
            stack
        ))
        return InteractionResult.SUCCESS
    }


    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        val entity = level.getBlockEntity(pos) as PlateBlockEntity

        return if (!entity.putItem(stack))
            ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
        else {
            level.playSound(
                player,
                pos,
                SoundEvents.ITEM_FRAME_ADD_ITEM,
                SoundSource.BLOCKS,
                1F, 1F
            )


            if (!player.hasInfiniteMaterials())
                player.setItemInHand(
                    hand,
                    stack.apply { shrink(1) }
                )

            ItemInteractionResult.SUCCESS
        }
    }

    override fun getDrops(state: BlockState, params: LootParams.Builder) = buildList {
        val breaker: Entity? = params.getOptionalParameter(LootContextParams.THIS_ENTITY)
        if (breaker !is Player || !breaker.hasInfiniteMaterials())
            add(Plate.defaultInstance)

        val entity = params.getParameter(LootContextParams.BLOCK_ENTITY) as PlateBlockEntity
        entity.forEachFoldedContent(::add)
    }



    companion object: FRegistry<Block>  {
        val instance = PlateBlock()

        val name = "Plate".toRegistryName()
        override val location = name.toResourceLocation()
        override val registryKey: ResourceKey<out Registry<Block>> = BuiltInRegistries.BLOCK.key()
        override val holder: Holder<Block> = DeferredHolder.create(registryKey, location)

        override fun registerByHelper(helper: RegisterEvent.RegisterHelper<Block>) {
            helper.register(location, instance)
        }
    }
}
