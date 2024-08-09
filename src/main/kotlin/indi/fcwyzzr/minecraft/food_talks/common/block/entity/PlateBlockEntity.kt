package indi.fcwyzzr.minecraft.food_talks.common.block.entity

import indi.fcwyzzr.minecraft.f_lib.registry.FRegistry
import indi.fcwyzzr.minecraft.food_talks.common.block.PlateBlock
import indi.fcwyzzr.minecraft.food_talks.common.registries.sandwichCover
import indi.fcwyzzr.minecraft.food_talks.toRegistryName
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.RegisterEvent

class PlateBlockEntity(
    pos: BlockPos,
    state: BlockState
): BlockEntity(type, pos, state) {
    val ingredient = mutableListOf<ItemStack>()
    val size get() = ingredient.size

    val frontCover: ItemStack? get() = ingredient.getOrNull(0)
    val lastCover: ItemStack? get(){
        return if (ingredient.size < 2 || ingredient.last().`is`(sandwichCover))
            null
        else
            ingredient.last()
    }

    private fun addCover(itemStack: ItemStack): Boolean{
        if (!itemStack.`is`(sandwichCover))
            return false

        ingredient.addLast(itemStack)
        return true
    }

    fun addLayer(itemStack: ItemStack): Boolean {
        if (size == MAX_LAYER)
            return false
        if (itemStack.components[DataComponents.FOOD] == null)
            return false

        if (size == 0 || size == MAX_LAYER - 1)
            return addCover(itemStack.copyWithCount(1))

        ingredient.addLast(itemStack.copyWithCount(1))
        return true
    }


    companion object: FRegistry<BlockEntityType<*>> {
        private const val MAX_LAYER = 15
        private const val MAX_INGREDIENT = MAX_LAYER - 2

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