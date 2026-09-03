package indi.muxin.food_talks.common.mixin.mechanic

import indi.muxin.food_talks.common.mob_effect.Happy
import net.minecraft.world.Difficulty
import net.minecraft.world.entity.player.Player
import net.minecraft.world.food.FoodData
import net.minecraft.world.level.GameRules
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import kotlin.math.max

@Mixin(FoodData::class)
abstract class FoodDataMixin {
    @Accessor("foodLevel")
    abstract fun setLastFoodLevel(pFoodLevel: Int)

    @get:Accessor("tickTimer")
    @set:Accessor("tickTimer")
    abstract var tickTimer: Int


    @Inject(method = ["tick"], at = [At("HEAD")], cancellable = true)
    fun tickMixin(player: Player, ci: CallbackInfo) {
        if (!player.hasEffect(Happy.holder))
            return

        // act same as vanilla
        val naturalRegen = player.level().gameRules.getBoolean(GameRules.RULE_NATURAL_REGENERATION)
        @Suppress("CAST_NEVER_SUCCEEDS")
        val foodData = this as FoodData

        val difficulty = player.level().difficulty
        setLastFoodLevel(foodData.foodLevel)
        if (foodData.exhaustionLevel > 4.0f) {
            setExhaustion(foodData.exhaustionLevel - 4f)

            if (foodData.saturationLevel > 0f)
                setSaturation(max(foodData.saturationLevel - 1.0f, 0.0f))
            else if (difficulty != Difficulty.PEACEFUL)
                foodLevel = max(foodData.foodLevel - 1, 0)
        }


        if (naturalRegen && player.isHurt && foodData.foodLevel > 6) {
            // do fast heal ignore condition
            // heals twice faster than vanilla fast heal
            // healing until you cannot run

            this.tickTimer += 1
            if (this.tickTimer >= 5) {
                val healRate = 6.0f
                player.heal(1f)
                foodData.addExhaustion(healRate)
                this.tickTimer = 0
            }
        } else if (foodData.foodLevel <= 0) {
            this.tickTimer += 1
            if (this.tickTimer >= 160) {
                if (player.health > 10.0f || difficulty == Difficulty.HARD || player.health > 1.0f && difficulty == Difficulty.NORMAL) {
                    player.hurt(player.damageSources().starve(), 1.0f)
                }

                this.tickTimer = 0
            }
        } else {
            this.tickTimer = 0
        }

        ci.cancel()
    }
}