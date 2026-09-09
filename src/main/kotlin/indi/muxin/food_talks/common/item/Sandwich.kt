package indi.muxin.food_talks.common.item

import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.common.block.PlateBlockEntity
import indi.muxin.food_talks.common.data_components.FoodStackProperties
import indi.muxin.food_talks.common.data_components.FoodStackPropertiesDCType
import indi.muxin.food_talks.common.data_components.PossibleEffectListDCType
import indi.muxin.food_talks.common.data_components.SandwichLayerDCType
import indi.muxin.food_talks.common.mixin.mechanic.MobEffectInstanceAccessor
import indi.muxin.food_talks.common.registries.FoodItemReward
import indi.muxin.food_talks.common.registries.FoodTagPunishment
import indi.muxin.food_talks.toResourceLocation
import indi.muxin.neoforged.utils.buildComponent
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.util.Mth
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.food.FoodProperties.PossibleEffect
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.component.ItemLore
import org.spongepowered.include.com.google.common.collect.Iterables
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

object Sandwich: CompoundFood(
    0.5F,
    1,
    null,
    true
) {

    fun layers(itemStack: ItemStack): List<Holder<Item>>{
        if (!itemStack.`is`(this))
            throw IllegalArgumentException("itemStack other than sandwich should not use this method")
        return itemStack.components[SandwichLayerDCType] ?: listOf()
    }

    override fun uponBite(itemStack: ItemStack, entity: LivingEntity): Boolean {
        if (entity !is Player)
            return true

        if (! entity.level().isClientSide) {
            val possibleEffect = itemStack.components[PossibleEffectListDCType] ?: listOf()
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

        val food = itemStack.components[FoodStackPropertiesDCType]
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
        // optimize for client
        if (entity.level ?.isClientSide != false)
            return@buildItemStack

        set(SandwichLayerDCType, entity.ingredients.map(ItemStack::getItemHolder))

        // food & potion property
        val (bites, nutrition, saturation, effectFolder) = entity
            .ingredients
            .fold(IngredientFolder(), IngredientFolder::fold)
            .unpack()

        entity
            .ingredients
            .asSequence()
            .drop(1)
            .map { mapOf(it.itemHolder to 1)  }
            .reduce {m1, m2 -> buildMap {(m1.keys union m2.keys).forEach {
                put(it, (m1[it] ?: 0) + (m2[it] ?: 0))
            }}}
            .mapNotNull { (holder, count) ->
                val reward = holder.getData(FoodItemReward.type) ?: return@mapNotNull null
                reward.calculate(count) to 1F
            }
            .fold(effectFolder, EffectFolder::fold)

        val tagCounter = entity
            .ingredients
            .drop(1)
            .dropLast(1)
            .asSequence()
            .mapNotNull {
                it.tags.filter {i ->
                    i.location.namespace == FoodTalks.MOD_ID && i.location.path.startsWith("food_category")
                }.findAny().getOrNull()
            }
            .groupingBy { it }
            .fold(0){i, _ -> i + 1}
            .toMap()

        val tagPunishmentThreshold1 = entity.ingredients.size / 2        // 50%, 0
//        val tagPunishmentThreshold2 = entity.ingredients.size * 3 / 4    // 100%, 5

        val (possibleEffects, mustEffects) = tagCounter
            .filter { (_, v) -> v >= max(tagPunishmentThreshold1, 2) }
            .mapNotNull { (tag, v) ->
                val punishment = BuiltInRegistries.ITEM.getTag(tag).get()
                    .first()
                    .getData(FoodTagPunishment.type) ?: return@mapNotNull null

                val prob = (v - tagPunishmentThreshold1) * 2.0F / entity.ingredients.size  + 0.5F
                val level = (v - tagPunishmentThreshold1) * 20.0F / entity.ingredients.size
                punishment.calculate(ceil(level).toInt()) to prob
            }.fold(effectFolder, EffectFolder::fold)
            .pack(bites)


        // final assembly

        val nutPerBite = Mth.ceil(nutrition.toFloat() * 3 / bites)
        val satPerBite = saturation / entity.ingredients.size

        set(FoodStackPropertiesDCType, FoodStackProperties(nutPerBite, satPerBite))
        set(PossibleEffectListDCType, possibleEffects.map {
            PossibleEffect({it.key}, it.value)
        })

        set(DataComponents.POTION_CONTENTS, PotionContents(
            Optional.empty(),
            Optional.empty(),
            mustEffects
        ))
        set(DataComponents.MAX_DAMAGE, bites)
        set(DataComponents.LORE, itemLoreFromDetail(
            nutPerBite, satPerBite, bites,
            possibleEffects, mustEffects, entity.ingredients))
    }

    private fun itemLoreFromDetail(
        nutPerBite: Int, satPerBite: Float, bites: Int,
        possibleEffects: Map<MobEffectInstance, Float>,
        mustEffects: List<MobEffectInstance>, ingredients: List<ItemStack>): ItemLore {
        return ItemLore(buildList<Component> {
            add(buildComponent(TranslatableContents(
                "nut_sat_bites".toResourceLocation().toLanguageKey("lore"), null,
                arrayOf("$nutPerBite", "%.2f".format(satPerBite * 100), "$bites",
                    "${nutPerBite * bites}", "${round(nutPerBite * bites * satPerBite)}")
            )))
            addAll(mustEffects.describeEffects())
            addAll(possibleEffects.describeEffects())
            ingredients.forEach {
                add(it.displayName)
            }
        })
    }

    private class EffectFolder {
        private val possibleEffect = mutableMapOf<
                Holder<MobEffect>,
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

        fun fold(record: Pair<MobEffectInstance, Float>): EffectFolder{
            val (addon, prob) = record

            possibleEffect[addon.effect] =
                if (addon.effect !in possibleEffect.keys)
                    MobEffectInstance(addon) to prob
                else {
                    val (base, baseProb) = possibleEffect[addon.effect]!!

                    mergeWeightedEffects(
                        base, baseProb,
                        addon, prob)
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

            bites += min(4, food.nutrition)

            val potionEffects = itemStack.components[DataComponents.POTION_CONTENTS]
                ?.allEffects
                ?.map{
                    it to 1F
                }
                ?: listOf()

            val foodEffects = food.effects
                .asIterable()
                .map{ it.effectSupplier.get() to it.probability }


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