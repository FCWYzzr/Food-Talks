package indi.fcwyzzr.minecraft.food_talks.common.mixin.tweaks.mechanic;

import indi.fcwyzzr.minecraft.food_talks.common.effects.beneficial.Happy;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
    
    @Shadow public abstract void setExhaustion(float exhaustionLevel);
    
    @Shadow public abstract void setSaturation(float saturationLevel);
    
    @Shadow public abstract void setFoodLevel(int foodLevel);
    
    @Accessor("foodLevel")
    public abstract void setLastFoodLevel(int pFoodLevel);
    
    @Accessor("tickTimer")
    public abstract int getTickTimer();
    
    @Accessor("tickTimer")
    public abstract void setTickTimer(int value);
    
    
    @SuppressWarnings("UnreachableCode")
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    void tick_mixin(Player player, CallbackInfo ci) {
        if (!player.hasEffect(Happy.INSTANCE.getHolder()))
            return;
        
        // act same as vanilla
        final var naturalRegen = player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
        final var foodData = (FoodData) (Object) this;
        
        Difficulty difficulty = player.level().getDifficulty();
        setLastFoodLevel(foodData.getFoodLevel());
        if (foodData.getExhaustionLevel() > 4.0F) {
            setExhaustion(foodData.getExhaustionLevel() - 4F);
            
            if (foodData.getSaturationLevel() > 0F)
                setSaturation(Math.max(foodData.getSaturationLevel() - 1.0F, 0.0F));
            else if (difficulty != Difficulty.PEACEFUL)
                setFoodLevel(Math.max(foodData.getFoodLevel() - 1, 0));
            
        }
        
        
        if (naturalRegen && player.isHurt() && foodData.getFoodLevel() > 6){
            // do fast heal ignore condition
            // heals twice faster than vanilla fast heal
            // healing until you cannot run
            
            setTickTimer(getTickTimer() + 1);
            if (getTickTimer() >= 5) {
                float healRate = 6.0F;
                player.heal(1);
                foodData.addExhaustion(healRate);
                setTickTimer(0);
            }
        }else if (foodData.getFoodLevel() <= 0) {
            setTickTimer(getTickTimer() + 1);
            if (getTickTimer() >= 160) {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    player.hurt(player.damageSources().starve(), 1.0F);
                }
                
                setTickTimer(0);
            }
        } else {
            setTickTimer(0);
        }
        
        ci.cancel();
    }
}
