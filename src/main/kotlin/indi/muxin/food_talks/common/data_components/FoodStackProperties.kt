package indi.muxin.food_talks.common.data_components

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import indi.muxin.food_talks.toRegistryName
import indi.muxin.food_talks.toResourceLocation
import io.netty.buffer.ByteBuf
import net.minecraft.core.component.DataComponentType
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs
import net.neoforged.neoforge.registries.RegisterEvent
import java.util.function.Function



data class FoodStackProperties(
    val nutritionPerBite: Int,
    val saturationPerBite: Float
)
private val codec: Codec<FoodStackProperties> = RecordCodecBuilder.create(
    Function {
        it.group(
            ExtraCodecs.NON_NEGATIVE_INT
                .fieldOf("nutritionPerBite")
                .forGetter<FoodStackProperties?>(FoodStackProperties::nutritionPerBite),
            Codec.FLOAT
                .fieldOf("saturationPerBite")
                .forGetter<FoodStackProperties?>(FoodStackProperties::saturationPerBite)
        ).apply<FoodStackProperties?>(it) { nutritionPerBite: Int, saturationPerBite: Float ->
            FoodStackProperties(
                nutritionPerBite,
                saturationPerBite
            )
        }
    }
)
private val streamCodec: StreamCodec<ByteBuf, FoodStackProperties> = StreamCodec
    .composite(
        ByteBufCodecs.VAR_INT, FoodStackProperties::nutritionPerBite,
        ByteBufCodecs.FLOAT, FoodStackProperties::saturationPerBite
    ) { nutritionPerBite: Int, saturationPerBite: Float ->
        FoodStackProperties(
            nutritionPerBite,
            saturationPerBite
        )
    }

private val location: ResourceLocation = FoodStackProperties::class.java.getSimpleName().toRegistryName().toResourceLocation()

val FoodStackPropertiesDCType: DataComponentType<FoodStackProperties> = DataComponentType
    .builder<FoodStackProperties>()
    .persistent(codec)
    .networkSynchronized(streamCodec)
    .build()

fun RegisterEvent.RegisterHelper<DataComponentType<*>>.registerFoodStackProperties() {
    register(location, FoodStackPropertiesDCType)
}