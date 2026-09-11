package indi.muxin.food_talks.common.events

import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.FoodTalks.MOD_ID
import indi.muxin.food_talks.common.block.BottleBlock
import indi.muxin.food_talks.common.block.BottleBlockEntity
import indi.muxin.food_talks.common.block.PlateBlock
import indi.muxin.food_talks.common.block.PlateBlockEntity
import indi.muxin.food_talks.common.data_components.registerFoodItemProperties
import indi.muxin.food_talks.common.data_components.registerFoodStackProperties
import indi.muxin.food_talks.common.data_components.registerSimpleDataComponents
import indi.muxin.food_talks.common.item.Cocktail
import indi.muxin.food_talks.common.item.Plate
import indi.muxin.food_talks.common.item.Sandwich
import indi.muxin.food_talks.common.mob_effect.*
import indi.muxin.food_talks.common.registries.FoodItemReward
import indi.muxin.food_talks.common.registries.FoodTagPunishment
import indi.muxin.food_talks.toResourceLocation
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potion
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
    fun registerComponents(event: RegisterEvent){
        event.register(BuiltInRegistries.DATA_COMPONENT_TYPE.key()){
            it.registerFoodItemProperties()
            it.registerFoodStackProperties()
            it.registerSimpleDataComponents()
        }
    }

    @SubscribeEvent
    fun registerEffectAndPotions(event: RegisterEvent){
        for (it in arrayOf(Anorexia,
                Gout,
                Toothache,
                Vomit,
                Overweight,
                PoisonResistance,
                DarknessInfused,
                Treasure,
                EndlessTreasure,
                Smelly,
                Happy,
                ProjectileImmune,
                Starving,
                Scapegoat)) {
            it registerTo event
            event.register(Registries.POTION) { rh ->
                rh.register(it.location,
                    Potion(MobEffectInstance(it.holder, 30 * FoodTalks.TPS)))
            }
        }
    }

    @SubscribeEvent
    fun registerItemBlocksAndTab(event: RegisterEvent){
        BottleBlock registerTo event
        PlateBlock  registerTo event

        BottleBlockEntity registerTo event
        PlateBlockEntity  registerTo event

        Cocktail    registerTo event
        Plate       registerTo event
        Sandwich    registerTo event

        event.register(Registries.CREATIVE_MODE_TAB){
            it.register("creative_tab".toResourceLocation(), CreativeModeTab.builder()
                .icon { ItemStack(Plate) }
                .title(Component.translatable("itemGroup.${MOD_ID}.ft_tab")) // 设置名称
                .displayItems { _, output ->
                    output.accept(Plate)
                    output.accept(Items.GLASS_BOTTLE.defaultInstance)
                }
                .build())
        }
    }


}