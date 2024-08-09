package indi.fcwyzzr.minecraft.food_talks.common.mixin.accessors;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor{
    @Invoker(value = "onEffectRemoved", remap = false)
    void onEffectRemoved(MobEffectInstance pEffectInstance);
}