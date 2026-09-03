package indi.muxin.food_talks.common.creative_tab

import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.common.item.Plate
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier


val creativeModeTabs: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FoodTalks.MOD_ID)

@Suppress("unused")
val PlateTab: Holder<CreativeModeTab> = creativeModeTabs.register(
    "ft_tab",
    Supplier {
        CreativeModeTab.builder()
            .icon { ItemStack(Plate) } // 设置图标
            .title(Component.translatable("itemGroup.${FoodTalks.MOD_ID}.ft_tab")) // 设置名称
            .displayItems { _: ItemDisplayParameters, output: CreativeModeTab.Output ->
                output.accept(Plate)
            }
            .build()
    }
)