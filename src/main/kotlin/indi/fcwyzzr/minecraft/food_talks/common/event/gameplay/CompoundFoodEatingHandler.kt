package indi.fcwyzzr.minecraft.food_talks.common.event.gameplay

import indi.fcwyzzr.minecraft.food_talks.api.common.item.CompoundFood
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Tick

object CompoundFoodEatingHandler {
    @SubscribeEvent
    fun tickEating(tick: Tick){
        val itemStack = tick.item
        val item = itemStack.item
        if (item !is CompoundFood)
            return


        val chewTick = item.chewTick(itemStack)
        if (tick.duration != 1)
            return


        itemStack.damageValue += 1
        if (itemStack.maxDamage == itemStack.damageValue)
            return

        tick.duration += chewTick


        val entity = tick.entity
        item.uponBite(itemStack, itemStack.damageValue, entity)
    }
}