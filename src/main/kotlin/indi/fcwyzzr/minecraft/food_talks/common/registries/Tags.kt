package indi.fcwyzzr.minecraft.food_talks.common.registries

import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.item.Item


fun ResourceLocation.toItemTag(): TagKey<Item> = ItemTags.create(this)
fun ResourceLocation.toMobEffectTag(): TagKey<MobEffect> = TagKey.create(
    Registries.MOB_EFFECT, this
)

val sandwichCover = "ingredient/sandwich_cover"
    .toResourceLocation()
    .toItemTag()

val milkIrremovable = "milk_irremovable"
    .toResourceLocation()
    .toMobEffectTag()