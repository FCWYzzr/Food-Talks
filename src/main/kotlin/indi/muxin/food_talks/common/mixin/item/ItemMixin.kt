package indi.muxin.food_talks.common.mixin.item

import indi.muxin.food_talks.common.item.CompoundFood.Companion.isFood
import indi.muxin.food_talks.common.mob_effect.Anorexia.canEat
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(Item::class)
class ItemMixin {
    @Inject(method = ["use"], at = [At("RETURN")], cancellable = true)
    private fun useMixin(
        @Suppress("unused")
        level: Level?,
        player: Player,
        usedHand: InteractionHand,
        cir: CallbackInfoReturnable<InteractionResultHolder<ItemStack?>?>
    ) {
        if (cir.getReturnValue()!!.result != InteractionResult.CONSUME)
            return
        val item = player.getItemInHand(usedHand)
        if (!isFood(item, false))
            return

        if (!canEat(item, player))
            cir.setReturnValue(InteractionResultHolder.fail(item))
    }
}