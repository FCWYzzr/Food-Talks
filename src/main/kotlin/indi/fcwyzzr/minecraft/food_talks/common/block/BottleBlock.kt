package indi.fcwyzzr.minecraft.food_talks.common.block

import indi.fcwyzzr.minecraft.f_lib.registry.FRegistry
import indi.fcwyzzr.minecraft.food_talks.common.block.entity.BottleBlockEntity
import indi.fcwyzzr.minecraft.food_talks.common.item.Cocktail
import indi.fcwyzzr.minecraft.food_talks.toMobEffectInstanceList
import indi.fcwyzzr.minecraft.food_talks.toRegistryName
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.level.*
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.RegisterEvent
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3

class BottleBlock private constructor(): Block(Properties.of().apply {
    instabreak()
    explosionResistance(0F)
    // noTerrainParticles()
    sound(SoundType.GLASS)
    noCollission()
    noOcclusion()
    isViewBlocking { _, _, _ -> false }
    isValidSpawn{_, _, _, _-> false}
    pushReaction(PushReaction.DESTROY)
}), EntityBlock{



    private val outlineShape: VoxelShape = box(
        4.0, 0.0, 4.0,
        12.0, 12.0, 12.0
    )



    override fun newBlockEntity(pPos: BlockPos, pState: BlockState) =
        BottleBlockEntity(pPos, pState)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(PROPERTY_FILL_LEVEL)
    }

    override fun getShape(
        pState: BlockState,
        pLevel: BlockGetter,
        pPos: BlockPos,
        pContext: CollisionContext
    ): VoxelShape {
        return outlineShape
    }

    override fun getBlockSupportShape(pState: BlockState, pLevel: BlockGetter, pPos: BlockPos): VoxelShape {
        return outlineShape
    }

    override fun canSurvive(pState: BlockState, pLevel: LevelReader, pPos: BlockPos): Boolean {
        return canSupportCenter(pLevel, pPos.below(), Direction.UP)
    }

    override fun onDestroyedByPlayer(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        willHarvest: Boolean,
        fluid: FluidState
    ): Boolean {
        val entity = level.getBlockEntity(pos) as BottleBlockEntity

        if (level.gameRules.getBoolean(GameRules.RULE_DOBLOCKDROPS) && ! player.hasInfiniteMaterials())
            doDestroyDrop(level, entity, state.getValue(PROPERTY_FILL_LEVEL))
        super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid)

        return true
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

    private fun doDestroyDrop(level: Level, entity: BottleBlockEntity, fillLevel: Int){
        val itemStack = Cocktail.buildFromBottle(entity, fillLevel)
        val spawnPos = entity.blockPos.toVec3()
        val itemEntity = ItemEntity(level, spawnPos.x, spawnPos.y, spawnPos.z, itemStack)
        level.addFreshEntity(itemEntity)
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
        if (!stack.`is` {
            val v = it.value()

            return@`is` v == Items.POTION
                    || v == Items.HONEY_BOTTLE
                    || v == Items.GLOWSTONE_DUST
                    || v == Items.REDSTONE
        })
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

        val fillLevel = state.getValue(PROPERTY_FILL_LEVEL)
        val entity = level.getBlockEntity(pos) as BottleBlockEntity

        val result = when{
            stack.`is`(Items.HONEY_BOTTLE) -> {
                if (fillLevel == MAX_FILL_LEVEL)
                    ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
                else if (!entity.allowNewCondiment(2))
                    ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
                else if (! entity.detoxify()) {
                    val newState = state.setValue(PROPERTY_FILL_LEVEL, fillLevel+1)
                    level.setBlockAndUpdate(pos, newState)
                    if (!player.hasInfiniteMaterials())
                        player.setItemInHand(hand, Items.GLASS_BOTTLE.defaultInstance)
                    ItemInteractionResult.SUCCESS
                }
                else
                    ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
            }

            stack.`is`(Items.GLOWSTONE_DUST) -> {
                if (!entity.allowNewCondiment(1))
                    ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
                else{
                    entity.upgrade()
                    if (!player.hasInfiniteMaterials())
                        player.setItemInHand(hand, ItemStack.EMPTY)
                    ItemInteractionResult.SUCCESS
                }
            }

            stack.`is`(Items.REDSTONE) -> {
                if (!entity.allowNewCondiment(1))
                    ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
                else{
                    entity.extend()
                    if (!player.hasInfiniteMaterials())
                        player.setItemInHand(hand, ItemStack.EMPTY)
                    ItemInteractionResult.SUCCESS
                }
            }

            else -> {
                if (fillLevel == MAX_FILL_LEVEL)
                    ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
                else {
                    val newState = state.setValue(PROPERTY_FILL_LEVEL, fillLevel+1)
                    level.setBlockAndUpdate(pos, newState)

                    entity.addPotion((stack
                        .components[DataComponents.POTION_CONTENTS]
                        ?: PotionContents.EMPTY).toMobEffectInstanceList()
                    )

                    if (!player.hasInfiniteMaterials())
                        player.setItemInHand(hand, Items.GLASS_BOTTLE.defaultInstance)
                    ItemInteractionResult.SUCCESS
                }
            }
        }

        return result
    }




    companion object: FRegistry<Block>{
        override val registryKey: ResourceKey<out Registry<Block>> = BuiltInRegistries
            .BLOCK.key()

        val name = "Bottle".toRegistryName()
        override val location = name.toResourceLocation()
        override val holder: Holder<Block> = DeferredHolder
            .create(registryKey, location)

        @JvmStatic
        val PROPERTY_FILL_LEVEL: IntegerProperty = IntegerProperty.create("fill_level", 0, 8)
        const val MAX_FILL_LEVEL = 8

        val instance = BottleBlock()

        override fun registerByHelper(helper: RegisterEvent.RegisterHelper<Block>) {
            helper.register(location, instance)
        }
    }
}

