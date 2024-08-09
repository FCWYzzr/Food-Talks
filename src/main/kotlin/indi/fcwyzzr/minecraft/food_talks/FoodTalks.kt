package indi.fcwyzzr.minecraft.food_talks

import com.mojang.logging.LogUtils
import indi.fcwyzzr.minecraft.food_talks.common.event.FTLifecycle
import indi.fcwyzzr.minecraft.food_talks.common.event.gameplay.CompoundFoodEatingHandler
import indi.fcwyzzr.minecraft.food_talks.common.event.gameplay.EffectHandler
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.item.alchemy.PotionContents
import net.neoforged.fml.common.Mod
import org.slf4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS


@Mod(FoodTalks.MOD_ID)
object FoodTalks {
    const val MOD_ID = "food_talks"
    const val TPS = 20

    private val lifeCycleLogger: Logger = LogUtils.getLogger()

    val random: RandomSource = RandomSource.create()

    init {
        lifeCycleLogger.info("initialize for mod $MOD_ID")

        MOD_BUS.register(FTLifecycle)

        FORGE_BUS.register(EffectHandler)
         FORGE_BUS.register(CompoundFoodEatingHandler)
    }
}


fun String.toRegistryName() = buildString{
    val text = this@toRegistryName
    append(text[0].lowercaseChar())
    for (i in 1..<text.length)
        if (text[i].isLowerCase())
            append(text[i])
        else{
            append('_')
            append(text[i].lowercaseChar())
        }
}

fun String.toMinecraftResourceLocation(): ResourceLocation =
    ResourceLocation
        .fromNamespaceAndPath("minecraft", this)

fun String.toResourceLocation(): ResourceLocation =
    ResourceLocation
        .fromNamespaceAndPath(FoodTalks.MOD_ID, this)


fun <T> ResourceLocation.toRegistryKey(): ResourceKey<Registry<T>> =
    ResourceKey.createRegistryKey(this)

fun <T> ResourceLocation.toResourceKeyOf(registry: ResourceKey<Registry<T>>): ResourceKey<T> =
    ResourceKey.create(registry, this)

fun PotionContents.toMobEffectInstanceList(): List<MobEffectInstance> = buildList {
    addAll(customEffects)
    addAll(potion.get().value().effects)
}