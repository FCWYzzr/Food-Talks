package indi.muxin.food_talks.common.data_components

import indi.muxin.food_talks.toRegistryName
import indi.muxin.food_talks.toResourceLocation
import net.minecraft.core.Holder
import net.minecraft.core.NonNullList
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.world.food.FoodProperties.PossibleEffect
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.RegisterEvent
import java.util.function.IntFunction

val SandwichLayerDCType: DataComponentType<List<Holder<Item>>> = DataComponentType
    .builder<List<Holder<Item>>>()
    .persistent(ItemStack.ITEM_NON_AIR_CODEC.listOf())
    .networkSynchronized(ByteBufCodecs
        .holderRegistry(Registries.ITEM).apply(
            ByteBufCodecs.collection { NonNullList.createWithCapacity(it) }))
    .build()

val PossibleEffectListDCType: DataComponentType<List<PossibleEffect>> = DataComponentType
    .builder<List<PossibleEffect>>()
    .persistent(PossibleEffect.CODEC.listOf())
    .networkSynchronized(PossibleEffect.STREAM_CODEC.apply(
        ByteBufCodecs.collection(IntFunction { NonNullList.createWithCapacity(it) })))
    .build()

fun RegisterEvent.RegisterHelper<DataComponentType<*>>.registerSimpleDataComponents() {
    register("SandwichLayer".toRegistryName().toResourceLocation(), SandwichLayerDCType)
    register("PossibleEffectList".toRegistryName().toResourceLocation(), PossibleEffectListDCType)
}