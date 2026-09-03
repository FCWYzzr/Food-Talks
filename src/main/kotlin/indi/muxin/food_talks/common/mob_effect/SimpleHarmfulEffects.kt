package indi.muxin.food_talks.common.mob_effect

import indi.muxin.neoforged.registry.FMobEffect
import indi.muxin.food_talks.toResourceLocation
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