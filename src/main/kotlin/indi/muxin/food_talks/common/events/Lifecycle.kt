package indi.muxin.food_talks.common.events

import indi.muxin.food_talks.FoodTalks.MOD_ID
import indi.muxin.food_talks.common.block.registerFTBlocksAndEntities
import indi.muxin.food_talks.common.data_components.registerFTDataComponents
import indi.muxin.food_talks.common.item.FTItems
import indi.muxin.food_talks.common.item.registerFTItems
import indi.muxin.food_talks.common.mob_effect.FTPotionHolders
import indi.muxin.food_talks.common.mob_effect.registerFTMobEffects
import indi.muxin.food_talks.common.registries.FoodItemReward
import indi.muxin.food_talks.common.registries.FoodTagPunishment
import indi.muxin.food_talks.toResourceLocation
import indi.muxin.neoforged.utils.buildItemStack
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionContents
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.registries.RegisterEvent
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent

@EventBusSubscriber(
    modid = MOD_ID,
    value = [Dist.CLIENT, Dist.DEDICATED_SERVER]
)
object Lifecycle {
    @SubscribeEvent
    fun registerDataMaps(event: RegisterDataMapTypesEvent){
        FoodItemReward      registerTo event
        FoodTagPunishment   registerTo event
    }

    @SubscribeEvent
    fun registerElements(event: RegisterEvent){
        registerFTDataComponents(event)
        registerFTMobEffects(event)
        registerFTBlocksAndEntities(event)
        registerFTItems(event)


        event.register(Registries.CREATIVE_MODE_TAB){
            it.register("creative_tab".toResourceLocation(), CreativeModeTab.builder()
                .icon { FTItems.PLATE.defaultInstance }
                .title(Component.translatable("itemGroup.${MOD_ID}.ft_tab")) // 设置名称
                .displayItems { _, output ->
                    output.accept(FTItems.PLATE.defaultInstance)
                    output.accept(FTItems.GLASS_BOTTLE.defaultInstance)
                    output.accept(FTItems.BOWL.defaultInstance)
                    for (potion in arrayOf(FTPotionHolders.HAPPY,
                                            FTPotionHolders.DARKNESS_INFUSED,
                                            FTPotionHolders.POISON_RESISTANCE,
                                            FTPotionHolders.TREASURE,
                                            FTPotionHolders.ENDLESS_TREASURE,
                                            FTPotionHolders.SMELLY,
                                            FTPotionHolders.PROJECTILE_IMMUNE,
                                            FTPotionHolders.STARVING,
                                            FTPotionHolders.SCAPEGOAT,
                                            FTPotionHolders.ANOREXIA,
                                            FTPotionHolders.GOUT,
                                            FTPotionHolders.VOMIT,
                                            FTPotionHolders.TOOTHACHE,
                                            FTPotionHolders.OVERWEIGHT))
                        output.accept(buildItemStack(Items.POTION){ itemStack ->
                            itemStack[DataComponents.POTION_CONTENTS] = PotionContents(potion)
                        })
                }
                .build())
        }
    }


}