package indi.muxin.food_talks.common.item

import indi.muxin.food_talks.common.block.BottleBlockEntity
import indi.muxin.food_talks.common.mob_effect.applyEffects
import indi.muxin.food_talks.common.mob_effect.describeEffects
import net.minecraft.core.component.DataComponents
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.UseAnim
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.component.ItemLore
import java.util.*


object Cocktail: CompoundFood(
    0.25F,
    1,
    Items.GLASS_BOTTLE.defaultInstance,
    true
) {

    override fun getUseAnimation(stack: ItemStack) = UseAnim.DRINK

    override fun uponBite(itemStack: ItemStack, entity: LivingEntity): Boolean {
        val addonEffect = itemStack[DataComponents.POTION_CONTENTS] ?: return true
        if (!entity.level().isClientSide)
            entity.applyEffects(addonEffect.allEffects)
        return true
    }

    /**
     * final duration = base duration * (1 + extend / 3)
     */
    fun buildFromBottle(entity: BottleBlockEntity, fillLevel: Int): ItemStack{
        if (fillLevel == 0)
            return Items.GLASS_BOTTLE.defaultInstance

        var effectSequence = entity.contents.asSequence()
        if (entity.detoxified)
            effectSequence = effectSequence.filter { (effect, _)->
                effect.value().category != MobEffectCategory.HARMFUL
            }

        val sips = fillLevel * 4
        val effectPool = effectSequence
            .map {(effect, detail) ->
                val totalTime = detail.duration + detail.duration * entity.extend / 3
                val amplifier = detail.amplifier + entity.upgrade
                MobEffectInstance(effect, totalTime / sips, amplifier)
            }.toList()

        return  buildItemStack {
            set(DataComponents.MAX_DAMAGE, sips)
            set(DataComponents.POTION_CONTENTS, PotionContents(
                    Optional.empty(),
                    Optional.empty(),
                    effectPool))
            set(DataComponents.DAMAGE, 0)
            set(DataComponents.LORE, itemLoreFromMobEffectInstances(effectPool))
        }
    }

    private fun itemLoreFromMobEffectInstances(effects: List<MobEffectInstance>): ItemLore {
        return ItemLore(effects.describeEffects())
    }
}