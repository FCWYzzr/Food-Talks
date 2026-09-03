package indi.muxin.food_talks.common.item

import indi.muxin.neoforged.registry.FRegistry
import indi.muxin.food_talks.common.block.PlateBlock
import indi.muxin.food_talks.toRegistryName
import indi.muxin.food_talks.toResourceLocation
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredHolder

object Plate: BlockItem(
    PlateBlock.instance,
    Properties()
), FRegistry<Item> {
    val name = "Plate".toRegistryName()
    override val registryKey: ResourceKey<out Registry<Item>> = BuiltInRegistries.ITEM.key()
    override val location = name.toResourceLocation()
    override val holder: Holder<Item> = DeferredHolder.create(registryKey, location)
}