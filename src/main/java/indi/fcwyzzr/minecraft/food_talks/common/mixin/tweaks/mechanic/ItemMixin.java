package indi.fcwyzzr.minecraft.food_talks.common.mixin.tweaks.mechanic;

import indi.fcwyzzr.minecraft.food_talks.api.common.item.CompoundFood;
import indi.fcwyzzr.minecraft.food_talks.common.effects.harmful.Anorexia;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "use", at = @At("RETURN"), cancellable = true)
    private void use_mixin(Level level, Player player, InteractionHand usedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir){
        if (!cir.getReturnValue().getResult().equals(InteractionResult.CONSUME))
            return;
        final var item = player.getItemInHand(usedHand);
        if (! CompoundFood.Companion.isFood(item))
            return;
        
        if (!Anorexia.INSTANCE.canEat(item, player))
            cir.setReturnValue(InteractionResultHolder.fail(item));
    }
}
