package indi.muxin.food_talks.common.mixin.mechanic

import net.minecraft.world.effect.MobEffectInstance
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

@Mixin(MobEffectInstance::class)
interface MobEffectInstanceAccessor : Comparable<MobEffectInstance> {
    @get:Accessor(value = "hiddenEffect")
    val hiddenEffect: MobEffectInstance?

    @Accessor(value = "duration")
    fun setDuration(value: Int)
}