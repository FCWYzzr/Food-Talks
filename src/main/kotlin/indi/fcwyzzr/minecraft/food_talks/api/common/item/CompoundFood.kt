package indi.fcwyzzr.minecraft.food_talks.api.common.item

import indi.fcwyzzr.minecraft.f_lib.registry.FItem
import indi.fcwyzzr.minecraft.f_lib.utils.buildDataComponentPatch
import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.common.data_component.compound_food.FoodItemProperties
import indi.fcwyzzr.minecraft.food_talks.common.data_component.compound_food.FoodStackProperties
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level
import java.util.*

abstract class CompoundFood(
    bitesNeed: Int,
    chewSeconds: Float,
    useConvertsTo: ItemStack? = null,
    canAlwaysEat: Boolean = false
): FItem(Properties().apply {
    durability(bitesNeed)
    setNoRepair()
    component(FoodItemProperties.type, FoodItemProperties(
        canAlwaysEat, Optional.ofNullable(useConvertsTo), chewSeconds
    ))
}) {
    fun chewTick(stack: ItemStack) = (stack.components[FoodItemProperties.type]!!.chewSeconds * FoodTalks.TPS).toInt()
    fun bites(stack: ItemStack) = stack.maxDamage
    override fun getUseAnimation(stack: ItemStack) = UseAnim.EAT
//    override fun getUseDuration(stack: ItemStack, entity: LivingEntity) =
//        (stack.components[FoodItemProperties.type]!!.chewSeconds * FoodTalks.TPS).toInt()

    override fun getUseDuration(stack: ItemStack, entity: LivingEntity) = 5 + chewTick(stack)
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(usedHand)
        if (!player.canEat(itemStack.components[FoodItemProperties.type]!!.canAlwaysEat))
            return InteractionResultHolder.pass(itemStack)
        player.startUsingItem(usedHand)
        return InteractionResultHolder.success(itemStack)
    }

    /**
     * @return: boolean: should continue eating?
     */
    abstract fun uponBite(itemStack: ItemStack, biteCount: Int, entity: LivingEntity): Boolean

    override fun onUseTick(level: Level, entity: LivingEntity, itemStack: ItemStack, remainingUseDuration: Int) {
        val tick = 72000 - remainingUseDuration
        if (tick <= 20 || tick % 20 != 1)
            return

        if (uponBite(itemStack, tick / 20 - 1, entity)){
            itemStack.damageValue += 1
            return
        }
    }

    open fun buildItemStack(action: DataComponentPatch.Builder.() -> Unit): ItemStack{
        val itemProperty: FoodItemProperties = this.components()[FoodItemProperties.type]!!
        val stackProperty: FoodStackProperties? = this.components()[FoodStackProperties.type]

        val itemStack = ItemStack(Holder.direct(this), 1, buildDataComponentPatch {
            set(DataComponents.FOOD, FoodProperties(
                stackProperty ?.nutrition ?: 0,
                stackProperty ?.saturation ?: 0F,
                itemProperty.canAlwaysEat,
                itemProperty.chewSeconds,
                itemProperty.convertsTo,
                listOf()
            ))

            this.action()
        })

        return itemStack
    }

    override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        return stack.components[FoodItemProperties.type]!!.convertsTo.orElse(ItemStack.EMPTY)
    }
}