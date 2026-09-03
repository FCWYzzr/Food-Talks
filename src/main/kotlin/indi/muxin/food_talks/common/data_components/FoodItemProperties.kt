package indi.muxin.food_talks.common.data_components

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import indi.muxin.food_talks.toRegistryName
import indi.muxin.food_talks.toResourceLocation
import net.minecraft.core.component.DataComponentType
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.RegisterEvent
import java.util.*
import java.util.function.Function


data class FoodItemProperties(
    val canAlwaysEat: Boolean,
    val convertsTo: Optional<ItemStack>,
    val chewSeconds: Float
)


private val codec: Codec<FoodItemProperties> = RecordCodecBuilder.create(
    Function {
        it.group(
            Codec.BOOL
                .fieldOf("can_always_eat")
                .forGetter(FoodItemProperties::canAlwaysEat),
            ItemStack.SINGLE_ITEM_CODEC
                .optionalFieldOf("using_converts_to")
                .forGetter(FoodItemProperties::convertsTo),
            Codec.FLOAT
                .fieldOf("eat_seconds")
                .forGetter(FoodItemProperties::chewSeconds)
        ).apply(it) { canAlwaysEat: Boolean, convertsTo: Optional<ItemStack>, chewSeconds: Float ->
            FoodItemProperties(
                canAlwaysEat,
                convertsTo,
                chewSeconds
            )
        }
    }
)
private val streamCodec: StreamCodec<RegistryFriendlyByteBuf, FoodItemProperties> = StreamCodec
    .composite(
        ByteBufCodecs.BOOL,
        FoodItemProperties::canAlwaysEat,

        ItemStack.STREAM_CODEC.apply { ByteBufCodecs.optional(it) },
        FoodItemProperties::convertsTo,

        ByteBufCodecs.FLOAT,
        FoodItemProperties::chewSeconds

    ) { canAlwaysEat: Boolean, convertsTo: Optional<ItemStack>, chewSeconds: Float ->
        FoodItemProperties(
            canAlwaysEat,
            convertsTo,
            chewSeconds
        )
    }


private val location: ResourceLocation = FoodItemProperties::class.java.getSimpleName().toRegistryName().toResourceLocation()

val FoodItemPropertiesDCType: DataComponentType<FoodItemProperties> = DataComponentType
    .builder<FoodItemProperties>()
    .persistent(codec)
    .networkSynchronized(streamCodec)
    .build()

fun RegisterEvent.RegisterHelper<DataComponentType<*>>.registerFoodItemProperties() {
    register(location, FoodItemPropertiesDCType)
}