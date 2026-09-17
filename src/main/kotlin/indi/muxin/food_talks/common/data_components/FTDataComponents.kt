@file:Suppress("unused")

package indi.muxin.food_talks.common.data_components

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import indi.muxin.food_talks.toResourceLocation
import net.minecraft.core.Holder
import net.minecraft.core.NonNullList
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.food.FoodProperties.PossibleEffect
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.RegisterEvent
import java.util.function.Function
import java.util.function.IntFunction

data class FoodStackProperties(
    val nutritionPerBite: Int,
    val saturationPerBite: Float
)

data class FoodItemProperties(
    val canAlwaysEat: Boolean,
    val convertsTo: ItemStack,
    val chewTick: Int
)


object FTDataComponents {
    @JvmField val FOOD_ITEM_PROPERTIES: DataComponentType<FoodItemProperties> = DataComponentType
        .builder<FoodItemProperties>()
        .persistent(RecordCodecBuilder.create(
            Function {
                it.group(
                    Codec.BOOL
                        .fieldOf("can_always_eat")
                        .forGetter(FoodItemProperties::canAlwaysEat),
                    ItemStack.SINGLE_ITEM_CODEC
                        .fieldOf("using_converts_to")
                        .forGetter(FoodItemProperties::convertsTo),
                    Codec.INT
                        .fieldOf("eat_seconds")
                        .forGetter(FoodItemProperties::chewTick)
                ).apply(it, ::FoodItemProperties)
            }))
        .networkSynchronized(StreamCodec
            .composite(
                ByteBufCodecs.BOOL,
                FoodItemProperties::canAlwaysEat,

                ItemStack.STREAM_CODEC,
                FoodItemProperties::convertsTo,

                ByteBufCodecs.INT,
                FoodItemProperties::chewTick,

                ::FoodItemProperties
            ))
        .build()

    @JvmField val FOOD_STACK_PROPERTIES: DataComponentType<FoodStackProperties> =DataComponentType
        .builder<FoodStackProperties>()
        .persistent(RecordCodecBuilder.create(
            Function {
                it.group(
                    ExtraCodecs.NON_NEGATIVE_INT
                        .fieldOf("nutritionPerBite")
                        .forGetter<FoodStackProperties?>(FoodStackProperties::nutritionPerBite),
                    Codec.FLOAT
                        .fieldOf("saturationPerBite")
                        .forGetter<FoodStackProperties?>(FoodStackProperties::saturationPerBite)
                ).apply<FoodStackProperties?>(it, ::FoodStackProperties)
            }))
        .networkSynchronized(StreamCodec
                .composite(
                    ByteBufCodecs.VAR_INT, FoodStackProperties::nutritionPerBite,
                    ByteBufCodecs.FLOAT, FoodStackProperties::saturationPerBite,
                    ::FoodStackProperties
                ))
        .build()

    @JvmField val SANDWICH_LAYERS: DataComponentType<List<Holder<Item>>> = DataComponentType
        .builder<List<Holder<Item>>>()
        .persistent(ItemStack.ITEM_NON_AIR_CODEC.listOf())
        .networkSynchronized(ByteBufCodecs
            .holderRegistry(Registries.ITEM).apply(
                ByteBufCodecs.collection { NonNullList.createWithCapacity(it) }))
        .build()

    @JvmField val POSSIBLE_EFFECTS: DataComponentType<List<PossibleEffect>> = DataComponentType
        .builder<List<PossibleEffect>>()
        .persistent(PossibleEffect.CODEC.listOf())
        .networkSynchronized(PossibleEffect.STREAM_CODEC.apply(
            ByteBufCodecs.collection(IntFunction { NonNullList.createWithCapacity(it) })))
        .build()
}

@Suppress("UNCHECKED_CAST")
object FTDataComponentHolders {
    private fun toHolder(name: String): Holder<DataComponentType<*>> = DeferredHolder
        .create(Registries.DATA_COMPONENT_TYPE, name.toResourceLocation())


    @JvmField val FOOD_ITEM_PROPERTIES  = toHolder("FoodItemProperties") as Holder<DataComponentType<FoodItemProperties>>
    @JvmField val FOOD_STACK_PROPERTIES = toHolder("FoodStackProperties") as Holder<DataComponentType<FoodStackProperties>>
    @JvmField val SANDWICH_LAYERS       = toHolder("SandwichLayers") as Holder<DataComponentType<List<Holder<Item>>>>
    @JvmField val POSSIBLE_EFFECTS      = toHolder("PossibleEffects") as Holder<DataComponentType<List<PossibleEffect>>>
}

fun registerFTDataComponents(ev: RegisterEvent) {
    ev.register(Registries.DATA_COMPONENT_TYPE) {
        it.register(FTDataComponentHolders.FOOD_ITEM_PROPERTIES .key!!.location(), FTDataComponents.FOOD_ITEM_PROPERTIES )
        it.register(FTDataComponentHolders.FOOD_STACK_PROPERTIES.key!!.location(), FTDataComponents.FOOD_STACK_PROPERTIES)
        it.register(FTDataComponentHolders.SANDWICH_LAYERS      .key!!.location(), FTDataComponents.SANDWICH_LAYERS      )
        it.register(FTDataComponentHolders.POSSIBLE_EFFECTS     .key!!.location(), FTDataComponents.POSSIBLE_EFFECTS     )

    }
}