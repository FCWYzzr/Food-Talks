package indi.fcwyzzr.minecraft.food_talks.common.item

import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.api.common.item.CompoundFood
import indi.fcwyzzr.minecraft.food_talks.client.renderer.Bewlr
import indi.fcwyzzr.minecraft.food_talks.common.block.entity.PlateBlockEntity
import indi.fcwyzzr.minecraft.food_talks.common.data_component.SimpleDataComponents
import indi.fcwyzzr.minecraft.food_talks.common.data_component.compound_food.FoodStackProperties
import indi.fcwyzzr.minecraft.food_talks.common.mixin.accessors.mechanic.MobEffectInstanceAccessor
import indi.fcwyzzr.minecraft.food_talks.common.registries.foodItemRewardRegistry
import indi.fcwyzzr.minecraft.food_talks.common.registries.foodTagPunishmentRegistry
import indi.fcwyzzr.minecraft.food_talks.toMobEffectInstanceSequence
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.util.Mth
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.food.FoodProperties.PossibleEffect
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import java.util.function.Consumer
import kotlin.jvm.optionals.getOrNull
import kotlin.math.exp
import kotlin.streams.asSequence

object Sandwich: CompoundFood(
    1F,
    256
) {

    fun layers(itemStack: ItemStack): List<Holder<Item>>{
        if (!itemStack.`is`(this))
            throw IllegalArgumentException("itemStack other than sandwich should not use this method")
        return itemStack.components[SimpleDataComponents.SandwichLayer]!!
    }

    override fun uponBite(itemStack: ItemStack, biteCount: Int, entity: LivingEntity): Boolean {
        if (entity !is Player)
            return true

        val food = itemStack.components[FoodStackProperties.type]
        val effect = itemStack.components[SimpleDataComponents.PossibleEffectList]

        // apply food property
        if (food != null)
            entity.foodData.eat(food.nutritionPerBite, food.saturationPerBite)




        return entity.canEat(false)
    }

    /**
     * note:
     * ingredients will be compiled, item infos stored for rendering only
     */
    fun assemblyFromPlate(entity: PlateBlockEntity): ItemStack = buildItemStack {
        set(SimpleDataComponents.SandwichLayer, entity.readOnlyIngredient.map(ItemStack::getItemHolder))

        // food & potion property
        val (nutrition, saturation, effectFolder) = entity
            .readOnlyIngredient
            .fold(IngredientFolder(), IngredientFolder::fold)
            .unpack()

        val bites = 8 * entity.size

        // food reward
        entity
            .readOnlyIngredient
            .asSequence()
            .map { BuiltInRegistries.ITEM.getResourceKey(it.item) }
            .mapNotNull { it.getOrNull() }
            .groupingBy {
                it.location()
            }
            .fold(0){i, _ -> i + 1}
            .asSequence()
            .mapNotNull { (location, count) ->
                val reward = foodItemRewardRegistry[location]
                    ?: return@mapNotNull null

                reward.effect.value() to (reward(count)to 1F)
            }.fold(effectFolder, EffectFolder::fold)

        val tagCounter = entity
            .readOnlyIngredient
            .asSequence()
            .flatMap { it.tags.asSequence() }
            .filter { it.location.namespace == FoodTalks.MOD_ID }
            .filter { it.location.path.startsWith("food_category") }
            .groupingBy { it }
            .fold(0){i, _ -> i + 1}

        val tagPunishmentThreshold = tagCounter.values
            .sorted().let {
                if (it.size <= 2)
                    return@let 0
                var counter = 0
                for (i in 1..< it.size - 1)
                    counter += it[i]
                return@let counter
            }

        val possibleEffects = tagCounter
            .asSequence()
            .filter { (_, v) -> v >= tagPunishmentThreshold * 2 }
            .map { (k, v) -> k to Mth.ceil(v.toFloat() / tagPunishmentThreshold)}
            .mapNotNull { (tag, level) ->
                val punishment = foodTagPunishmentRegistry[tag.location] ?: return@mapNotNull null
                punishment.effect.value() to (punishment(level) to 1F)
            }.fold(effectFolder, EffectFolder::fold)
            .pack(bites)
            .asSequence()
            .map { PossibleEffect({it.key}, it.value) }
            .toList()

        // final assembly

        val nutPerBite = Mth.ceil(nutrition.toFloat() / bites)
        val satPerBite = balanceSaturation(saturation)

        set(FoodStackProperties.type, FoodStackProperties(nutPerBite, satPerBite))
        set(SimpleDataComponents.PossibleEffectList, possibleEffects)
    }

    @Suppress("removal")
    @Deprecated("???")
    override fun initializeClient(consumer: Consumer<IClientItemExtensions>) {
        consumer.accept(object :IClientItemExtensions{
            override fun getCustomRenderer() = Bewlr
        })
    }

    /**
     * input: 0~30
     * output: 0 ~ 2
     * mapper: sigmoid
     */
    private fun balanceSaturation(old: Float): Float{
        val sigmoid = {v: Double -> 1 / (1 + exp(-v))}

        val input = old / 2.5 - 6
        val output = sigmoid(input)
        return (output * 2).toFloat()
    }

    private class EffectFolder {
        private val possibleEffect = mutableMapOf<
                MobEffect,
                Pair<MobEffectInstance, Float>
                >()

        fun pack(multiply: Int): Map<MobEffectInstance, Float> = possibleEffect
            .asSequence()
            .map { it.value.first to (it.value.second / multiply) }
            .toMap()

        fun fold(record: Pair<MobEffect, Pair<MobEffectInstance, Float>>): EffectFolder{
            val (effect, pair) = record
            val (instance, prob) = pair

            possibleEffect[effect] = when{
                effect !in possibleEffect.keys ->
                    pair

                else -> {
                    val (base, baseProb) = possibleEffect[effect]!!

                    mergeWeightedEffects(
                        base, baseProb,
                        instance, prob
                    )
                }
            }

            return this
        }

        companion object {
            fun mergeWeightedEffects(
                baseEffect: MobEffectInstance,
                baseProb: Float,
                addonEffect: MobEffectInstance,
                addonProb: Float
            ): Pair<MobEffectInstance, Float> {
                if (baseEffect.amplifier < addonEffect.amplifier)
                    return mergeWeightedEffects(
                        addonEffect, addonProb,
                        baseEffect, baseProb
                    )
                // convert levels:
                val effect = Cocktail.mergeMobEffectInstance(baseEffect, addonEffect)
                val prob = baseProb + addonProb * addonEffect.amplifier / addonEffect.amplifier

                return if (prob < 1)
                    effect to prob
                else {
                    (effect as MobEffectInstanceAccessor)
                        .setDuration((effect.duration * prob).toInt())
                    effect to 1F
                }
            }
        }
    }

    private class IngredientFolder {
        data class Result(
            val nutrition: Int,
            val saturation: Float,
            val effectFolder: EffectFolder
        )

        // food property
        private val foodSet = mutableMapOf<Item, Int>()
        private var nutrition = 0
        private var saturation = 0F
        private val effectFolder = EffectFolder()

        fun unpack() = Result(nutrition, saturation, effectFolder)

        fun fold(itemStack: ItemStack): IngredientFolder {
            val item = itemStack.item
            val food = itemStack.components[DataComponents.FOOD]!!
            val potionEffects = itemStack.components[DataComponents.POTION_CONTENTS]
                ?.toMobEffectInstanceSequence()
                ?.map{
                    it.effect.value() to (it to 1F)
                }
                ?: sequenceOf()

            val foodEffects = food.effects
                .asSequence()
                .map{ it.effectSupplier.get() to it.probability }
                .map { (effect, prob) -> effect.effect.value() to (effect to prob) }


            foodSet[item] = (foodSet[item] ?: 0) + 1
            val weight = 1F / foodSet[item]!!

            nutrition += food.nutrition
            saturation -= food.saturation * weight

            sequence{
                yieldAll(potionEffects)
                yieldAll(foodEffects)
            }.fold(effectFolder, EffectFolder::fold)

            return this
        }


    }
}