@file:Suppress("unused")

package indi.muxin.food_talks.common.mob_effect

import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.common.mixin.MobEffectInstanceAccessor
import indi.muxin.food_talks.common.mob_effect.Anorexia.addAttributeModifier
import indi.muxin.food_talks.toResourceLocation
import indi.muxin.neoforged.utils.buildComponent
import indi.muxin.neoforged.utils.buildMutableComponent
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.contents.PlainTextContents
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.alchemy.Potion
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.RegisterEvent
import java.time.Duration

object FTMobEffects {
    @JvmField val HAPPY             : MobEffect = Happy
    @JvmField val DARKNESS_INFUSED  : MobEffect = object: MobEffect(MobEffectCategory.BENEFICIAL, 0x000000){}
    @JvmField val POISON_RESISTANCE : MobEffect = PoisonResistance
    @JvmField val TREASURE          : MobEffect = object: MobEffect(MobEffectCategory.BENEFICIAL, 0xffff00){}
    @JvmField val ENDLESS_TREASURE  : MobEffect = object: MobEffect(MobEffectCategory.BENEFICIAL, 0xffff00){}
    @JvmField val SMELLY            : MobEffect = object: MobEffect(MobEffectCategory.BENEFICIAL, 0xffffff){}
    @JvmField val PROJECTILE_IMMUNE : MobEffect = object: MobEffect(MobEffectCategory.BENEFICIAL, 0x993333){}
    @JvmField val STARVING          : MobEffect = object: MobEffect(MobEffectCategory.BENEFICIAL, 0xa42312){}
    @JvmField val SCAPEGOAT         : MobEffect = object: MobEffect(MobEffectCategory.BENEFICIAL, 0x000000){}


    @JvmField val ANOREXIA          : MobEffect = Anorexia
    @JvmField val GOUT              : MobEffect = Gout
    @JvmField val VOMIT             : MobEffect = Vomit
    @JvmField val TOOTHACHE         : MobEffect = object: MobEffect(MobEffectCategory.HARMFUL, 0xffffff) {}
    @JvmField val OVERWEIGHT        : MobEffect = object: MobEffect(MobEffectCategory.HARMFUL, 0xffffff) {}
}

object FTMobEffectHolders {
    private fun toHolder(name: String) = DeferredHolder
        .create(Registries.MOB_EFFECT, name.toResourceLocation())


    @JvmField val HAPPY             : Holder<MobEffect> = toHolder("Happy")
    @JvmField val DARKNESS_INFUSED  : Holder<MobEffect> = toHolder("DarknessInfused").apply {
        val location = key!!.location()
        addAttributeModifier(
            Attributes.MOVEMENT_SPEED,
            location,
            0.5,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        )
        addAttributeModifier(
            Attributes.ATTACK_SPEED,
            location,
            1.0,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        )
        addAttributeModifier(
            Attributes.ATTACK_DAMAGE,
            location,
            0.5,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        )
        addAttributeModifier(
            Attributes.KNOCKBACK_RESISTANCE,
            location,
            5.0,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        )
        addAttributeModifier(
            Attributes.LUCK,
            location,
            1.0,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        )
    }
    @JvmField val POISON_RESISTANCE : Holder<MobEffect> = toHolder("PoisonResistance")
    @JvmField val TREASURE          : Holder<MobEffect> = toHolder("Treasure")
    @JvmField val ENDLESS_TREASURE  : Holder<MobEffect> = toHolder("EndlessTreasure")
    @JvmField val SMELLY            : Holder<MobEffect> = toHolder("Smelly")
    @JvmField val PROJECTILE_IMMUNE : Holder<MobEffect> = toHolder("ProjectileImmune")
    @JvmField val STARVING          : Holder<MobEffect> = toHolder("Starving")
    @JvmField val SCAPEGOAT         : Holder<MobEffect> = toHolder("Scapegoat")

    @JvmField val ANOREXIA          : Holder<MobEffect> = toHolder("Anorexia")
    @JvmField val GOUT              : Holder<MobEffect> = toHolder("Gout")
    @JvmField val VOMIT             : Holder<MobEffect> = toHolder("Vomit")
    @JvmField val TOOTHACHE         : Holder<MobEffect> = toHolder("Toothache")
    @JvmField val OVERWEIGHT        : Holder<MobEffect> = toHolder("Overweight").apply {
        addAttributeModifier(
            Attributes.MOVEMENT_SPEED,
            key!!.location(),
            -0.1,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        )
    }
}

object FTPotionHolders {
    private fun toHolder(name: String) = DeferredHolder
        .create(Registries.POTION, name.toResourceLocation())


    @JvmField val HAPPY             : Holder<Potion> = toHolder("Happy")
    @JvmField val DARKNESS_INFUSED  : Holder<Potion> = toHolder("DarknessInfused")
    @JvmField val POISON_RESISTANCE : Holder<Potion> = toHolder("PoisonResistance")
    @JvmField val TREASURE          : Holder<Potion> = toHolder("Treasure")
    @JvmField val ENDLESS_TREASURE  : Holder<Potion> = toHolder("EndlessTreasure")
    @JvmField val SMELLY            : Holder<Potion> = toHolder("Smelly")
    @JvmField val PROJECTILE_IMMUNE : Holder<Potion> = toHolder("ProjectileImmune")
    @JvmField val STARVING          : Holder<Potion> = toHolder("Starving")
    @JvmField val SCAPEGOAT         : Holder<Potion> = toHolder("Scapegoat")

    @JvmField val ANOREXIA          : Holder<Potion> = toHolder("Anorexia")
    @JvmField val GOUT              : Holder<Potion> = toHolder("Gout")
    @JvmField val VOMIT             : Holder<Potion> = toHolder("Vomit")
    @JvmField val TOOTHACHE         : Holder<Potion> = toHolder("Toothache")
    @JvmField val OVERWEIGHT        : Holder<Potion> = toHolder("Overweight")
}


fun registerFTMobEffects(ev: RegisterEvent){
    ev.register(Registries.MOB_EFFECT){
        it.register(FTMobEffectHolders.HAPPY            .key!!.location(), FTMobEffects.HAPPY            )
        it.register(FTMobEffectHolders.DARKNESS_INFUSED .key!!.location(), FTMobEffects.DARKNESS_INFUSED )
        it.register(FTMobEffectHolders.POISON_RESISTANCE.key!!.location(), FTMobEffects.POISON_RESISTANCE)
        it.register(FTMobEffectHolders.TREASURE         .key!!.location(), FTMobEffects.TREASURE         )
        it.register(FTMobEffectHolders.ENDLESS_TREASURE .key!!.location(), FTMobEffects.ENDLESS_TREASURE )
        it.register(FTMobEffectHolders.SMELLY           .key!!.location(), FTMobEffects.SMELLY           )
        it.register(FTMobEffectHolders.PROJECTILE_IMMUNE.key!!.location(), FTMobEffects.PROJECTILE_IMMUNE)
        it.register(FTMobEffectHolders.STARVING         .key!!.location(), FTMobEffects.STARVING         )
        it.register(FTMobEffectHolders.SCAPEGOAT        .key!!.location(), FTMobEffects.SCAPEGOAT        )
        it.register(FTMobEffectHolders.ANOREXIA         .key!!.location(), FTMobEffects.ANOREXIA         )
        it.register(FTMobEffectHolders.GOUT             .key!!.location(), FTMobEffects.GOUT             )
        it.register(FTMobEffectHolders.VOMIT            .key!!.location(), FTMobEffects.VOMIT            )
        it.register(FTMobEffectHolders.TOOTHACHE        .key!!.location(), FTMobEffects.TOOTHACHE        )
        it.register(FTMobEffectHolders.OVERWEIGHT       .key!!.location(), FTMobEffects.OVERWEIGHT       )
    }

    ev.register(Registries.POTION){
        it.register(FTPotionHolders.HAPPY            .key!!.location(), Potion(MobEffectInstance(FTMobEffectHolders.HAPPY            , 30 * FoodTalks.TPS, 0)))
        it.register(FTPotionHolders.DARKNESS_INFUSED .key!!.location(), Potion(MobEffectInstance(FTMobEffectHolders.DARKNESS_INFUSED , 30 * FoodTalks.TPS, 0)))
        it.register(FTPotionHolders.POISON_RESISTANCE.key!!.location(), Potion(MobEffectInstance(FTMobEffectHolders.POISON_RESISTANCE, 30 * FoodTalks.TPS, 0)))
        it.register(FTPotionHolders.TREASURE         .key!!.location(), Potion(MobEffectInstance(FTMobEffectHolders.TREASURE         , 30 * FoodTalks.TPS, 0)))
        it.register(FTPotionHolders.ENDLESS_TREASURE .key!!.location(), Potion(MobEffectInstance(FTMobEffectHolders.ENDLESS_TREASURE , 30 * FoodTalks.TPS, 0)))
        it.register(FTPotionHolders.SMELLY           .key!!.location(), Potion(MobEffectInstance(FTMobEffectHolders.SMELLY           , 30 * FoodTalks.TPS, 0)))
        it.register(FTPotionHolders.PROJECTILE_IMMUNE.key!!.location(), Potion(MobEffectInstance(FTMobEffectHolders.PROJECTILE_IMMUNE, 30 * FoodTalks.TPS, 0)))
        it.register(FTPotionHolders.STARVING         .key!!.location(), Potion(MobEffectInstance(FTMobEffectHolders.STARVING         , 30 * FoodTalks.TPS, 0)))
        it.register(FTPotionHolders.SCAPEGOAT        .key!!.location(), Potion(MobEffectInstance(FTMobEffectHolders.SCAPEGOAT        , 30 * FoodTalks.TPS, 0)))
        it.register(FTPotionHolders.ANOREXIA         .key!!.location(), Potion(MobEffectInstance(FTMobEffectHolders.ANOREXIA         , 30 * FoodTalks.TPS, 0)))
        it.register(FTPotionHolders.GOUT             .key!!.location(), Potion(MobEffectInstance(FTMobEffectHolders.GOUT             , 30 * FoodTalks.TPS, 0)))
        it.register(FTPotionHolders.VOMIT            .key!!.location(), Potion(MobEffectInstance(FTMobEffectHolders.VOMIT            , 30 * FoodTalks.TPS, 0)))
        it.register(FTPotionHolders.TOOTHACHE        .key!!.location(), Potion(MobEffectInstance(FTMobEffectHolders.TOOTHACHE        , 30 * FoodTalks.TPS, 0)))
        it.register(FTPotionHolders.OVERWEIGHT       .key!!.location(), Potion(MobEffectInstance(FTMobEffectHolders.OVERWEIGHT       , 30 * FoodTalks.TPS, 0)))
    }
}


infix fun MobEffectInstance.merge(other: MobEffectInstance?): MobEffectInstance {
    if (other == null)
        return this
    assert(effect == other.effect)
    if (amplifier < other.amplifier)
        return other.merge(this)
    return MobEffectInstance(effect, duration + other.duration ushr (amplifier - other.amplifier), amplifier)
}

data class MobEffectDetail(
    var duration: Int,
    var amplifier: Int
){
    constructor(mei: MobEffectInstance): this(mei.duration, mei.amplifier)


    infix fun merge(other: MobEffectDetail?): MobEffectDetail {
        if (other == null)
            return this
        if (amplifier < other.amplifier)
            return other.merge(this)

        return MobEffectDetail(duration + other.duration ushr (amplifier - other.amplifier), amplifier)
    }

    companion object {
        fun instate(effect: Holder<MobEffect>, detail: MobEffectDetail) = MobEffectInstance(effect, detail.duration, detail.amplifier, false, false, false)

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
        addEffect(effect merge oldEffect ?.mergeFolded())
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
                PlainTextContents.LiteralContents(
                    " §3<%d>§r".format(amplifier + 1)
                )
            )
        )
        append(
            buildComponent(
                PlainTextContents.LiteralContents(
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
        append(buildComponent(
            PlainTextContents.LiteralContents(
                " §5%.2f%%§r".format(
                    possible * 100
                )
            )
        ))
    }
}.toList()