package indi.fcwyzzr.minecraft.food_talks.common.registries

import indi.fcwyzzr.minecraft.food_talks.toResourceKeyOf
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.level.Level


private fun String.toDamageTypeKey() = toResourceLocation()
        .toResourceKeyOf(Registries.DAMAGE_TYPE)

infix fun ResourceKey<DamageType>.from(level: Level): DamageSource =
    level.damageSources().source(this)

val GoutDamage: ResourceKey<DamageType> = "gout".toDamageTypeKey()
val ToothacheDamage: ResourceKey<DamageType> = "toothache".toDamageTypeKey()