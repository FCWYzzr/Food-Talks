package indi.fcwyzzr.minecraft.food_talks.common.effects.harmful

import indi.fcwyzzr.minecraft.f_lib.registry.FMobEffect
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes

object Toothache: FMobEffect(
    MobEffectCategory.HARMFUL,
    0xffffff
)

object Overweight: FMobEffect(
    MobEffectCategory.HARMFUL,
    0xffffff
){
    init {
        addAttributeModifier(
            Attributes.MOVEMENT_SPEED,
            "effect.overweight".toResourceLocation(),
            -0.1,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        )
    }
}