package indi.muxin.food_talks.common.block

import com.mojang.datafixers.types.Type
import com.mojang.logging.LogUtils
import com.mojang.serialization.Codec
import indi.muxin.food_talks.common.item.CompoundFood
import indi.muxin.food_talks.common.item.Plate
import indi.muxin.food_talks.common.item.Sandwich
import indi.muxin.food_talks.common.registries.sandwichCover
import indi.muxin.food_talks.common.registries.soupFood
import indi.muxin.food_talks.toRegistryName
import indi.muxin.food_talks.toResourceLocation
import indi.muxin.neoforged.registry.FRegistry
import net.minecraft.core.*
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
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
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.RegisterEvent
import org.slf4j.Logger
import java.util.*
import kotlin.jvm.optionals.getOrDefault
import kotlin.jvm.optionals.getOrNull

class PlateBlockEntity(
    pos: BlockPos,
    state: BlockState
): BlockEntity(type, pos, state) {
    enum class Mode {
        DISPLAY, ASSEMBLY
    }

    val ingredients: List<ItemStack>
        field = mutableListOf<ItemStack>()
    val displayItem: ItemStack get() = item
    val displayMode: Mode get() = mode

    private var item: ItemStack = ItemStack.EMPTY
    private var mode: Mode = Mode.DISPLAY

    val canAssembly get() = ingredients.size > 1 && ingredients.last().`is`(sandwichCover)

    fun clear(){
        item = ItemStack.EMPTY
        ingredients.clear()
        mode = Mode.DISPLAY
        setChanged()
    }

    fun forEachContent(action: ItemStack.() -> Unit){
        when(mode){
            Mode.DISPLAY -> {
                displayItem.action()
            }
            Mode.ASSEMBLY -> {
                ingredients
                    .fold(mutableMapOf<Item, MutableMap<DataComponentMap, ItemStack>>()) { map, itemStack ->
                        if (itemStack.item !in map) {
                            map[itemStack.item] = mutableMapOf(itemStack.components to itemStack)
                            return@fold map
                        }

                        val itemSet = map[itemStack.item]!!
                        if (itemStack.components !in itemSet) {
                            itemSet[itemStack.components] = itemStack
                            return@fold map
                        }

                        itemSet[itemStack.components]!!.grow(itemStack.count)

                        map
                    }
                    .flatMap { it.value.values }
                    .forEach(action)
            }
        }
    }

    fun putItem(itemStack: ItemStack): Boolean {
        if (itemStack.isEmpty)
            return false

        when (mode) {
            Mode.DISPLAY -> {
                if (item.isEmpty){
                    item = itemStack.copyWithCount(1)
                    setChanged()
                    return true
                }
                if (item.`is`(sandwichCover)
                        && CompoundFood.isHandHoldFood(itemStack, true)
                        && !itemStack.`is`(soupFood)){
                    mode = Mode.ASSEMBLY
                    ingredients.clear()
                    ingredients.add(item)
                    ingredients.add(itemStack.copyWithCount(1))
                    item = ItemStack.EMPTY
                    setChanged()
                    return true
                }
                else
                    return false
            }
            Mode.ASSEMBLY -> {
                if (ingredients.size == MAX_LAYER)
                    return false
                // only allow non-compound food as ingredient
                if (!CompoundFood.isHandHoldFood(itemStack, true))
                    return false
                if (itemStack.`is`(soupFood))
                    return false
                if (ingredients.size == MAX_LAYER - 1 && !itemStack.`is`(sandwichCover))
                    return false

                ingredients.addLast(itemStack.copyWithCount(1))
                setChanged()
                return true
            }
        }
    }

    fun popItem(): ItemStack {
        when (mode) {
            Mode.DISPLAY -> {
                val v = item
                item = ItemStack.EMPTY
                setChanged()
                return v
            }
            Mode.ASSEMBLY -> {
                val v = ingredients.removeLast()
                if (ingredients.size == 1) {
                    mode = Mode.DISPLAY
                    item = ingredients.removeLast()
                    ingredients.clear()
                    setChanged()
                }
                return v
            }
        }
    }


    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        ingredients.clear()
        val ops = registries.createSerializationContext(NbtOps.INSTANCE)

        tag.get("mode")
            ?.apply {
                val displayMode = Codec.INT.parse(ops, this).map {
                    Mode.entries[it]
                } .resultOrPartial { name ->
                    LOGGER.warn("Tried to load invalid display mode: '{}'", name)
                }
                mode = displayMode.getOrDefault(Mode.DISPLAY)
            }
        tag.get("item")
            ?.apply {
                val displayItem = ItemStack.SINGLE_ITEM_CODEC.parse(ops, this).resultOrPartial { name ->
                    LOGGER.warn("Tried to load invalid display item: '{}'", name)
                }
                item = displayItem.getOrDefault(ItemStack.EMPTY)
            }
        tag.getList("ingredients", 10)
            .asIterable()
            .map {
                ItemStack.CODEC.parse(ops, it).resultOrPartial { name ->
                    LOGGER.warn("Tried to load invalid ingredient: '{}'", name)
                }
            }.mapNotNull(Optional<ItemStack>::getOrNull)
            .forEach(ingredients::add)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)

        tag.put("mode", Codec.INT.encodeStart(
            registries.createSerializationContext(NbtOps.INSTANCE),
            mode.ordinal
        ).getOrThrow())

        if (!item.isEmpty){
            tag.put("item",
                ItemStack.CODEC.encodeStart(
                    registries.createSerializationContext(NbtOps.INSTANCE),
                    item
                ).getOrThrow()
            )
            return
        }

        val contentTag = ListTag().apply {
            ingredients
                .asIterable()
                .map {
                    ItemStack.CODEC.encodeStart(
                        registries.createSerializationContext(NbtOps.INSTANCE),
                        it
                    ).getOrThrow()
                }
                .forEach(::add)
        }

        tag.put("ingredients", contentTag)
    }


    override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
        ClientboundBlockEntityDataPacket.create(this)

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        val tag = CompoundTag()
        saveAdditional(tag, registries)
        return tag
    }


    companion object {
        private val LOGGER: Logger = LogUtils.getLogger()

        private const val MAX_LAYER = 20

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "TYPE_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        val type: BlockEntityType<PlateBlockEntity> = BlockEntityType.Builder.of(
            ::PlateBlockEntity,
            PlateBlock
        ).build(null as Type<*>?)

        val registryKey: ResourceKey<out Registry<BlockEntityType<*>>> = BuiltInRegistries.BLOCK_ENTITY_TYPE.key()
        val location = "Plate".toRegistryName().toResourceLocation()
        val holder: Holder<BlockEntityType<*>> = DeferredHolder.create(registryKey, location)

        infix fun registerTo(ev: RegisterEvent) {
            ev.register(registryKey){
                it.register(location, type)
            }
        }
    }
}

object PlateBlock: Block(Properties.of().apply {
    instabreak()
    explosionResistance(0F)
    sound(SoundType.WOOD)
    noCollission()
    noOcclusion()
    isViewBlocking { _, _, _ -> false }
    isValidSpawn{_, _, _, _-> false}
    pushReaction(PushReaction.DESTROY)
}), EntityBlock, FRegistry<Block>{
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
                pos))
            Blocks.AIR.defaultBlockState()
        else
            super.updateShape(
                state, direction,
                neighbor,
                level, pos,
                neighborPos)
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        val entity = level.getBlockEntity(pos) as PlateBlockEntity
        if (entity.displayMode == PlateBlockEntity.Mode.DISPLAY) {
            val item = entity.popItem()
            if (item.isEmpty)
                return InteractionResult.PASS
            if (!level.isClientSide && !player.hasInfiniteMaterials())
                popResource(level, pos, item)
            level.playSound(
                player,
                pos,
                SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                SoundSource.BLOCKS,
                1F, 1F)
            return InteractionResult.SUCCESS
        }

        if (player.isCrouching){
            if (!level.isClientSide && !player.hasInfiniteMaterials())
                entity.forEachContent { popResource(level, pos, this) }
            entity.clear()
            level.playSound(
                player,
                pos,
                SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                SoundSource.BLOCKS,
                1F, 1F)
            return InteractionResult.SUCCESS
        }

        if (!entity.canAssembly)
            return InteractionResult.PASS

        if (!level.isClientSide)
            level.addFreshEntity(ItemEntity(
                level,
                pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(),
                Sandwich.assemblyFromPlate(entity)))
        if (!player.hasInfiniteMaterials())
            entity.clear()
        level.playSound(
            player,
            pos,
            SoundEvents.ITEM_FRAME_REMOVE_ITEM,
            SoundSource.BLOCKS,
            1F, 1F)
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

        if (!entity.putItem(stack))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

        level.playSound(
            player,
            pos,
            SoundEvents.ITEM_FRAME_ADD_ITEM,
            SoundSource.BLOCKS,
            1F, 1F)

        if (!player.hasInfiniteMaterials())
            player.setItemInHand(
                hand,
                stack.apply { shrink(1) })

        return ItemInteractionResult.SUCCESS
    }

    override fun getDrops(state: BlockState, params: LootParams.Builder) = buildList {
        val breaker: Entity? = params.getOptionalParameter(LootContextParams.THIS_ENTITY)
        if (breaker !is Player || !breaker.hasInfiniteMaterials())
            add(Plate.defaultInstance)

        val entity = params.getParameter(LootContextParams.BLOCK_ENTITY) as PlateBlockEntity
        entity.forEachContent(::add)
    }



    override val location = "Plate".toRegistryName().toResourceLocation()
    override val registryKey: ResourceKey<out Registry<Block>> = BuiltInRegistries.BLOCK.key()
    override val holder: Holder<Block> = DeferredHolder.create(registryKey, location)
}
