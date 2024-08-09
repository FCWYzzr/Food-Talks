package indi.fcwyzzr.minecraft.food_talks.common.mixin.accessors;

import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobEffectInstance.class)
public interface MobEffectInstanceAccessor extends Comparable<MobEffectInstance> {
    
    @Accessor(value = "hiddenEffect", remap = false)
    MobEffectInstance getHiddenEffect();
}
