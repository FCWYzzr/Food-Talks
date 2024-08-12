package indi.fcwyzzr.minecraft.food_talks.common.data_component.compound_food;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Optional;

import static indi.fcwyzzr.minecraft.food_talks.FoodTalksKt.toRegistryName;
import static indi.fcwyzzr.minecraft.food_talks.FoodTalksKt.toResourceLocation;

public record FoodItemProperties(
    boolean canAlwaysEat,
    Optional<ItemStack> convertsTo,
    float chewSeconds
) {
    public static final Codec<FoodItemProperties> codec = RecordCodecBuilder.create(
        it -> it.group(
            Codec.BOOL
                .fieldOf("can_always_eat")
                .forGetter(FoodItemProperties::canAlwaysEat),
            ItemStack.SINGLE_ITEM_CODEC
                .optionalFieldOf("using_converts_to")
                .forGetter(FoodItemProperties::convertsTo),
            Codec.FLOAT
                .fieldOf("eat_seconds")
                .forGetter(FoodItemProperties::chewSeconds)
        ).apply(it, FoodItemProperties::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, FoodItemProperties> streamCodec = StreamCodec
        .composite(
            ByteBufCodecs.BOOL,
            FoodItemProperties::canAlwaysEat,
            
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs::optional),
            FoodItemProperties::convertsTo,
            
            ByteBufCodecs.FLOAT,
            FoodItemProperties::chewSeconds,
            
            FoodItemProperties::new
        );
    
    
    
    public static final String name = toRegistryName(FoodItemProperties.class.getSimpleName());
    
    public static final ResourceLocation location = toResourceLocation(name);
    
    public static DataComponentType<FoodItemProperties> type = DataComponentType
        .<FoodItemProperties>builder()
        .persistent(codec)
        .networkSynchronized(streamCodec)
        .build();
}
