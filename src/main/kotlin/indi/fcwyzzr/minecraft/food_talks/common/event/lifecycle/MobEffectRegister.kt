package indi.fcwyzzr.minecraft.food_talks.common.event.lifecycle

import indi.fcwyzzr.minecraft.f_lib.registry.doRegister
import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.common.mob_effect.beneficial.*
import indi.fcwyzzr.minecraft.food_talks.common.mob_effect.harmful.*
import indi.fcwyzzr.minecraft.food_talks.common.mob_effect.harmful.Vomit
import indi.fcwyzzr.minecraft.food_talks.common.registries.deferred.*
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.registries.RegisterEvent

@Suppress("DuplicatedCode")
@EventBusSubscriber(
    modid = FoodTalks.MOD_ID,
    value = [Dist.CLIENT, Dist.DEDICATED_SERVER],
    bus = EventBusSubscriber.Bus.MOD
)
object MobEffectRegister {

    @SubscribeEvent
    fun registerEffect(event: RegisterEvent){
        event doRegister Anorexia
        event doRegister Gout
        event doRegister Toothache
        event doRegister Vomit
        event doRegister Overweight

        event doRegister PoisonResistance
        event doRegister DarknessInfused
        event doRegister GoddessGrace
        event doRegister GameGrace
        event doRegister Smelly
        event doRegister Happy
        event doRegister ProjectileImmune
        event doRegister Starving
        event doRegister JackHead
    }
}