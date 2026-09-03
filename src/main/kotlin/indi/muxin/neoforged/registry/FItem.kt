package indi.muxin.neoforged.registry

import indi.muxin.food_talks.toRegistryName
import indi.muxin.food_talks.toResourceLocation
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredHolder

abstract class FItem(property: Properties): Item(property), FRegistry<Item> {
    val name = javaClass.simpleName.toRegistryName()

    final override val location = name.toResourceLocation()
    final override val registryKey: ResourceKey<out Registry<Item>> = BuiltInRegistries.ITEM.key()
    final override val holder: Holder<Item> = DeferredHolder.create(registryKey, location)

}