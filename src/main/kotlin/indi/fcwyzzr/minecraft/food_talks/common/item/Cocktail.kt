package indi.fcwyzzr.minecraft.food_talks.common.item

import indi.fcwyzzr.minecraft.f_lib.utils.buildComponent
import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.api.common.item.CompoundFood
import indi.fcwyzzr.minecraft.food_talks.common.block.entity.BottleBlockEntity
import indi.fcwyzzr.minecraft.food_talks.common.data_component.compound_food.FoodStackProperties
import indi.fcwyzzr.minecraft.food_talks.common.mixin.accessors.LivingEntityAccessor
import indi.fcwyzzr.minecraft.food_talks.common.mixin.accessors.MobEffectInstanceAccessor
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.contents.PlainTextContents.LiteralContents
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.UseAnim
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.component.ItemLore
import java.time.Duration
import java.util.*

object Cocktail: CompoundFood(
    16,
    1F,
    Items.GLASS_BOTTLE.defaultInstance,
    true
) {

     override fun getUseAnimation(stack: ItemStack) = UseAnim.DRINK

    override fun uponBite(itemStack: ItemStack, biteCount: Int, entity: LivingEntity): Boolean {
        val baseEffectMap = entity.activeEffectsMap
        val addonEffectMap = itemStack[DataComponents.POTION_CONTENTS]
            ?.allEffects
            ?.asSequence()
            ?.map { it.effect to it }
            ?.toMap()

            ?: return true

        removalWhenMergeMobEffectMap(addonEffectMap, baseEffectMap)
            .forEach {
                val mobEffectInstance: MobEffectInstance? = entity.removeEffectNoUpdate(it)
                (entity as LivingEntityAccessor).onEffectRemoved(mobEffectInstance)
            }

        computedAddonMobEffect(addonEffectMap, baseEffectMap)
            .forEach {
                entity.addEffect(it, entity)
            }

        return true
    }

    fun buildFromBottle(entity: BottleBlockEntity, fillLevel: Int): ItemStack{
        if (fillLevel == 0)
            return Items.GLASS_BOTTLE.defaultInstance

        val damage = (8 - fillLevel) * 2
        val amplifierAddon = entity.upgrade
        val timeMultiplier = entity.extend
        val harmFilter = entity.detoxified

        val potionContents = entity.dumpContents()
            .asSequence()
            .filter {
                if (harmFilter)
                    it.effect.value().category != MobEffectCategory.HARMFUL
                else
                    true
            }
            .map {
                if (timeMultiplier == 0 && amplifierAddon == 0)
                    it
                else {
                val time = it.duration * (timeMultiplier + 1) / 4
                    val amplifier = it.amplifier + amplifierAddon
                    MobEffectInstance(it.effect, time, amplifier)
                }
            }
            .toList()

        DataComponents.ITEM_NAME

        return  buildItemStack {
            set(
                DataComponents.POTION_CONTENTS,
                PotionContents(
                    Optional.empty(),
                    Optional.empty(),
                    potionContents
                )
            )
            set(
                FoodStackProperties.type,
                FoodStackProperties(
                    1, 0.5F
                )
            )

            set(
                DataComponents.DAMAGE,
                damage
            )

            set(
                DataComponents.LORE,
                itemLoreFromMobEffectInstances(potionContents)
            )
        }
    }


    fun mergeMobEffectInstance(addon: MobEffectInstance, base: MobEffectInstance?): MobEffectInstance {
        return when {
            base === null -> addon
            addon.amplifier > base.amplifier -> mergeMobEffectInstance(base, addon)
            addon.amplifier == base.amplifier -> MobEffectInstance(
                base.effect,
                base.duration + addon.duration,
                base.amplifier
            )
            addon.amplifier < base.amplifier -> MobEffectInstance(
                base.effect,
                base.duration + addon.duration * base.amplifier / addon.amplifier,
                base.amplifier
            )

            else -> throw Error("???")
        }
    }

    private fun mergeCollapsedMobEffectInstance(self: MobEffectInstance): MobEffectInstance{
        val hidden = (self as MobEffectInstanceAccessor).hiddenEffect ?: return self

        val mergedHidden = mergeCollapsedMobEffectInstance(hidden)

        return mergeMobEffectInstance(mergedHidden, self)
    }

    private fun removalWhenMergeMobEffectMap(
        addonMap: Map<Holder<MobEffect>, MobEffectInstance>,
        baseMap: Map<Holder<MobEffect>, MobEffectInstance>
    ): Set<Holder<MobEffect>>{
        return addonMap.keys.intersect(baseMap.keys)
    }

    private fun computedAddonMobEffect(
        addonMap: Map<Holder<MobEffect>, MobEffectInstance>,
        baseMap: Map<Holder<MobEffect>, MobEffectInstance>
    ) = buildList {
        val changesKey = removalWhenMergeMobEffectMap(addonMap, baseMap)
        val hiddenMergedRemoval = changesKey
            .asSequence()
            .map(baseMap::get)
            .filterNotNull()
            .map(::mergeCollapsedMobEffectInstance)
            .map { it.effect to it }
            .toMap()

        changesKey
            .asSequence()
            .map(addonMap::get)
            .filterNotNull()
            .map { mergeMobEffectInstance(
                it, hiddenMergedRemoval[it.effect]
            )}
            .toCollection(this)

        addonMap
            .asSequence()
            .filterNot { it.key in changesKey }
            .map(Map.Entry<Holder<MobEffect>, MobEffectInstance>::value)
            .toCollection(this)

    }

    private fun itemLoreFromMobEffectInstances(effects: List<MobEffectInstance>): ItemLore {

        val lore = effects.map {
            val time = Duration.ofSeconds(it.duration / FoodTalks.TPS.toLong())
            val key = it.effect.key!!
            buildComponent(TranslatableContents(
                key.location().toLanguageKey("effect"), null, arrayOf()
            )){
                style.withColor(0x87CEEB)
                style.withBold(true)


                append(buildComponent(LiteralContents(
                    "  §f=§r §f§o%d§r".format(it.amplifier+1)
                )))
                append(buildComponent(LiteralContents(
                    "  §f+§r §f%02d:%02d:%02d§r".format(
                        time.toHours(),
                        time.toMinutesPart(),
                        time.toSecondsPart()
                    ))))
            }
        }.toList()

        return ItemLore(lore)
    }
}