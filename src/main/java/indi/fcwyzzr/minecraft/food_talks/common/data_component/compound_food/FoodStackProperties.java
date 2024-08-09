package indi.fcwyzzr.minecraft.food_talks.common.data_component.compound_food;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;

import static indi.fcwyzzr.minecraft.food_talks.FoodTalksKt.toRegistryName;
import static indi.fcwyzzr.minecraft.food_talks.FoodTalksKt.toResourceLocation;

public record FoodStackProperties(
    int nutrition,
    float saturation
) {
    public static final Codec<FoodStackProperties> codec = RecordCodecBuilder.create(
        it -> it.group(
            ExtraCodecs.NON_NEGATIVE_INT
                .fieldOf("nutrition")
                .forGetter(FoodStackProperties::nutrition),
            Codec.FLOAT
                .fieldOf("saturation")
                .forGetter(FoodStackProperties::saturation)
        ).apply(it, FoodStackProperties::new)
    );
    public static final StreamCodec<ByteBuf, FoodStackProperties> streamCodec = StreamCodec
        .composite(
            ByteBufCodecs.VAR_INT, FoodStackProperties::nutrition,
            ByteBufCodecs.FLOAT, FoodStackProperties::saturation,
            FoodStackProperties::new
        );
    
    
    
    public static final String name = toRegistryName(FoodStackProperties.class.getSimpleName());
    
    public static final ResourceLocation location = toResourceLocation(name);
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FoodStackProperties>> holder = DeferredHolder.create(
        BuiltInRegistries.DATA_COMPONENT_TYPE.key(), location
    );
    
    public static DataComponentType<FoodStackProperties> type = DataComponentType
        .<FoodStackProperties>builder()
        .persistent(codec)
        .networkSynchronized(streamCodec)
        .build();
}
