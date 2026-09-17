package indi.muxin.food_talks.common.block


import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.common.block.BottleBlockEntity.AddItemResult.*
import indi.muxin.food_talks.common.item.Cocktail
import indi.muxin.food_talks.common.item.FTItems
import indi.muxin.food_talks.common.mob_effect.merge
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUtils
import net.minecraft.world.item.Items
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.*
import kotlin.jvm.optionals.getOrNull


class BottleBlockEntity(
    pos: BlockPos,
    state: BlockState
):
    BlockEntity(FTBlockEntityTypeHolders.BOTTLE_BLOCK_ENTITY.value(), pos, state) {
    var freeWaterLevel = 0
        private set
    var upgrade = 0
        private set
    var extend = 0
        private set
    var detoxified = false
        private set

    val contents: Map<Holder<MobEffect>, MobEffectInstance>
        field = mutableMapOf()

    fun addPotion(effects: Sequence<MobEffectInstance>) {
        effects.forEach {
            contents[it.effect] = (it merge contents[it.effect])
        }
        setChanged()
    }
    enum class AddItemResult {SUCCESS, SUCCESS_NO_GROW, FAIL}
    fun addItem(state: BlockState, stack: ItemStack, player: Player, hand: InteractionHand): AddItemResult {
        if (!stack.`is` {
                val v = it.value()

                return@`is` v == Items.POTION
                        || v == Items.HONEY_BOTTLE
                        || v == Items.GLOWSTONE_DUST
                        || v == Items.REDSTONE
                        || v == Items.OMINOUS_BOTTLE
            })
            return FAIL

        val fillLevel = state.getValue(BottleBlock.PROPERTY_FILL_LEVEL)

        if (fillLevel == BottleBlock.MAX_FILL_LEVEL)
            return FAIL

        return when {
            stack.`is`(Items.HONEY_BOTTLE) -> {
                if (freeWaterLevel < 2 || detoxified)
                    FAIL
                else {
                    detoxified = true
                    freeWaterLevel -= 2
                    if (!player.hasInfiniteMaterials()) {
                        stack.shrink(1)
                        val newStack = ItemUtils.createFilledResult(
                            stack, player, Items.GLASS_BOTTLE.defaultInstance)
                        if (newStack !== stack)
                            player.setItemInHand(
                                hand, newStack)
                    }
                    SUCCESS
                }
            }
            stack.`is`(Items.GLOWSTONE_DUST) -> {
                if (freeWaterLevel < 2)
                    FAIL
                else{
                    ++ upgrade
                    freeWaterLevel -= 2
                    if (!player.hasInfiniteMaterials())
                        stack.shrink(1)
                    SUCCESS_NO_GROW
                }
            }

            stack.`is`(Items.REDSTONE) -> {
                if (freeWaterLevel < 1)
                    FAIL
                else{
                    ++ extend
                    -- freeWaterLevel
                    if (!player.hasInfiniteMaterials())
                        stack.shrink(1)
                    SUCCESS_NO_GROW
                }
            }

            stack.`is`(Items.OMINOUS_BOTTLE) -> {
                addPotion(sequenceOf(MobEffectInstance(
                    MobEffects.BAD_OMEN,
                    FoodTalks.TPS,
                    stack.components[DataComponents.OMINOUS_BOTTLE_AMPLIFIER] ?: 0
                )))
                if (!player.hasInfiniteMaterials())
                    stack.shrink(1)
                SUCCESS
            }

            else -> {
                val effects = stack.components[DataComponents.POTION_CONTENTS]
                if (effects == null || !effects.hasEffects())
                    ++freeWaterLevel
                else
                    addPotion(effects.allEffects.asSequence())

                if (!player.hasInfiniteMaterials()){
                    val newStack = ItemUtils.createFilledResult(
                        stack, player,
                        Items.GLASS_BOTTLE.defaultInstance
                    )
                    stack.shrink(1)
                    if (newStack !== stack)
                        player.setItemInHand(hand, newStack)
                }
                SUCCESS
            }
        }
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        contents.clear()

        freeWaterLevel = tag.getInt("free_water_level")
        detoxified = tag.getBoolean("detoxified")
        upgrade = tag.getInt("upgrade")
        extend = tag.getInt("extend")

        tag.getList("contents", 10)
            .map {
                MobEffectInstance.CODEC.parse(
                    registries.createSerializationContext(NbtOps.INSTANCE),
                    it
                ).resultOrPartial { name ->
                    FoodTalks.logger.error("Tried to load invalid item: '{}'", name)
                }
            }.mapNotNull(Optional<MobEffectInstance>::getOrNull)
            .forEach {
                contents[it.effect] = it
            }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        val contentTag = ListTag().apply {
            contents.values.asSequence()
                .map {
                    MobEffectInstance.CODEC.encodeStart(
                        registries.createSerializationContext(NbtOps.INSTANCE),
                        it
                    ).getOrThrow()
                }
                .forEach(::add)
        }

        tag.putInt("free_water_level", freeWaterLevel)
        tag.putInt("upgrade", upgrade)
        tag.putBoolean("detoxified", detoxified)
        tag.put("contents", contentTag)
        tag.putInt("extend", extend)

    }
}



class BottleBlock: Block(Properties.of().apply {
    instabreak()
    explosionResistance(0F)
    sound(SoundType.GLASS)
    noCollission()
    noOcclusion()
    isViewBlocking { _, _, _ -> false }
    isValidSpawn{_, _, _, _-> false}
    pushReaction(PushReaction.DESTROY)
}), EntityBlock {
    companion object {
        const val MAX_FILL_LEVEL = 8
        @JvmField
        val PROPERTY_FILL_LEVEL: IntegerProperty = IntegerProperty.create("fill_level", 0, MAX_FILL_LEVEL)

        private val outlineShape: VoxelShape = box(
            4.0, 0.0, 4.0,
            12.0, 12.0, 12.0
        )
    }



    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return outlineShape
    }


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

    override fun getDrops(state: BlockState, params: LootParams.Builder): MutableList<ItemStack> {
        val entity = params.getParameter(LootContextParams.BLOCK_ENTITY) as BottleBlockEntity
        val fillLevel = state.getValue(PROPERTY_FILL_LEVEL)
        return arrayListOf(Cocktail.buildFromBottle(entity, fillLevel))
    }

    /**
     * shift click take
     */
    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (! player.isCrouching)
            return InteractionResult.PASS


        if (player.hasInfiniteMaterials() && level is ServerLevel)
            getDrops(state, level, pos, level.getBlockEntity(pos)).forEach {
                popResource(level, pos, it)
            }
        else if (level is ServerLevel)
            level.destroyBlock(pos, true)

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
        if (!stack.`is` {
            val v = it.value()

            return@`is` v == Items.POTION
                    || v == Items.HONEY_BOTTLE
                    || v == Items.GLOWSTONE_DUST
                    || v == Items.REDSTONE
                    || v == Items.OMINOUS_BOTTLE
        })
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

        val entity = level.getBlockEntity(pos) as BottleBlockEntity

        val ret = entity.addItem(state, stack, player, hand)
        if (ret ==  SUCCESS)
            level.setBlockAndUpdate(pos, state.setValue(PROPERTY_FILL_LEVEL, state.getValue(PROPERTY_FILL_LEVEL) + 1))

        return if (ret == FAIL)
            ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
        else
            ItemInteractionResult.SUCCESS
    }

    override fun getCloneItemStack(
        state: BlockState,
        target: HitResult,
        level: LevelReader,
        pos: BlockPos,
        player: Player
    ): ItemStack {
        return FTItems.GLASS_BOTTLE.defaultInstance
    }


}

