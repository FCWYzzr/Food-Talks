package indi.fcwyzzr.minecraft.food_talks.common.item

import indi.fcwyzzr.minecraft.food_talks.api.common.item.CompoundFood
import indi.fcwyzzr.minecraft.food_talks.client.renderer.Bewlr
import indi.fcwyzzr.minecraft.food_talks.common.block.entity.PlateBlockEntity
import indi.fcwyzzr.minecraft.food_talks.common.data_component.SimpleDataComponents
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.core.Holder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import java.util.function.Consumer

object Sandwich: CompoundFood(
    1F
) {

    fun layers(itemStack: ItemStack): List<Holder<Item>>{
        if (!itemStack.`is`(this))
            throw IllegalArgumentException("itemStack other than sandwich should not use this method")
        return itemStack.components[SimpleDataComponents.SandwichLayer]!!
    }

    override fun uponBite(itemStack: ItemStack, biteCount: Int, entity: LivingEntity): Boolean {
        // TODO("Not yet implemented")
        return true
    }

    /**
     * note:
     * ingredients will be compiled, item infos are stored only for rendering
     */
    fun assemblyFromPlate(entity: PlateBlockEntity): ItemStack = buildItemStack {
        set(SimpleDataComponents.SandwichLayer, entity.readOnlyIngredient.map(ItemStack::getItemHolder))
        // TODO
    }

    @Suppress("removal")
    @Deprecated("???")
    override fun initializeClient(consumer: Consumer<IClientItemExtensions>) {
        consumer.accept(object :IClientItemExtensions{
            override fun getCustomRenderer() = Bewlr
        })
    }
}