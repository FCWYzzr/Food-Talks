package indi.fcwyzzr.minecraft.food_talks

import com.mojang.logging.LogUtils
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.neoforged.fml.common.Mod
import org.slf4j.Logger


@Mod(FoodTalks.MOD_ID)
object FoodTalks {
    const val MOD_ID = "food_talks"
    const val TPS = 20

    private val lifeCycleLogger: Logger = LogUtils.getLogger()

    val random: RandomSource = RandomSource.create()

    init {
        lifeCycleLogger.info("initialize for mod $MOD_ID")
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

fun String.toResourceLocation(): ResourceLocation =
    ResourceLocation
        .fromNamespaceAndPath(FoodTalks.MOD_ID, this)


fun <T> ResourceLocation.toRegistryKey(): ResourceKey<Registry<T>> =
    ResourceKey.createRegistryKey(this)

fun <T> ResourceLocation.toResourceKeyOf(registry: ResourceKey<Registry<T>>): ResourceKey<T> =
    ResourceKey.create(registry, this)