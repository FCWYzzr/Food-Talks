package indi.fcwyzzr.minecraft.food_talks.common.registries

import indi.fcwyzzr.minecraft.food_talks.toRegistryName
import indi.fcwyzzr.minecraft.food_talks.toResourceKeyOf
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.alchemy.Potion
import net.neoforged.neoforge.registries.DeferredHolder


val liquorKey = "liquor".toRegistryName()
    .toResourceLocation()
    .toResourceKeyOf(Registries.POTION)

val liquorHolder: DeferredHolder<Potion, Potion> = DeferredHolder.create(liquorKey)