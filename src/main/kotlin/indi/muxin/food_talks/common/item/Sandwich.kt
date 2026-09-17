package indi.muxin.food_talks.common.item

import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.common.block.PlateBlockEntity
import indi.muxin.food_talks.common.data_components.FTDataComponents
import indi.muxin.food_talks.common.data_components.FoodStackProperties
import indi.muxin.food_talks.common.mob_effect.MobEffectDetail
import indi.muxin.food_talks.common.mob_effect.applyEffects
import indi.muxin.food_talks.common.registries.FoodItemReward
import indi.muxin.food_talks.common.registries.FoodTagPunishment
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.tags.TagKey
import net.minecraft.util.Mth
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.alchemy.PotionContents
import org.spongepowered.include.com.google.common.collect.Iterables
import java.util.*
import kotlin.jvm.optionals.getOrDefault
import kotlin.jvm.optionals.getOrNull
import kotlin.math.ceil

object Sandwich: CompoundFood(
    0.4F,
    1,
    ItemStack.EMPTY,
    true
) {
    const val NUT_PER_BITE = 8

    fun layers(itemStack: ItemStack): List<Holder<Item>>{
        if (!itemStack.`is`(this))
            throw IllegalArgumentException("itemStack other than sandwich should not use this method")
        return itemStack.components[FTDataComponents.SANDWICH_LAYERS] ?: listOf()
    }

    override fun uponBite(itemStack: ItemStack, entity: LivingEntity): Boolean {
        if (entity !is Player)
            return true

        if (! entity.level().isClientSide) {
            val possibleEffect = itemStack.components[FTDataComponents.POSSIBLE_EFFECTS] ?: listOf()
            val mustEffect = itemStack.components[DataComponents.POTION_CONTENTS]?.allEffects ?: listOf()

            val confirmedEffect = possibleEffect
                .stream()
                .map {
                    it to Mth.randomBetween(FoodTalks.random, 0F, 1F)
                }
                .filter {
                    it.first.probability <= it.second
                }
                .map { it.first.effect() }
                .iterator()

            val effect = Iterables.concat(
                Iterable { confirmedEffect }, mustEffect
            )

            // apply effects
            entity.applyEffects(effect)
        }

        val food = itemStack.components[FTDataComponents.FOOD_STACK_PROPERTIES]
        // apply food property
        if (food != null)
            entity.foodData.eat(food.nutritionPerBite, food.saturationPerBite)

        return true
    }

    /**
     * note:
     * ingredients will be compiled, item infos stored for rendering only
     */
    fun assemblyFromPlate(entity: PlateBlockEntity) = assemblyFromIngredientList(entity.ingredients)

    fun assemblyFromIngredientList(ingredients: List<ItemStack>) = buildList { buildItemStack {
        set(FTDataComponents.SANDWICH_LAYERS, ingredients.map(ItemStack::getItemHolder))

        var totalNut = 0
        var totalSat = 0F
        val effectPool = mutableMapOf<Holder<MobEffect>, MobEffectDetail>()

        val tagSample   = mutableMapOf<TagKey<Item>, Holder<Item>>()
        val tagCounter  = mutableMapOf<TagKey<Item>, Int>()
        val itemCounter = mutableMapOf<Holder<Item>, Int>()

        // basic food & potion property
        ingredients.forEach { ingredient ->
            ingredient.components[DataComponents.FOOD] ?.let {foodProp ->
                totalNut += foodProp.nutrition
                totalSat += foodProp.saturation * foodProp.nutrition
                if (!foodProp.usingConvertsTo.getOrDefault(ItemStack.EMPTY).isEmpty)
                    add(foodProp.usingConvertsTo.get())
                for (pEffect in foodProp.effects){
                    val effect = pEffect.effect()
                    val prob = pEffect.probability
                    effectPool[effect.effect] =
                        MobEffectDetail((effect.duration * prob).toInt(), effect.amplifier) merge effectPool[effect.effect]
                }
            }


            ingredient.components[DataComponents.POTION_CONTENTS] ?.let { potionContent ->
                potionContent.potion().getOrNull() ?.let { potion ->
                    for (effect in potion.value().effects)
                        effectPool[effect.effect] = MobEffectDetail(effect.duration, effect.amplifier) merge effectPool[effect.effect]
                }
                for (effect in potionContent.customEffects)
                    effectPool[effect.effect] = MobEffectDetail(effect.duration, effect.amplifier) merge effectPool[effect.effect]
            }

            ingredient.components[DataComponents.SUSPICIOUS_STEW_EFFECTS] ?.let { ssEffect ->
                for (effect in ssEffect.effects)
                    effectPool[effect.effect] = MobEffectDetail(effect.duration, 0) merge effectPool[effect.effect]
            }

            ingredient.components[DataComponents.OMINOUS_BOTTLE_AMPLIFIER] ?.let { amplifier ->
                effectPool[MobEffects.BAD_OMEN] = MobEffectDetail(100 * 60 * FoodTalks.TPS, amplifier) merge effectPool[MobEffects.BAD_OMEN]
            }

        }

        ingredients.subList(1, ingredients.size - 2).forEach { ingredient ->
            itemCounter[ingredient.itemHolder] = (itemCounter[ingredient.itemHolder] ?: 0) + 1
            ingredient.tags.filter {i ->
                i.location.namespace == FoodTalks.MOD_ID && i.location.path.startsWith("food_category")
            }.findAny().getOrNull() ?.let { tag ->
                if (tag in tagCounter)
                    tagCounter[tag] = tagCounter[tag]!! + 1
                else {
                    tagCounter[tag] = 1
                    tagSample[tag] = ingredient.itemHolder
                }
            }
        }

        itemCounter.forEach { (itemHolder, level) ->
            val reward = itemHolder.getData(FoodItemReward.type) ?: return@forEach
            effectPool[reward.effect] = reward.calculate(level) merge effectPool[reward.effect]
        }

        tagCounter.maxBy{ it.value }.let { (maximumTag, maximumCount) ->
            if (maximumCount <= 2)
                return@let
            val tagPunishmentThreshold1 = ingredients.size / 2 - 1
            val punishment = tagSample[maximumTag]!!.getData(FoodTagPunishment.type) ?: return@let

            val level = (maximumCount - tagPunishmentThreshold1) * 20.0F / ingredients.size

            effectPool[punishment.effect] = punishment.calculate(ceil(level).toInt()) merge effectPool[punishment.effect]
        }



        // final assembly

        val bites = (totalNut + NUT_PER_BITE - 1) / NUT_PER_BITE
        val sat = totalSat / totalNut
        val effects = effectPool.map { (effect, detail) ->
            MobEffectDetail.instate(effect, detail)
        }

        set(FTDataComponents.FOOD_STACK_PROPERTIES, FoodStackProperties(NUT_PER_BITE, sat))

        set(DataComponents.POTION_CONTENTS, PotionContents(
            Optional.empty(),
            Optional.empty(),
            effects
        ))
        set(DataComponents.MAX_DAMAGE, bites)
    }.let{add(it)}}

}