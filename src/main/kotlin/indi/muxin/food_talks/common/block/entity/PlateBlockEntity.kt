package indi.muxin.food_talks.common.block.entity

import com.mojang.datafixers.types.Type
import com.mojang.logging.LogUtils
import com.mojang.serialization.Codec
import indi.muxin.food_talks.common.block.PlateBlock
import indi.muxin.food_talks.common.item.CompoundFood
import indi.muxin.food_talks.common.registries.sandwichCover
import indi.muxin.food_talks.common.registries.soupFood
import indi.muxin.food_talks.toRegistryName
import indi.muxin.food_talks.toResourceLocation
import indi.muxin.neoforged.registry.FRegistry
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
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


    val size get() = ingredients.size

    val canAssembly get() = ingredients.size > 1 && ingredients.last().`is`(sandwichCover)

    fun clear(){
        item = ItemStack.EMPTY
        ingredients.clear()
        mode = Mode.DISPLAY
        setChanged()
    }

    fun forEachFoldedContent(action: ItemStack.() -> Unit){
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
                if (itemStack.`is`(sandwichCover)){
                    mode = Mode.ASSEMBLY
                    ingredients.add(itemStack.copyWithCount(1))
                    setChanged()
                    return true
                }
                else if (item.isEmpty){
                    item = itemStack.copyWithCount(1)
                    setChanged()
                    return true
                }
                else
                    return false
            }
            Mode.ASSEMBLY -> {
                if (size == MAX_LAYER)
                    return false
                // only allow non-compound food as ingredient
                if (!CompoundFood.isHandHoldFood(itemStack, true))
                    return false
                if (itemStack.`is`(soupFood))
                    return false
                if (size == MAX_LAYER - 1 && !itemStack.`is`(sandwichCover))
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
                return v
            }
            Mode.ASSEMBLY -> {
                val v = ingredients.removeLast()
                if (ingredients.isEmpty())
                    mode = Mode.DISPLAY
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


    companion object: FRegistry<BlockEntityType<*>> {
        private val LOGGER: Logger = LogUtils.getLogger()

        private const val MAX_LAYER = 20

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "TYPE_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        val type: BlockEntityType<PlateBlockEntity> = BlockEntityType.Builder.of(
            ::PlateBlockEntity,
            PlateBlock.instance
        ).build(null as Type<*>?)

        override val registryKey: ResourceKey<out Registry<BlockEntityType<*>>> = BuiltInRegistries.BLOCK_ENTITY_TYPE.key()
        val name = "Plate".toRegistryName()
        override val location = name.toResourceLocation()
        override val holder: Holder<BlockEntityType<*>> = DeferredHolder.create(registryKey, location)

        override fun registerByHelper(helper: RegisterEvent.RegisterHelper<BlockEntityType<*>>) {
            helper.register(location, type)
        }
    }
}