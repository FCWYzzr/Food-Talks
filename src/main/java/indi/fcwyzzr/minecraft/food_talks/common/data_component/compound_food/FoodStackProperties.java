package indi.fcwyzzr.minecraft.food_talks.common.data_component.compound_food;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import static indi.fcwyzzr.minecraft.food_talks.FoodTalksKt.toRegistryName;
import static indi.fcwyzzr.minecraft.food_talks.FoodTalksKt.toResourceLocation;

public record FoodStackProperties(
    int nutritionPerBite,
    float saturationPerBite
) {
    public static final Codec<FoodStackProperties> codec = RecordCodecBuilder.create(
        it -> it.group(
            ExtraCodecs.NON_NEGATIVE_INT
                .fieldOf("nutritionPerBite")
                .forGetter(FoodStackProperties::nutritionPerBite),
            Codec.FLOAT
                .fieldOf("saturationPerBite")
                .forGetter(FoodStackProperties::saturationPerBite)
        ).apply(it, FoodStackProperties::new)
    );
    public static final StreamCodec<ByteBuf, FoodStackProperties> streamCodec = StreamCodec
        .composite(
            ByteBufCodecs.VAR_INT, FoodStackProperties::nutritionPerBite,
            ByteBufCodecs.FLOAT, FoodStackProperties::saturationPerBite,
            FoodStackProperties::new
        );
    
    
    
    public static final String name = toRegistryName(FoodStackProperties.class.getSimpleName());
    
    public static final ResourceLocation location = toResourceLocation(name);
    
    public static final DataComponentType<FoodStackProperties> type = DataComponentType
        .<FoodStackProperties>builder()
        .persistent(codec)
        .networkSynchronized(streamCodec)
        .build();
}
