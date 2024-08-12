package indi.fcwyzzr.minecraft.food_talks.common.mixin.tweaks.mechanic;

import indi.fcwyzzr.minecraft.food_talks.FoodTalks;
import indi.fcwyzzr.minecraft.food_talks.common.effects.beneficial.ProjectileImmune;
import indi.fcwyzzr.minecraft.food_talks.common.effects.beneficial.Smelly;
import indi.fcwyzzr.minecraft.food_talks.common.effects.harmful.Overweight;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    
    @Shadow public abstract boolean hasEffect(Holder<MobEffect> effect);
    
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    
    @Override
    @NotNull
    public ProjectileDeflection deflection(@NotNull Projectile projectile) {
        if (hasEffect(ProjectileImmune.INSTANCE.getHolder()))
            return (arrow, entity, random) -> {
                final var v = arrow.getDeltaMovement();
                arrow.setDeltaMovement(-v.x / 2, 0, -v.z / 2);
            };
        return super.deflection(projectile);
    }
    
    @SuppressWarnings("UnreachableCode")
    @Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
    public void setSprinting_mixin(boolean sprinting, CallbackInfo ci){
        final var self = (LivingEntity) (Object) this;
        if (!self.hasEffect(Overweight.INSTANCE.getHolder()))
            return;
        
        if (sprinting)
            ci.cancel();
    }
}
