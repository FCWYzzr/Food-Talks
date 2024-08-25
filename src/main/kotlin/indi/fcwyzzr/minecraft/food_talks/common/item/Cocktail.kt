package indi.fcwyzzr.minecraft.food_talks.common.item

import indi.fcwyzzr.minecraft.f_lib.utils.buildComponent
import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.api.common.item.CompoundFood
import indi.fcwyzzr.minecraft.food_talks.common.block.entity.BottleBlockEntity
import indi.fcwyzzr.minecraft.food_talks.common.mixin.accessors.mechanic.MobEffectInstanceAccessor
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
    0.25F,
    1,
    Items.GLASS_BOTTLE.defaultInstance,
    true
) {

    override fun getUseAnimation(stack: ItemStack) = UseAnim.DRINK

    fun mergePotionContent(addonEffect: Iterable<MobEffectInstance>?, entity: LivingEntity){
        val baseEffectMap = entity.activeEffectsMap
        val addonEffectMap = addonEffect
            ?.associate { it.effect to it }
            ?: return

        val effects = computedAddonMobEffect(addonEffectMap, baseEffectMap)

        removalWhenMergeMobEffectMap(addonEffectMap, baseEffectMap)
            .forEach(entity::removeEffect)


        effects.forEach {
            entity.addEffect(it, entity)
        }
    }

    override fun uponBite(itemStack: ItemStack, entity: LivingEntity): Boolean {
        val addonEffect = itemStack[DataComponents.POTION_CONTENTS]
        if (!entity.level().isClientSide)
            mergePotionContent(addonEffect ?.allEffects , entity)
        return true
    }

    fun buildFromBottle(entity: BottleBlockEntity, fillLevel: Int): ItemStack{
        if (fillLevel == 0)
            return Items.GLASS_BOTTLE.defaultInstance

        val amplifierAddon = entity.upgrade
        val timeMultiplier = entity.extend
        val harmFilter = entity.detoxified

        val potionContents = entity.dumpContents()
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
                val time = it.duration * (timeMultiplier + 1) / 16
                    val amplifier = it.amplifier + 1 + amplifierAddon
                    MobEffectInstance(it.effect, time, amplifier)
                }
            }
            .toList()

        DataComponents.ITEM_NAME

        return  buildItemStack {
            set(
                DataComponents.MAX_DAMAGE,
                fillLevel * 8
            )
            set(
                DataComponents.POTION_CONTENTS,
                PotionContents(
                    Optional.empty(),
                    Optional.empty(),
                    potionContents
                )
            )

            set(
                DataComponents.DAMAGE,
                0
            )

            set(
                DataComponents.LORE,
                itemLoreFromMobEffectInstances(potionContents)
            )
        }
    }


    fun mergeMobEffectInstance(addon: MobEffectInstance, base: MobEffectInstance?): MobEffectInstance {
        return when {
            base === null -> MobEffectInstance(addon)
            addon.amplifier > base.amplifier -> mergeMobEffectInstance(base, MobEffectInstance(addon))
            addon.amplifier == base.amplifier -> {
                (base as MobEffectInstanceAccessor)
                    .setDuration(base.duration + addon.duration)

                base
            }
            addon.amplifier < base.amplifier -> {
                (base as MobEffectInstanceAccessor)
                    .setDuration(base.duration + addon.duration * (base.amplifier + 1) / (addon.amplifier + 1))
                base
            }

            else -> throw Error("???")
        }
    }

    private fun mergeFoldedMobEffectInstance(self: MobEffectInstance): MobEffectInstance{
        val hidden = (self as MobEffectInstanceAccessor).hiddenEffect ?: return self

        val mergedHidden = mergeFoldedMobEffectInstance(MobEffectInstance(hidden))

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
            .mapNotNull(baseMap::get)
            .map(::mergeFoldedMobEffectInstance)
            .associateBy { it.effect }

        changesKey
            .mapNotNull(addonMap::get)
            .map { mergeMobEffectInstance(
                it, hiddenMergedRemoval[it.effect]
            )}
            .toCollection(this)

        addonMap
            .filterNot { it.key in changesKey }
            .map { MobEffectInstance(it.value) }
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
                    " §3<%d>§r".format(it.amplifier+1)
                )))
                append(buildComponent(LiteralContents(
                    " §9[%02d:%02d:%02d]§r".format(
                        time.toHours(),
                        time.toMinutesPart(),
                        time.toSecondsPart()
                    ))))
            }
        }.toList()

        return ItemLore(lore)
    }
}