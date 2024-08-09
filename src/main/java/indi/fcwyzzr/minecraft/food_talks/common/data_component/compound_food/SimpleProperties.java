package indi.fcwyzzr.minecraft.food_talks.common.data_component.compound_food;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;

public interface SimpleProperties {
    DataComponentType<Integer> useTick = DataComponentType
        .<Integer> builder()
        .persistent(Codec.INT)
        .networkSynchronized(ByteBufCodecs.INT)
        .build();
}
