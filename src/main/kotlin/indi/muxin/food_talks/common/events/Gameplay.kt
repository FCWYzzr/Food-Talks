package indi.muxin.food_talks.common.events

import com.google.common.math.IntMath.pow
import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.common.item.CompoundFood
import indi.muxin.food_talks.common.mob_effect.EndlessTreasure
import indi.muxin.food_talks.common.mob_effect.Treasure
import indi.muxin.food_talks.common.mob_effect.Scapegoat
import indi.muxin.food_talks.common.mob_effect.PoisonResistance
import indi.muxin.food_talks.common.mob_effect.Smelly
import indi.muxin.food_talks.common.mob_effect.Starving
import indi.muxin.food_talks.common.mob_effect.Anorexia
import indi.muxin.food_talks.common.mob_effect.Toothache
import indi.muxin.food_talks.common.mob_effect.Vomit
import indi.muxin.food_talks.common.registries.ToothacheDamage
import indi.muxin.food_talks.common.registries.from
import indi.muxin.food_talks.common.registries.milkIrremovable
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.block.Block
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.ICancellableEvent
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Tick
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove
import net.neoforged.neoforge.event.level.BlockDropsEvent
import net.neoforged.neoforge.event.level.BlockEvent
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent
import kotlin.math.max
import kotlin.math.min

@EventBusSubscriber(
    modid = FoodTalks.MOD_ID,
    value = [Dist.CLIENT, Dist.DEDICATED_SERVER]
)
object Gameplay {
    @SubscribeEvent
    fun tickEating(tick: Tick){
        val itemStack = tick.item
        val item = itemStack.item
        if (item !is CompoundFood)
            return


        val chewTick = item.chewTick(itemStack)
        if (tick.duration != 1)
            return


        val entity = tick.entity
        if (itemStack.damageValue < itemStack.maxDamage
            && item.uponBite(itemStack, entity)
            && Anorexia.canEat(itemStack, entity))
            tick.duration += chewTick
        else
            tick.isCanceled = true


        itemStack.damageValue += 1
    }

    @SubscribeEvent
    fun entityTryToRemoveEffect(event: Remove){
        if (event.cure != null)
            if (event.effect.`is`(milkIrremovable))
                event.isCanceled = true
    }

    @SubscribeEvent
    fun entityTryToEatWhenAnorexiaOrVomit(event: LivingEntityUseItemEvent){
        if (!event.entity.hasEffect(Anorexia.holder)
            && !event.entity.hasEffect(Vomit.holder))
            return

        val holdItem = event.entity.mainHandItem

        if (holdItem.isEmpty)
            return

        if (holdItem.`is` { !it.isBound })
            return

        if (CompoundFood.isFood(event.item) && event is ICancellableEvent)
            event.isCanceled = true
    }

    @SubscribeEvent
    fun entityEatingWhenToothache(event: Tick){
        if (!event.entity.hasEffect(Toothache.holder))
            return
        if (event.duration % 10 != 0)
            return
        if (!CompoundFood.isFood(event.item))
            return

        event.entity.hurt(ToothacheDamage from event.entity.level(), 1F)
    }

    @SubscribeEvent
    fun entityEatingWhenStarving(event: Tick){
        if (event.duration < 20)
            return
        if (event.duration % 10 != 0)
            return

        if (!CompoundFood.isFood(event.item))
            return

        val starvingLevel = event.entity.getEffect(Starving.holder)
            ?.amplifier
            ?.plus(1)
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
        else {
            event.entity.removeEffect(PoisonResistance.holder)
            Applicable.Result.APPLY
        }
    }

    @SubscribeEvent
    fun lootingDuringTreasure(event: LivingDropsEvent){
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

        if (event.entity is Player)
            return

        attacker.getEffect(Treasure.holder)
            ?.amplifier
            ?.let { treasureLevel ->
                val multiply = Mth.randomBetween(
                    FoodTalks.random,
                    1F,
                    treasureLevel * 0.5F + 1.05F
                )

                event.drops
                    .filter {it.item.isStackable}
                    .forEach{
                        it.item.count = Mth.ceil(it.item.count * multiply)
                    }
            }

        val reviveRate = (attacker.getEffect(EndlessTreasure.holder)
            ?.amplifier ?: return).let { min(max(0F, it / 6.0F), 0.5F) }

        if (Mth.randomBetween(FoodTalks.random, 0F, 1F) < reviveRate)
            return lootingDuringTreasure(event)
    }

    private fun shouldIgnoreMiningEvent(blk: BlockEvent, player: Player?): Boolean {
        if (player == null)
            return true

        if (blk.level.isClientSide)
            return true

        val level = blk.level as ServerLevel
        if (!level.gameRules.getBoolean(GameRules.RULE_DOBLOCKDROPS))
            return true

        if (player.hasInfiniteMaterials())
            return true

        val tool = player.mainHandItem

        return !tool.isCorrectToolForDrops(blk.state)
    }

    private fun calculateTreasureDrop(event: BlockEvent, player: Player, level: ServerLevel, tool: ItemStack): List<ItemStack> {
        val drops = Block.getDrops(
            event.state,
            level,
            event.pos,
            level.getBlockEntity(event.pos),
            player,
            tool
        )

        val treasureLevel = player.getEffect(Treasure.holder) ?.amplifier ?: return drops
        val multiply = Mth.randomBetween(
            FoodTalks.random,
            treasureLevel * 0.1F + 1F,
            treasureLevel * 0.3F + 1.3F
        )

        drops.forEach {
            it.count = Mth.ceil(it.count * multiply)
        }

        return drops
    }


    @SubscribeEvent
    fun miningDuringEndlessTreasure(event: BreakEvent){
        if (shouldIgnoreMiningEvent(event, event.player))
            return

        val miner = event.player
        val reviveRate = (miner.getEffect(EndlessTreasure.holder)
            ?.amplifier ?: return ).let { min(max(0F, it / 6.0F), 0.5F) }


        if (Mth.randomBetween(FoodTalks.random, 0F, 1.0F) < reviveRate) {
            calculateTreasureDrop(event, miner, event.level as ServerLevel, miner.mainHandItem).forEach{
                Block.popResource(event.level as ServerLevel, event.pos, it)
            }
            event.isCanceled = true
        }
    }

    @SubscribeEvent
    fun miningDuringTreasure(event: BlockDropsEvent){
        if (shouldIgnoreMiningEvent(event, event.breaker as? Player? ?: return))
           return
        if (!(event.breaker as Player).hasEffect(Treasure.holder))
            return
        event.drops.clear()
        calculateTreasureDrop(event, event.breaker as Player, event.level as ServerLevel, (event.breaker as Player).mainHandItem).forEach {
            event.drops.add(ItemEntity(event.level,
                event.pos.x * 1.0,
                event.pos.y * 1.0,
                event.pos.z * 1.0, it))
        }
    }


    @SubscribeEvent
    fun tryToAttackSmelly(event: LivingChangeTargetEvent){
        if (event.entity.level().isClientSide)
            return
        val newTarget = event.newAboutToBeSetTarget ?: return
        if (!newTarget.hasEffect(Smelly.holder))
            return
        event.isCanceled = true
    }

    @SubscribeEvent
    fun oneMoreChance(event: LivingDeathEvent){
        if (event.entity.level().isClientSide)
            return
        if (!event.entity.hasEffect(Scapegoat.holder))
            return

        event.isCanceled = true
        event.entity.health = event.entity.maxHealth
        event.entity.removeEffect(Scapegoat.holder)
        event.entity.addEffect(MobEffectInstance(MobEffects.DARKNESS, 20))
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