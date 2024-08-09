package indi.fcwyzzr.minecraft.food_talks.common.event.gameplay

import indi.fcwyzzr.minecraft.food_talks.common.effects.*
import indi.fcwyzzr.minecraft.food_talks.common.registries.ToothacheDamage
import indi.fcwyzzr.minecraft.food_talks.common.registries.from
import net.minecraft.core.component.DataComponents
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Start
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Tick
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove

object EffectHandler {
    @SubscribeEvent
    fun entityTryToRemoveEffect(event: Remove){
        if (event.cure != null)
            if (event.effect.`is`(Anorexia.location)
                || event.effect.`is`(Gout.location)
                || event.effect.`is`(Overweight.location)
                || event.effect.`is`(Toothache.location)
                || event.effect.`is`(Vomit.location)
            )

                event.isCanceled = true
    }

    @SubscribeEvent
    fun entityTryToEatWhenAnorexiaOrVomit(event: Start){
        if (!event.entity.hasEffect(Anorexia.holder)
            && !event.entity.hasEffect(Vomit.holder))
            return

        val holdItem = event.entity.mainHandItem

        if (holdItem.isEmpty)
            return

        if (holdItem.`is` { !it.isBound })
            return

        if (holdItem.`is` { it.value().components().has(DataComponents.FOOD) })
            event.isCanceled = true
    }

    @SubscribeEvent
    fun entityEatingWhenToothache(event: Tick){
        if (event.duration < 20)
            return
        if (event.duration % 10 != 0)
            return
        if (!event.entity.hasEffect(Toothache.holder))
            return
        if (!event.item.item.components().has(DataComponents.FOOD))
            return

        event.entity.hurt(ToothacheDamage from event.entity.level(), 1F)
    }

}