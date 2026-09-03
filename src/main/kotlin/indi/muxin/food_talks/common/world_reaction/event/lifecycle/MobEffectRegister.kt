package indi.muxin.food_talks.common.world_reaction.event.lifecycle

import indi.muxin.neoforged.registry.doRegister
import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.common.mob_effect.DarknessInfused
import indi.muxin.food_talks.common.mob_effect.EndlessTreasure
import indi.muxin.food_talks.common.mob_effect.Treasure
import indi.muxin.food_talks.common.mob_effect.Happy
import indi.muxin.food_talks.common.mob_effect.ScapeGoat
import indi.muxin.food_talks.common.mob_effect.PoisonResistance
import indi.muxin.food_talks.common.mob_effect.ProjectileImmune
import indi.muxin.food_talks.common.mob_effect.Smelly
import indi.muxin.food_talks.common.mob_effect.Starving
import indi.muxin.food_talks.common.mob_effect.Anorexia
import indi.muxin.food_talks.common.mob_effect.Gout
import indi.muxin.food_talks.common.mob_effect.Overweight
import indi.muxin.food_talks.common.mob_effect.Toothache
import indi.muxin.food_talks.common.mob_effect.Vomit
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.registries.RegisterEvent

@Suppress("DuplicatedCode")
@EventBusSubscriber(
    modid = FoodTalks.MOD_ID,
    value = [Dist.CLIENT, Dist.DEDICATED_SERVER]
)
object MobEffectRegister {

    @SubscribeEvent
    fun registerEffect(event: RegisterEvent){
        arrayOf(
            Anorexia,
            Gout,
            Toothache,
            Vomit,
            Overweight,
            PoisonResistance,
            DarknessInfused,
            Treasure,
            EndlessTreasure,
            Smelly,
            Happy,
            ProjectileImmune,
            Starving,
            ScapeGoat
        ).forEach {
            event doRegister it
        }
    }

}