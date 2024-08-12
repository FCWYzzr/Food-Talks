package indi.fcwyzzr.minecraft.food_talks.common.effects.harmful

import indi.fcwyzzr.minecraft.f_lib.registry.FMobEffect
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.util.Mth
import net.minecraft.world.Difficulty
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.ComposterBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import kotlin.math.min

/**
 * apply to non-peaceful player
 *
 * <milk irremovable>
 *
 * causing player throw up every thing in his/her stomach(hunger bar)
 * stop when hunger bar is empty
 *
 * after vomit, player will be slow, dizzy & unable to eat for 10s
 */
object Vomit : FMobEffect(
    MobEffectCategory.HARMFUL,
    0x00592f
){
    override fun shouldApplyEffectTickThisTick(pDuration: Int, pAmplifier: Int): Boolean {
        return pDuration % 3 == 0
    }

    override fun applyEffectTick(livingEntity: LivingEntity, amplifier: Int): Boolean {
        if (livingEntity !is Player)
            return false
        if ((livingEntity.level().difficulty ?: Difficulty.PEACEFUL)
            == Difficulty.PEACEFUL)
            return false

        val shouldContinueApplyEffect = reduceFoodLevel(livingEntity)

        if (! shouldContinueApplyEffect)
            return false

        throwUpInComposter(livingEntity, livingEntity.level())

        if (livingEntity is AbstractClientPlayer)
            clientRenderParticle(livingEntity, livingEntity.clientLevel)

        return true
    }
}

private fun reduceFoodLevel(player: Player): Boolean {
    if (player.foodData.foodLevel != 0){
        player.foodData.foodLevel -= 1
        return true
    }
    player.foodData.setSaturation(0F)
    player.addEffect(MobEffectInstance(
        MobEffects.CONFUSION, 10, 1, false, true, false
    ))

    player.addEffect(MobEffectInstance(
        MobEffects.MOVEMENT_SLOWDOWN, 10, 1, false, true, false
    ))

    player.addEffect(MobEffectInstance(
        Anorexia.holder, 10, 1, false, true, false
    ))

    return false
}

private fun throwUpInComposter(player: Player, level: Level){
    val target = player.pick(20.0, 0F, false)
    if (target.type != HitResult.Type.BLOCK)
        return

    val targetPos = BlockPos(
        (target as BlockHitResult).blockPos
    )

    val composter = level.getBlockState(targetPos)
    if (! composter.`is`(Blocks.COMPOSTER))
        return

    val fillLevel = composter.getValue(ComposterBlock.LEVEL)
    if (fillLevel == 8){
        ComposterBlock.extractProduce(
            player,
            composter,
            level,
            targetPos
        )
        return
    }


    val filledComposter: BlockState = composter.setValue(
        ComposterBlock.LEVEL, min(fillLevel + 3, 8)
    )

    level.setBlock(targetPos, filledComposter, 3)

    level.gameEvent(
        GameEvent.BLOCK_CHANGE,
        targetPos,
        GameEvent.Context.of(player, filledComposter)
    )
}

private fun clientRenderParticle(player: AbstractClientPlayer, level: ClientLevel) {

    val horizontalRotation = - Mth.wrapDegrees(player.yRot) * Mth.DEG_TO_RAD
    val verticalRotation = - Mth.wrapDegrees(player.xRot) * Mth.DEG_TO_RAD

    val startPos = player.eyePosition

    val speed = 50.0 + player.speed
    val speedY = Mth.sin(verticalRotation) * speed
    val speedXZ = Mth.cos(verticalRotation) * speed

    val speedX = Mth.sin(horizontalRotation) * speedXZ
    val speedZ = Mth.cos(horizontalRotation) * speedXZ

    level.addParticle(
        BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()),
        startPos.x, startPos.y - 0.2, startPos.z,
        speedX, speedY, speedZ
    )
}