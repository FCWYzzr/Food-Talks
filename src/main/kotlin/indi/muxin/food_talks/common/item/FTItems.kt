@file:Suppress("unused")

package indi.muxin.food_talks.common.item

import indi.muxin.food_talks.common.block.FTBlocks
import indi.muxin.food_talks.toResourceLocation
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.RegisterEvent

object FTItems {
    @JvmField val BOWL              = BlockItem(FTBlocks.BOWL_BLOCK, Item.Properties())
    @JvmField val GLASS_BOTTLE      = BlockItem(FTBlocks.BOTTLE_BLOCK, Item.Properties())
    @JvmField val PLATE             = BlockItem(FTBlocks.PLATE_BLOCK, Item.Properties())
    @JvmField val COCKTAIL          = Cocktail
    @JvmField val SANDWICH          = Sandwich
    @JvmField val TIPPED_BREAD      = Item(Item.Properties().stacksTo(64))
}

object FTItemHolders {
    private fun toHolder(name: String) = DeferredHolder
        .create(Registries.ITEM, name.toResourceLocation())

    @JvmField val BOWL              : Holder<Item> = toHolder("Bowl")
    @JvmField val GLASS_BOTTLE      : Holder<Item> = toHolder("GlassBottle")
    @JvmField val PLATE             : Holder<Item> = toHolder("Plate")
    @JvmField val COCKTAIL          : Holder<Item> = toHolder("Cocktail")
    @JvmField val SANDWICH          : Holder<Item> = toHolder("Sandwich")
    @JvmField val TIPPED_BREAD      : Holder<Item> = toHolder("TippedBread")
}

fun registerFTItems(ev: RegisterEvent){
    ev.register(Registries.ITEM){
        it.register(FTItemHolders.BOWL.key!!        ,   FTItems.BOWL)
        it.register(FTItemHolders.GLASS_BOTTLE.key!!,   FTItems.GLASS_BOTTLE)
        it.register(FTItemHolders.PLATE.key!!       ,   FTItems.PLATE)
        it.register(FTItemHolders.COCKTAIL.key!!    ,   FTItems.COCKTAIL)
        it.register(FTItemHolders.SANDWICH.key!!    ,   FTItems.SANDWICH)
        it.register(FTItemHolders.TIPPED_BREAD.key!!,   FTItems.TIPPED_BREAD)
    }
}