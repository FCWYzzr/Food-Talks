package indi.fcwyzzr.minecraft.food_talks.api.common.registry

import indi.fcwyzzr.minecraft.f_lib.registry.FRegistry
import indi.fcwyzzr.minecraft.food_talks.common.registries.foodItemRewardKey
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.neoforged.neoforge.registries.DeferredHolder

abstract class FoodItemReward(
    final override val location: ResourceLocation,
    val effect: Holder<MobEffect>
): FRegistry<FoodItemReward> {

    final override val registryKey = foodItemRewardKey

    final override val holder: Holder<FoodItemReward> = DeferredHolder.create(
        ResourceKey.create(
            registryKey, location
        )
    )



    abstract fun extendTime(ingredientCount: Int): Int
    abstract fun amplifier(ingredientCount: Int): Int
}