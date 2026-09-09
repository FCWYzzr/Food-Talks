package indi.muxin.food_talks.common.registries

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import indi.muxin.food_talks.toRegistryName
import indi.muxin.food_talks.toResourceLocation
import indi.muxin.neoforged.utils.buildDataMapType
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent

data class EffectFormula(
    val effect: Holder<MobEffect>,
    val durationBase: Int,
    val durationUnit: Int,
    val amplifierBase: Int,
    val amplifierUnit: Int) {
    fun calculate(level: Int) =
        MobEffectInstance(effect, durationBase + durationUnit * level, amplifierBase + amplifierUnit * level)

    companion object {
        val codec: Codec<EffectFormula> = RecordCodecBuilder.create{
            it.group(
                MobEffect.CODEC.fieldOf("effect").forGetter(EffectFormula::effect),
                Codec.INT.fieldOf("durationBase").forGetter(EffectFormula::durationBase),
                Codec.INT.fieldOf("durationUnit").forGetter(EffectFormula::durationUnit),
                Codec.INT.fieldOf("amplifierBase").forGetter(EffectFormula::amplifierBase),
                Codec.INT.fieldOf("amplifierUnit").forGetter(EffectFormula::amplifierUnit)
            ).apply(it, ::EffectFormula)
        }
    }
}

object FoodItemReward{
    val type = buildDataMapType(
        Registries.ITEM,
        "ItemReward".toRegistryName().toResourceLocation(),
        EffectFormula.codec
    )

    infix fun registerTo(dme: RegisterDataMapTypesEvent) {
        dme.register(type)
    }
}

object FoodTagPunishment{
    val type = buildDataMapType(
        Registries.ITEM,
        "TagPunishment".toRegistryName().toResourceLocation(),
        EffectFormula.codec
    )

    infix fun registerTo(dme: RegisterDataMapTypesEvent) {
        dme.register(type)
    }
}

