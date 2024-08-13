package indi.fcwyzzr.minecraft.f_lib.registry

import indi.fcwyzzr.minecraft.food_talks.toRegistryName
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredHolder

abstract class FItem(property: Properties): Item(property), FRegistry<Item> {
    val name = javaClass.simpleName.toRegistryName()

    final override val location = name.toResourceLocation()
    final override val registryKey: ResourceKey<Registry<Item>> = BuiltInRegistries.ITEM.key()
    final override val holder: Holder<Item> = DeferredHolder.create(registryKey, location)

}