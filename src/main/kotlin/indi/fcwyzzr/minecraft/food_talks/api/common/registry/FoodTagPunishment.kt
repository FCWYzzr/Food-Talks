package indi.fcwyzzr.minecraft.food_talks.api.common.registry

import indi.fcwyzzr.minecraft.f_lib.registry.FRegistry
import indi.fcwyzzr.minecraft.food_talks.common.registries.foodTagPunishmentKey
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.neoforged.neoforge.registries.DeferredHolder

abstract class FoodTagPunishment(
    final override val location: ResourceLocation,
    val effect: Holder<MobEffect>
): FRegistry<FoodTagPunishment> {

    final override val holder: Holder<FoodTagPunishment> = DeferredHolder.create(
        ResourceKey.create(
            foodTagPunishmentKey, location
        )
    )

    override val registryKey = foodTagPunishmentKey

    abstract fun extendTime(punishLevel: Int): Int
    abstract fun amplifier(punishLevel: Int): Int
}