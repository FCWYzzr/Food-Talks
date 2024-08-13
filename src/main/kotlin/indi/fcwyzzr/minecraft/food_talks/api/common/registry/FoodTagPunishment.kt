package indi.fcwyzzr.minecraft.food_talks.api.common.registry

import indi.fcwyzzr.minecraft.f_lib.registry.FRegistry
import indi.fcwyzzr.minecraft.food_talks.common.registries.foodTagPunishmentKey
import indi.fcwyzzr.minecraft.food_talks.toRegistryName
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
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

    operator fun invoke(punishLevel: Int) =
        MobEffectInstance(effect, extendTime(punishLevel), amplifier(punishLevel))

    companion object {
        fun levelBased(
            location: ResourceLocation,
            effect: Holder<MobEffect>, duration: Int, amplifier: Int = 1,
            multiplyTime: Boolean = true,
            multiplyAmplifier: Boolean = true
        ) = object: FoodTagPunishment(
            location,
            effect
        ){
            override fun extendTime(punishLevel: Int) = duration *
                    if (multiplyTime) punishLevel else 1

            override fun amplifier(punishLevel: Int) = amplifier *
                    if (multiplyAmplifier) punishLevel else 1
        }
    }
}