package indi.muxin.food_talks

import com.mojang.logging.LogUtils
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
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

    val logger: Logger = LogUtils.getLogger()
    val random: RandomSource = RandomSource.create()

    init {
        logger.info("initialize for mod $MOD_ID")
    }
}

@JeiPlugin
class JustEnoughFoodTalks: IModPlugin {
    override fun getPluginUid() = "Jei".toResourceLocation()
}

fun String.toResourceLocation(namespace: String=FoodTalks.MOD_ID): ResourceLocation =
    ResourceLocation
        .fromNamespaceAndPath(namespace, buildString{
            val text = this@toResourceLocation
            append(text[0].lowercaseChar())
            for (i in 1..<text.length)
                if (text[i].isUpperCase()) {
                    append('_')
                    append(text[i].lowercaseChar())
                } else
                    append(text[i])
        })



fun <T> ResourceLocation.toResourceKeyOf(registry: ResourceKey<Registry<T>>): ResourceKey<T> =
    ResourceKey.create(registry, this)