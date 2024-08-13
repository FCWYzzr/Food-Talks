package indi.fcwyzzr.minecraft.food_talks.common.event.gameplay

import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.api.common.item.CompoundFood
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Tick

@EventBusSubscriber(
    modid = FoodTalks.MOD_ID,
    value = [Dist.CLIENT, Dist.DEDICATED_SERVER],
    bus = EventBusSubscriber.Bus.GAME
)
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


        val entity = tick.entity
        if (item.uponBite(itemStack, entity) && itemStack.damageValue < itemStack.maxDamage)
            tick.duration += chewTick
        else
            tick.isCanceled = true


        itemStack.damageValue += 1
    }
}