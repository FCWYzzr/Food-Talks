package indi.muxin.food_talks.common.registries

import indi.muxin.food_talks.toResourceLocation
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.item.Item




object FTTags {
    private fun ResourceLocation.toItemTag(): TagKey<Item> = ItemTags.create(this)
    private fun ResourceLocation.toMobEffectTag(): TagKey<MobEffect> = TagKey.create(
        Registries.MOB_EFFECT, this
    )

    @JvmField val SANDWICH_COVER = "ingredient/sandwichCover"
        .toResourceLocation()
        .toItemTag()

    @JvmField val MILK_IRREMOVABLE = "milkIrremovable"
        .toResourceLocation()
        .toMobEffectTag()

    @JvmField val SOUP = "foods/soup"
        .toResourceLocation("c")
        .toItemTag()
}

