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
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.tags.TagKey
import net.minecraft.util.Mth
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.food.FoodProperties.PossibleEffect
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionContents
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import org.spongepowered.include.com.google.common.collect.Iterables
import java.util.*
import java.util.function.Consumer
import kotlin.jvm.optionals.getOrNull
import kotlin.math.min

object Sandwich: CompoundFood(
    0.5F,
    1,
    null,
    true
) {

    fun layers(itemStack: ItemStack): List<Holder<Item>>{
        if (!itemStack.`is`(this))
            throw IllegalArgumentException("itemStack other than sandwich should not use this method")
        return itemStack.components[SimpleDataComponents.SandwichLayer]!!
    }

    override fun uponBite(itemStack: ItemStack, entity: LivingEntity): Boolean {
        if (entity !is Player)
            return true

        if (! entity.level().isClientSide) {
            val possibleEffect = itemStack.components[SimpleDataComponents.PossibleEffectList] ?: listOf()
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
            Cocktail.mergePotionContent(effect, entity)
        }

        val food = itemStack.components[FoodStackProperties.type]
        // apply food property
        if (food != null)
            entity.foodData.eat(food.nutritionPerBite, food.saturationPerBite)

        return true
    }

    /**
     * note:
     * ingredients will be compiled, item infos stored for rendering only
     */
    fun assemblyFromPlate(entity: PlateBlockEntity): ItemStack = buildItemStack {
        set(SimpleDataComponents.SandwichLayer, entity.readOnlyIngredient.map(ItemStack::getItemHolder))

        // optimize for client
        if (entity.level ?.isClientSide != false)
            return@buildItemStack

        // food & potion property
        val (bites, nutrition, saturation, effectFolder) = entity
            .readOnlyIngredient
            .fold(IngredientFolder(), IngredientFolder::fold)
            .unpack()

        // food reward
        val foodCounter = entity
            .readOnlyIngredient
            .asSequence()
            .map { mutableMapOf(it.item to 1)  }
            .reduce {m1, m2 ->
                val intersect = m1.keys intersect m2.keys

                intersect.forEach{k ->
                    m1[k] = m1[k]!! + m2[k]!!
                }

                m2.filterNot {(k, _) ->
                    k in intersect
                }.forEach(m1::put)

                m1
            }


        foodCounter[Items.BREAD] = foodCounter[Items.BREAD]!! - 1

        foodCounter
            .map { (item, count) ->
                BuiltInRegistries.ITEM.getResourceKey(item) to count
            }
            .mapNotNull { (key, count) ->
                key.getOrNull() ?.location() ?.to(count)
            }
            .mapNotNull { (location, count) ->

                val reward = foodItemRewardRegistry[location]
                    ?: return@mapNotNull null

                reward.effect.value() to (reward(count)to 1F)
            }
            .fold(effectFolder, EffectFolder::fold)





        val tagCounter = entity
            .readOnlyIngredient
            .asSequence()
            .flatMap { it.tags.toList() }
            .filter { it.location.namespace == FoodTalks.MOD_ID }
            .filter { it.location.path.startsWith("food_category") }
            .groupingBy { it }
            .fold(0){i, _ -> i + 1}
            .toMutableMap()

        val key = TagKey.create(
            BuiltInRegistries.ITEM.key(),
            "food_category/staples".toResourceLocation()
        )

        tagCounter[key] = tagCounter[key]!! - 1

        val tagPunishmentThreshold = tagCounter.values
            .sorted().let {
                if (it.size <= 2)
                    return@let 1
                var counter = 0
                for (i in 1..< it.size - 1)
                    counter += it[i]
                return@let counter
            }

        val (possibleEffects, mustEffect) = tagCounter
            .filter { (_, v) -> v >= tagPunishmentThreshold * 1.5 }
            .map { (k, v) ->
                k to v.toFloat() / tagPunishmentThreshold
            }
            .mapNotNull { (tag, level) ->
                val punishment = foodTagPunishmentRegistry[tag.location] ?: return@mapNotNull null
                punishment.effect.value() to (punishment(level) to 1F)
            }.fold(effectFolder, EffectFolder::fold)
            .pack(bites)


        // final assembly

        val nutPerBite = Mth.ceil(nutrition.toFloat() * 3 / bites)
        val satPerBite = saturation / entity.size

        set(FoodStackProperties.type, FoodStackProperties(nutPerBite, satPerBite))
        set(SimpleDataComponents.PossibleEffectList, possibleEffects.map {
            PossibleEffect({it.key}, it.value)
        })

        set(DataComponents.POTION_CONTENTS, PotionContents(
            Optional.empty(),
            Optional.empty(),
            mustEffect
        ))
        set(DataComponents.MAX_DAMAGE, bites)
    }

    @Suppress("removal")
    @Deprecated("???")
    override fun initializeClient(consumer: Consumer<IClientItemExtensions>) {
        consumer.accept(object :IClientItemExtensions{
            override fun getCustomRenderer() = Bewlr
        })
    }

    private class EffectFolder {
        private val possibleEffect = mutableMapOf<
                MobEffect,
                Pair<MobEffectInstance, Float>
                >()

        fun pack(multiply: Int): Pair<Map<MobEffectInstance, Float>, List<MobEffectInstance>> {
            val list = mutableListOf<MobEffectInstance>()
            val map = mutableMapOf<MobEffectInstance, Float>()

            possibleEffect.values
                .forEach { (effect, prob) ->
                if (prob >= 0.997) {
                    (effect as MobEffectInstanceAccessor)
                        .setDuration(effect.duration * 3 / multiply)
                    list.add(effect)
                }
                else
                    map[effect] = prob / multiply
            }
            return map to list
        }

        fun fold(record: Pair<MobEffect, Pair<MobEffectInstance, Float>>): EffectFolder{
            val (effect, pair) = record
            val (addon, prob) = pair

            possibleEffect[effect] = when{
                effect !in possibleEffect.keys ->
                    MobEffectInstance(addon) to prob

                else -> {
                    val (base, baseProb) = possibleEffect[effect]!!

                    mergeWeightedEffects(
                        base, baseProb,
                        addon, prob
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
                val effect = Cocktail.mergeMobEffectInstance(addonEffect, baseEffect)
                val prob = baseProb + addonProb * (addonEffect.amplifier + 1) / (addonEffect.amplifier + 1)

                return if (prob < 1F)
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
            val bites: Int,
            val nutrition: Int,
            val saturation: Float,
            val effectFolder: EffectFolder
        )

        // food property
        private var bites = 0
        private val foodSet = mutableMapOf<Item, Int>()
        private var nutrition = 0
        private var saturation = 0F
        private val effectFolder = EffectFolder()

        fun unpack() = Result(bites, nutrition, saturation, effectFolder)

        fun fold(itemStack: ItemStack): IngredientFolder {
            val item = itemStack.item
            val food = itemStack.components[DataComponents.FOOD]!!

            bites += min(6, food.nutrition)

            val potionEffects = itemStack.components[DataComponents.POTION_CONTENTS]
                ?.allEffects
                ?.map{
                    it.effect.value() to (it to 1F)
                }
                ?: listOf()

            val foodEffects = food.effects
                .asIterable()
                .map{ it.effectSupplier.get() to it.probability }
                .map { (effect, prob) -> effect.effect.value() to (effect to prob) }


            foodSet[item] = (foodSet[item] ?: 0) + 1
            val weight = 1F / foodSet[item]!!

            nutrition += food.nutrition
            saturation += food.saturation * weight


            potionEffects
                .fold(effectFolder, EffectFolder::fold)
            foodEffects
                .fold(effectFolder, EffectFolder::fold)

            return this
        }


    }
}