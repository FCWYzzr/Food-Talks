package indi.muxin.food_talks.common.mob_effect

import indi.muxin.neoforged.registry.FMobEffect
import indi.muxin.food_talks.toResourceLocation
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes

object PoisonResistance: FMobEffect(
    MobEffectCategory.BENEFICIAL,
    0xff0000
)

object DarknessInfused: FMobEffect(
    MobEffectCategory.BENEFICIAL, 0x0
){
    init {
        val location = "effect.darkness_infused".toResourceLocation()
        addAttributeModifier(
            Attributes.MOVEMENT_SPEED,
            location,
            0.5,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        )
        addAttributeModifier(
            Attributes.ATTACK_SPEED,
            location,
            1.0,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        )
        addAttributeModifier(
            Attributes.ATTACK_DAMAGE,
            location,
            0.5,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        )
        addAttributeModifier(
            Attributes.KNOCKBACK_RESISTANCE,
            location,
            5.0,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        )
        addAttributeModifier(
            Attributes.LUCK,
            location,
            1.0,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        )
    }
}

object Treasure: FMobEffect(
    MobEffectCategory.BENEFICIAL, 0xffff00
)

object EndlessTreasure: FMobEffect(
    MobEffectCategory.BENEFICIAL, 0xffff00
)

object Smelly: FMobEffect(
    MobEffectCategory.BENEFICIAL, 0xffffff
)

object Happy: FMobEffect(
    MobEffectCategory.BENEFICIAL, 0x993333
)

object ProjectileImmune: FMobEffect(
    MobEffectCategory.BENEFICIAL, 0x993333
)

object Starving: FMobEffect(
    MobEffectCategory.BENEFICIAL, 0xa42312
)

object Scapegoat: FMobEffect(
    MobEffectCategory.BENEFICIAL, 0x000000
)