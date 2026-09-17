package indi.muxin.food_talks.common.item

import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.common.data_components.FTDataComponents
import indi.muxin.food_talks.common.data_components.FoodItemProperties
import indi.muxin.food_talks.common.mob_effect.FTMobEffectHolders
import indi.muxin.neoforged.utils.buildDataComponentPatch
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level
import kotlin.jvm.optionals.getOrNull

abstract class CompoundFood(
    chewSeconds: Float,
    bitesNeed: Int = 1,
    useConvertsTo: ItemStack = ItemStack.EMPTY,
    canAlwaysEat: Boolean = false
): Item(Properties().apply {
    durability(bitesNeed)
    setNoRepair()
    component(FTDataComponents.FOOD_ITEM_PROPERTIES, FoodItemProperties(
        canAlwaysEat, useConvertsTo, (FoodTalks.TPS * chewSeconds).toInt()
    ))
}) {
    fun chewTick(stack: ItemStack) = stack.components[FTDataComponents.FOOD_ITEM_PROPERTIES]!!.chewTick
    override fun getUseAnimation(stack: ItemStack) = UseAnim.EAT

    override fun getUseDuration(stack: ItemStack, entity: LivingEntity) = 5 + chewTick(stack)
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(usedHand)
        if (player.hasEffect(FTMobEffectHolders.VOMIT) || player.hasEffect(FTMobEffectHolders.ANOREXIA))
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
        return stack.components[FTDataComponents.FOOD_ITEM_PROPERTIES]!!.convertsTo
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

        fun containerOrEmpty(itemStack: ItemStack): ItemStack {
            if (!isFood(itemStack))
                return ItemStack.EMPTY
            return itemStack.components
                .let {
                    it[DataComponents.FOOD]
                        ?.usingConvertsTo ?.getOrNull()
                        ?: it[FTDataComponents.FOOD_ITEM_PROPERTIES]
                            ?.convertsTo
                            ?: ItemStack.EMPTY
                }
        }

        fun isHandHoldFood(itemStack: ItemStack, notCompound: Boolean = false): Boolean{
            return isFood(itemStack, notCompound) && containerOrEmpty(itemStack).isEmpty
        }
    }
}