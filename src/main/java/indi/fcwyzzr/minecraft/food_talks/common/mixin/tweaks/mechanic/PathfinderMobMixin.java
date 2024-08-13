package indi.fcwyzzr.minecraft.food_talks.common.mixin.tweaks.mechanic;

import indi.fcwyzzr.minecraft.food_talks.common.ai.goal.ConditionallyAvoidEntityGoal;
import indi.fcwyzzr.minecraft.food_talks.common.effects.beneficial.Smelly;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PathfinderMob.class)
public class PathfinderMobMixin extends Mob {
    
    protected PathfinderMobMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }
    
    @SuppressWarnings("UnreachableCode")
    @Inject(method = "<init>", at=@At("RETURN"))
    void constructor_mixin(EntityType<? extends Mob> entityType, Level level, CallbackInfo ci){
        final var self = (PathfinderMob) (Object) this;
        
        if (level != null && !level.isClientSide) {
            self.goalSelector.addGoal(10, new ConditionallyAvoidEntityGoal<> (
                self,
                LivingEntity.class,
                16,
                1.2,
                1.5,
                e -> e.hasEffect(Smelly.INSTANCE.getHolder())
            ));
        }
    }
    
}
