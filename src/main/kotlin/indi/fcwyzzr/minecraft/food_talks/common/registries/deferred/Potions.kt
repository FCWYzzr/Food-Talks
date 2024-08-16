package indi.fcwyzzr.minecraft.food_talks.common.registries.deferred

import indi.fcwyzzr.minecraft.f_lib.registry.FPotion
import indi.fcwyzzr.minecraft.food_talks.common.mob_effect.beneficial.*
import indi.fcwyzzr.minecraft.food_talks.common.mob_effect.harmful.*
import indi.fcwyzzr.minecraft.food_talks.common.mob_effect.harmful.Vomit
import indi.fcwyzzr.minecraft.food_talks.toRegistryName
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.item.alchemy.Potion

private fun MobEffectInstance.createPotion(name: String? = null) =
    FPotion(this, name = name?.toRegistryName())

private fun FPotion.register(): Holder.Reference<Potion> = Registry.registerForHolder(
    BuiltInRegistries.POTION,
    this.location,
    this
)


// beneficial
val poisonResistance = PoisonResistance
    .createDefaultInstance()
    .createPotion()
    .register()

val poisonResistanceII = PoisonResistance
    .createUpgradedInstance()
    .createPotion("StrongPoisonResistance")
    .register()

val lPoisonResistance = PoisonResistance
    .createExtendedInstance()
    .createPotion("LongPoisonResistance")
    .register()


val darknessInfusing = DarknessInfused
    .createDefaultInstance()
    .createPotion("DarknessInfusing")
    .register()

val darknessInfusingII = DarknessInfused
    .createUpgradedInstance()
    .createPotion("StrongDarknessInfusing")
    .register()

val lDarknessInfusing = DarknessInfused
    .createExtendedInstance()
    .createPotion("LongDarknessInfusing")
    .register()

val goddessGrace = GoddessGrace
    .createDefaultInstance()
    .createPotion("GoddessGrace")
    .register()

val goddessGraceII = GoddessGrace
    .createUpgradedInstance()
    .createPotion("StrongGoddessGrace")
    .register()

val lGoddessGrace = GoddessGrace
    .createExtendedInstance()
    .createPotion("LongGoddessGrace")
    .register()

val gameGrace = GameGrace
    .createDefaultInstance()
    .createPotion("GameGrace")
    .register()

val gameGraceII = GameGrace
    .createUpgradedInstance()
    .createPotion("StrongGameGrace")
    .register()

val lGameGrace = GameGrace
    .createExtendedInstance()
    .createPotion("LongGameGrace")
    .register()

val smelly = Smelly
    .createDefaultInstance()
    .createPotion()
    .register()

val lSmelly = Smelly
    .createExtendedInstance()
    .createPotion("LongSmelly")
    .register()

val happy = Happy
    .createDefaultInstance()
    .createPotion()
    .register()

val lHappy = Happy
    .createExtendedInstance()
    .createPotion("LongHappy")
    .register()

val projectileImmune = ProjectileImmune
    .createDefaultInstance()
    .createPotion()
    .register()

val lProjectileImmune = ProjectileImmune
    .createExtendedInstance()
    .createPotion("LongProjectileImmune")
    .register()

val starving = Starving
    .createDefaultInstance()
    .createPotion()
    .register()

val lStarving = Starving
    .createExtendedInstance()
    .createPotion("LongStarving")
    .register()

val starvingII = Starving
    .createUpgradedInstance()
    .createPotion("StrongStarving")
    .register()

val jackHead = JackHead
    .createDefaultInstance()
    .createPotion()
    .register()

val lJackHead = JackHead
    .createExtendedInstance()
    .createPotion("LongJackHead")
    .register()

// harmful

val anorexia = Anorexia
    .createDefaultInstance()
    .createPotion()
    .register()

val lAnorexia = Anorexia
    .createExtendedInstance()
    .createPotion("LongAnorexia")
    .register()

val gout = Gout
    .createDefaultInstance()
    .createPotion()
    .register()

val lGout = Gout
    .createExtendedInstance()
    .createPotion("LongGout")
    .register()

val vomit = Vomit
    .createNewInstance(-1, 0)
    .createPotion()
    .register()

val toothache = Toothache
    .createDefaultInstance()
    .createPotion()
    .register()

val lToothache = Toothache
    .createExtendedInstance()
    .createPotion("LongToothache")
    .register()

val overweight = Overweight
    .createDefaultInstance()
    .createPotion()
    .register()

val lOverweight = Overweight
    .createExtendedInstance()
    .createPotion("LongOverweight")
    .register()

val overweightII = Overweight
    .createUpgradedInstance()
    .createPotion("StrongOverweight")
    .register()



