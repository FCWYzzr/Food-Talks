package indi.fcwyzzr.minecraft.food_talks.api.common.item

import indi.fcwyzzr.minecraft.f_lib.registry.FItem
import indi.fcwyzzr.minecraft.f_lib.utils.buildDataComponentPatch
import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.common.data_component.compound_food.FoodItemProperties
import indi.fcwyzzr.minecraft.food_talks.common.mob_effect.harmful.Anorexia
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level
import java.util.*
import kotlin.jvm.optionals.getOrNull

abstract class CompoundFood(
    chewSeconds: Float,
    bitesNeed: Int = 1,
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
        if (!Anorexia.canEat(itemStack, player))
            return InteractionResultHolder.fail(itemStack)
        player.startUsingItem(usedHand)
        return InteractionResultHolder.success(itemStack)
    }

    /**
     * @return: boolean: should continue eating?
     */
    abstract fun uponBite(itemStack: ItemStack, entity: LivingEntity): Boolean

    open fun buildItemStack(action: DataComponentPatch.Builder.() -> Unit): ItemStack{
        val itemStack = ItemStack(Holder.direct(this), 1, buildDataComponentPatch {
            this.action()
        })

        return itemStack
    }

    override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        if (stack.damageValue < stack.maxDamage)
            return stack
        return stack.components[FoodItemProperties.type]!!.convertsTo.orElse(ItemStack.EMPTY)
    }

    companion object{
        fun isFood(itemStack: ItemStack, notCompound: Boolean = false): Boolean{
            return if (notCompound)
                isVanillaFood(itemStack) && itemStack.item !is CompoundFood
            else
                isVanillaFood(itemStack) || itemStack.item is CompoundFood
        }

        private fun isVanillaFood(itemStack: ItemStack): Boolean{
            return itemStack.components.has(DataComponents.FOOD)
        }

        fun containerOrNull(itemStack: ItemStack): ItemStack? {
            if (!isFood(itemStack))
                return null
            return itemStack.components
                .let {
                    it[DataComponents.FOOD]
                        ?.usingConvertsTo
                        ?: it[FoodItemProperties.type]
                            ?.convertsTo

                }?.getOrNull()
        }

        fun isHandHoldFood(itemStack: ItemStack, notCompound: Boolean = false): Boolean{
            return isFood(itemStack, notCompound) && containerOrNull(itemStack) == null
        }
    }
}