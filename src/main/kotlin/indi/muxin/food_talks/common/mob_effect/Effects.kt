package indi.muxin.food_talks.common.mob_effect

import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.common.mixin.mechanic.MobEffectInstanceAccessor
import indi.muxin.neoforged.utils.buildComponent
import indi.muxin.neoforged.utils.buildMutableComponent
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.contents.PlainTextContents.LiteralContents
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import java.time.Duration
import kotlin.math.pow


infix fun MobEffectInstance.merge(other: MobEffectInstance?): MobEffectInstance {
    if (other == null)
        return this
    assert(effect == other.effect)
    if (amplifier < other.amplifier)
        return other.merge(this)
    return MobEffectInstance(effect, duration + other.duration ushr (amplifier - other.amplifier), amplifier)
}

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
    val effect = baseEffect merge addonEffect
    val prob = baseProb + addonProb / (2.0F).pow(baseEffect.amplifier - addonEffect.amplifier)

    return if (prob < 1F)
        effect to prob
    else {
        (effect as MobEffectInstanceAccessor)
            .setDuration((effect.duration * prob).toInt())
        effect to 1F
    }
}

/**
 * make hiddenEffect no longer exists
 * convert lower level effect's duration directly to higher level effect
 */
fun MobEffectInstance.mergeFolded(): MobEffectInstance {
    val hidden = (this as MobEffectInstanceAccessor).hiddenEffect ?: return this
    return merge(hidden.mergeFolded())
}

fun LivingEntity.applyEffects(addonEffects: Iterable<MobEffectInstance>?) {
    if (addonEffects == null)
        return
    for (effect in addonEffects) {
        val oldEffect = activeEffectsMap[effect.effect]
        if (oldEffect != null)
            removeEffect(effect.effect)
        addEffect(effect merge oldEffect)
    }
}

fun MobEffectInstance.describe(): MutableComponent {
    val time = Duration.ofSeconds(duration / FoodTalks.TPS.toLong())
    val key = effect.key!!
    return buildMutableComponent(
        TranslatableContents(
            key.location().toLanguageKey("effect"), null, arrayOf()
        )
    ) {
        style.withColor(0x87CEEB)
        style.withBold(true)


        append(
            buildComponent(
                LiteralContents(
                    " §3<%d>§r".format(amplifier + 1)
                )
            )
        )
        append(
            buildComponent(
                LiteralContents(
                    when {
                        time.toHours() > 0 -> " §9[%02d:%02d:%02d]§r".format(
                            time.toHours(),
                            time.toMinutesPart(),
                            time.toSecondsPart()
                        )

                        time.toMinutes() > 0 -> " §9[%02d:%02d]§r".format(
                            time.toMinutes(),
                            time.toSecondsPart()
                        )

                        else -> " §9%02ds§r".format(
                            time.toSeconds()
                        )
                    }
                )
            )
        )
    }
}
fun List<MobEffectInstance>.describeEffects() = map {it.describe()}.toList()

fun Map<MobEffectInstance, Float>.describeEffects() = map {(it, possible) ->
    it.describe().apply {
        append(buildComponent(LiteralContents(
            " §5%.2f%%§r".format(
                possible * 100
            ))))
    }
}.toList()