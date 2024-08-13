package indi.fcwyzzr.minecraft.food_talks.common.mixin.accessors.mechanic;

import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobEffectInstance.class)
public interface MobEffectInstanceAccessor extends Comparable<MobEffectInstance> {
    
    @Accessor(value = "hiddenEffect")
    MobEffectInstance getHiddenEffect();
    
    @Accessor(value = "duration")
    void setDuration(int value);
}
