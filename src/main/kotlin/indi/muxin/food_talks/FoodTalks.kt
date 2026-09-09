package indi.muxin.food_talks

import com.mojang.logging.LogUtils
import indi.muxin.food_talks.common.item.Plate
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.registries.DeferredRegister
import org.slf4j.Logger
import java.util.function.Supplier


@Mod(FoodTalks.MOD_ID)
class FoodTalks {
    companion object {
        const val MOD_ID = "food_talks"
        const val TPS = 20

        private val lifeCycleLogger: Logger = LogUtils.getLogger()

        val random: RandomSource = RandomSource.create()
    }

    init {
        lifeCycleLogger.info("initialize for mod $MOD_ID")

        val creativeModeTabs: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID)
        creativeModeTabs.register(
            "food_talks_creative_tab",
            Supplier {
                CreativeModeTab.builder()
                    .icon { ItemStack(Plate) } // 设置图标
                    .title(Component.translatable("itemGroup.${MOD_ID}.ft_tab")) // 设置名称
                    .displayItems { _, output ->
                        output.accept(Plate)
                    }
                    .build()
            }
        )
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

fun <T> ResourceLocation.toResourceKeyOf(registry: ResourceKey<Registry<T>>): ResourceKey<T> =
    ResourceKey.create(registry, this)