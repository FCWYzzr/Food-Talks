package indi.fcwyzzr.minecraft.food_talks.common.block.entity

import com.mojang.logging.LogUtils
import indi.fcwyzzr.minecraft.f_lib.registry.FRegistry
import indi.fcwyzzr.minecraft.food_talks.api.common.item.CompoundFood
import indi.fcwyzzr.minecraft.food_talks.common.block.PlateBlock
import indi.fcwyzzr.minecraft.food_talks.common.registries.sandwichCover
import indi.fcwyzzr.minecraft.food_talks.toRegistryName
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
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
import kotlin.jvm.optionals.getOrNull

class PlateBlockEntity(
    pos: BlockPos,
    state: BlockState
): BlockEntity(type, pos, state) {
    val readOnlyIngredient: List<ItemStack> get() = ingredient
    val displayItem: ItemStack? get() = display
    private val ingredient = mutableListOf<ItemStack>()
    private var display: ItemStack? = null


    val size get() = ingredient.size

    val canAssembly get() = ingredient.size > 1 && ingredient.last().`is`(sandwichCover)

    fun clear(){
        display = null
        ingredient.clear()
        setChanged()
    }

    fun forEachFoldedContent(action: ItemStack.() -> Unit){
        displayItem
            ?.apply(action)
            ?: run {
                readOnlyIngredient
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

    private fun addCover(itemStack: ItemStack): Boolean{
        if (!itemStack.`is`(sandwichCover))
            return false

        ingredient.addLast(itemStack)
        setChanged()
        return true
    }

    fun putItem(itemStack: ItemStack): Boolean {
        if (display != null)
            return false
        if (size == MAX_LAYER)
            return false

        // only allow non-compound food as ingredient
        if (!CompoundFood.isHandHoldFood(itemStack, true))
            return false

        // 1. last cover
        if (size == MAX_LAYER - 1)
            return addCover(itemStack.copyWithCount(1))

        // 2. first layer
        if (size == 0)
            return addCover(itemStack.copyWithCount(1))
                    || pushDisplay(itemStack.copyWithCount(1))

        // 3.others
        // todo add seasonings(next version)
        ingredient.addLast(itemStack.copyWithCount(1))
        setChanged()
        return true
    }

    private fun pushDisplay(itemStack: ItemStack): Boolean {
        if (size != 0)
            return false
        if (display != null)
            return false

        display = itemStack
        setChanged()
        return true
    }


    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        ingredient.clear()

        tag.get("display")
            ?.apply {
                val displayItem = ItemStack.SINGLE_ITEM_CODEC.parse(
                    registries.createSerializationContext(NbtOps.INSTANCE),
                    this
                ).resultOrPartial { name ->
                    LOGGER.error("Tried to load invalid display item: '{}'", name)
                }
                display = displayItem.getOrNull()
            }
            ?: tag.getList("ingredients", 10)
                .asIterable()
                .map {
                    ItemStack.CODEC.parse(
                        registries.createSerializationContext(NbtOps.INSTANCE),
                        it
                    ).resultOrPartial { name ->
                        LOGGER.error("Tried to load invalid ingredient: '{}'", name)
                    }
                }.mapNotNull(Optional<ItemStack>::getOrNull)
                .forEach(ingredient::add)


    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)

        if (display != null){
            tag.put("display",
                ItemStack.CODEC.encodeStart(
                    registries.createSerializationContext(NbtOps.INSTANCE),
                    display
                ).getOrThrow()
            )
            return
        }

        val contentTag = ListTag().apply {
            ingredient
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

        private const val MAX_LAYER = 15

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        val type: BlockEntityType<PlateBlockEntity> = BlockEntityType.Builder.of(
            ::PlateBlockEntity,
            PlateBlock.instance
        ).build(null)

        override val registryKey: ResourceKey<out Registry<BlockEntityType<*>>> = BuiltInRegistries.BLOCK_ENTITY_TYPE.key()
        val name = "Plate".toRegistryName()
        override val location = name.toResourceLocation()
        override val holder: Holder<BlockEntityType<*>> = DeferredHolder.create(registryKey, location)

        override fun registerByHelper(helper: RegisterEvent.RegisterHelper<BlockEntityType<*>>) {
            helper.register(location, type)
        }
    }
}