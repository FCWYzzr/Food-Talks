package indi.fcwyzzr.minecraft.food_talks.api.common.registry

import indi.fcwyzzr.minecraft.f_lib.registry.FRegistry
import indi.fcwyzzr.minecraft.food_talks.common.registries.foodItemRewardKey
import indi.fcwyzzr.minecraft.food_talks.toRegistryName
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredHolder
import java.time.Duration

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

    operator fun invoke(ingredientCount: Int) =
        MobEffectInstance(effect, extendTime(ingredientCount), amplifier(ingredientCount))

    companion object {
        fun countBased(
            key: ResourceKey<Item>,
            effect: Holder<MobEffect>,
            duration: Int,
            amplifier: Int = 1,
            multiplyTime: Boolean = true,
            multiplyAmplifier: Boolean = true
            ) = object: FoodItemReward(
            key.location(),
            effect
        ){
            override fun extendTime(ingredientCount: Int) = duration *
                    if (multiplyTime) ingredientCount else 1

            override fun amplifier(ingredientCount: Int) = amplifier *
                    if (multiplyAmplifier) ingredientCount else 1
        }
    }
}