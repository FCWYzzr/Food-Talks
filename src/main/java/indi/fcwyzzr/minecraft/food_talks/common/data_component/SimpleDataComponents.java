package indi.fcwyzzr.minecraft.food_talks.common.data_component;

import com.mojang.serialization.Codec;
import indi.fcwyzzr.minecraft.food_talks.common.data_component.compound_food.FoodItemProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface SimpleDataComponents {
    interface SimpleCodecs {
        Codec<List<Holder<Item>>> itemList = ItemStack.ITEM_NON_AIR_CODEC.listOf();
        Codec<List<ItemStack>> itemStackList = ItemStack.SINGLE_ITEM_CODEC.listOf();
    }
    interface SimpleStreamCodecs{
        StreamCodec<RegistryFriendlyByteBuf, List<Holder<Item>>> itemList = ByteBufCodecs
            .holderRegistry(Registries.ITEM).apply(
                ByteBufCodecs.collection(NonNullList::createWithCapacity)
            );
        StreamCodec<RegistryFriendlyByteBuf, List<ItemStack>> itemStackList = ItemStack.LIST_STREAM_CODEC;
    }
    
    DataComponentType<List<Holder<Item>>> SandwichLayer = DataComponentType
        .<List<Holder<Item>>>builder()
        .persistent(SimpleCodecs.itemList)
        .networkSynchronized(SimpleStreamCodecs.itemList)
        .build();
    
}
