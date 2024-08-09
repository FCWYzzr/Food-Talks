package indi.fcwyzzr.minecraft.f_lib.registry

import indi.fcwyzzr.minecraft.food_talks.toRegistryName
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.neoforged.neoforge.registries.DeferredHolder

abstract class FMobEffect(
    cate: MobEffectCategory,
    color: Int
): MobEffect(cate, color), FRegistry<MobEffect> {
    final override val registryKey: ResourceKey<out Registry<MobEffect>> = BuiltInRegistries.MOB_EFFECT.key()

    final override val location = this::class.simpleName!!
        .toRegistryName()
        .toResourceLocation()


    final override val holder: Holder<MobEffect> = DeferredHolder.create(
        ResourceKey.create(
            Registries.MOB_EFFECT, location
        )
    )
}