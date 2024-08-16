package indi.fcwyzzr.minecraft.f_lib.registry

import indi.fcwyzzr.minecraft.food_talks.toRegistryName
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.Potions
import net.neoforged.neoforge.registries.DeferredHolder

class FPotion(
    vararg effects: MobEffectInstance,
    val name: String? = null
): Potion(name, *effects), FRegistry<Potion> {
    override val registryKey: ResourceKey<out Registry<Potion>> = BuiltInRegistries.POTION.key()

    override val location: ResourceLocation = name
        ?.toRegistryName()
        ?.toResourceLocation()
        ?: effects[0]
            .effect
            .key!!
            .location()

    override val holder: Holder<Potion> = DeferredHolder.create(
        ResourceKey.create(
            Registries.POTION, location
        )
    )
}