package indi.fcwyzzr.minecraft.food_talks.common.event.gameplay

import com.google.common.math.IntMath.pow
import indi.fcwyzzr.minecraft.food_talks.FoodTalks
import indi.fcwyzzr.minecraft.food_talks.api.common.item.CompoundFood
import indi.fcwyzzr.minecraft.food_talks.common.effects.beneficial.*
import indi.fcwyzzr.minecraft.food_talks.common.effects.harmful.*
import indi.fcwyzzr.minecraft.food_talks.common.registries.ToothacheDamage
import indi.fcwyzzr.minecraft.food_talks.common.registries.from
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.block.Block
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Start
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Tick
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove
import net.neoforged.neoforge.event.level.BlockDropsEvent
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent
import kotlin.math.max

@EventBusSubscriber(
    modid = FoodTalks.MOD_ID,
    value = [Dist.CLIENT, Dist.DEDICATED_SERVER],
    bus = EventBusSubscriber.Bus.GAME
)
object EffectHandler {
    @SubscribeEvent
    fun entityTryToRemoveEffect(event: Remove){
        if (event.cure != null)
            if (event.effect.`is`(Anorexia.location)
                || event.effect.`is`(Gout.location)
                || event.effect.`is`(Overweight.location)
                || event.effect.`is`(Toothache.location)
                || event.effect.`is`(Vomit.location)
            )

                event.isCanceled = true
    }

    @SubscribeEvent
    fun entityTryToEatWhenAnorexiaOrVomit(event: Start){
        if (!event.entity.hasEffect(Anorexia.holder)
            && !event.entity.hasEffect(Vomit.holder))
            return

        val holdItem = event.entity.mainHandItem

        if (holdItem.isEmpty)
            return

        if (holdItem.`is` { !it.isBound })
            return

        if (holdItem.`is` { it.value().components().has(DataComponents.FOOD) })
            event.isCanceled = true
    }

    @SubscribeEvent
    fun entityEatingWhenToothache(event: Tick){
        if (event.duration < 20)
            return
        if (event.duration % 10 != 0)
            return
        if (!event.entity.hasEffect(Toothache.holder))
            return
        if (!event.item.item.components().has(DataComponents.FOOD))
            return

        event.entity.hurt(ToothacheDamage from event.entity.level(), 1F)
    }

    @SubscribeEvent
    fun entityEatingWhenStaring(event: Tick){
        if (event.duration < 20)
            return
        if (event.duration % 10 != 0)
            return

        if (!event.item.item.components().has(DataComponents.FOOD)
            && event.item.item !is CompoundFood)
            return

        val starvingLevel = event.entity.getEffect(Starving.holder)
            ?.amplifier
            ?: return

        event.duration = max(1, event.duration - pow(starvingLevel + 1, 2))
    }

    @SubscribeEvent
    fun tryToApplyPoisonToEntity(event: Applicable){
        if (event.effectInstance?.effect?.value() !== MobEffects.POISON.value())
            return
        if (!event.entity.hasEffect(PoisonResistance.holder))
            return

        val resistant = event.entity.getEffect(PoisonResistance.holder)!!
        val poison = event.effectInstance!!

        event.result = if (resistant.amplifier >= poison.amplifier)
            Applicable.Result.DO_NOT_APPLY
        else
            Applicable.Result.DEFAULT
    }

    @SubscribeEvent
    fun lootDuringGrace(event: LivingDropsEvent){
        if (event.entity.level().isClientSide)
            return

        val attacker = event.source.entity ?: return
        val level = attacker.level() as ServerLevel



        if (!level.gameRules.getBoolean(GameRules.RULE_DOMOBLOOT))
            return

        if (attacker !is LivingEntity)
            return

        if (attacker is Player && attacker.hasInfiniteMaterials())
            return



        attacker.getEffect(GoddessGrace.holder)
            ?.amplifier
            ?.let { graceLevel ->
                val multiply = Mth.randomBetween(
                    FoodTalks.random,
                    1F,
                    graceLevel + 1.5F
                )

                event.drops.forEach {
                    it.item.count = Mth.ceil(it.item.count * multiply)
                }
            }

        attacker.getEffect(MojangGrace.holder)
            ?.amplifier
            ?.let { graceLevel ->
                val multiply = Mth.randomBetween(
                    FoodTalks.random,
                    1F,
                    graceLevel + 3F
                )

                event.drops.forEach {
                    it.item.count = Mth.ceil(it.item.count * multiply)
                }
            }
    }

    @SubscribeEvent
    fun miningDuringGrace(event: BreakEvent){
        // todo mine with improper tool still drop
        // todo mine in creative still drop
        if (event.player.level().isClientSide)
            return

        val miner = event.player as ServerPlayer
        val level = miner.serverLevel()

        if (!level.gameRules.getBoolean(GameRules.RULE_DOBLOCKDROPS))
            return

        if (miner.hasInfiniteMaterials())
            return

        val tool = miner.mainHandItem

        if (!tool.isCorrectToolForDrops(event.state))
            return

        val drops = miner.getEffect(GoddessGrace.holder)
            ?.amplifier
            .let { graceLevel ->
                val multiply =
                    if (graceLevel != null)
                        Mth.randomBetween(
                            FoodTalks.random,
                            1F,
                            graceLevel + 1.5F
                        )
                    else 1F

                val drops = Block.getDrops(
                    event.state,
                    level,
                    event.pos,
                    level.getBlockEntity(event.pos),
                    miner,
                    tool
                )

                drops.forEach {
                    it.count = Mth.ceil(it.count * multiply)
                }

                drops
            }


        val chance = miner.getEffect(MojangGrace.holder)
            ?.amplifier
            ?.apply {
                val multiply = Mth.randomBetween(
                    FoodTalks.random,
                    1F,
                    this + 3F
                )

                drops.forEach {
                    it.count = Mth.ceil(it.count * multiply)
                }
            }
            ?.let { max(8F - it, 1F) }

        drops.forEach{
            Block.popResource(level, event.pos, it)
        }

        if (chance !== null
            && Mth.randomBetween(FoodTalks.random, 0F, chance) < 1)
            event.isCanceled = true

    }

    @SubscribeEvent
    fun tryToAttackSmelly(event: LivingChangeTargetEvent){
        if (event.newAboutToBeSetTarget == null)
            return
        event.isCanceled = event.newAboutToBeSetTarget!!.hasEffect(Smelly.holder)
    }

    @SubscribeEvent
    fun jackHeadSaveLife(event: LivingDeathEvent){
        if (!event.entity.hasEffect(JackHead.holder))
            return

        event.isCanceled = true
        event.entity.health = event.entity.maxHealth
        event.entity.removeEffect(JackHead.holder)
        if (event.entity.level().isClientSide)
            return
        val serverLevel = event.entity.level() as ServerLevel
        val lightningBolt = EntityType.LIGHTNING_BOLT.create(serverLevel)
        if (lightningBolt != null) {
            lightningBolt.moveTo(event.entity.position())
            lightningBolt.setVisualOnly(true)
            serverLevel.addFreshEntity(lightningBolt)
        }
    }
}