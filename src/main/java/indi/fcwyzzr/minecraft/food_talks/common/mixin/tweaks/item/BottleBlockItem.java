package indi.fcwyzzr.minecraft.food_talks.common.mixin.tweaks.item;

import indi.fcwyzzr.minecraft.food_talks.common.block.BottleBlock;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nonnull;


/**
 * code copied from BlockItem
 */
@Mixin(BottleItem.class)
public class BottleBlockItem extends Item {
    
    public BottleBlockItem(Properties pProperties) {
        super(pProperties);
    }
    
    @Override
    @Nonnull
    public InteractionResult useOn(@Nonnull UseOnContext pContext) {
        InteractionResult interactionresult = this.place_mixin(new BlockPlaceContext(pContext));
        if (!interactionresult.consumesAction() && pContext.getItemInHand().has(DataComponents.FOOD)) {
            InteractionResult interactionResult1 = super.use(pContext.getLevel(), pContext.getPlayer(), pContext.getHand()).getResult();
            return interactionResult1 == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : interactionResult1;
        } else {
            return interactionresult;
        }
    }
    
    @Unique
    public InteractionResult place_mixin(BlockPlaceContext context) {
        if (!context.canPlace())
            return InteractionResult.FAIL;
        
        BlockState blockstate = BottleBlock.Companion.getInstance().defaultBlockState();
        if (!this.placeBlock_mixin(context, blockstate))
            return InteractionResult.FAIL;
        
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        BlockState currentState = level.getBlockState(blockpos);
        
        
        if (currentState.is(blockstate.getBlock())) {
            currentState.getBlock().setPlacedBy(level, blockpos, currentState, player, itemstack);
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
            }
        }
        
        SoundType soundtype = currentState.getSoundType(level, blockpos, context.getPlayer());
        level.playSound(
            player,
            blockpos,
            this.getPlaceSound_mixin(currentState, level, blockpos, context.getPlayer()),
            SoundSource.BLOCKS,
            (soundtype.getVolume() + 1.0F) / 2.0F,
            soundtype.getPitch() * 0.8F
        );
        level.gameEvent(GameEvent.BLOCK_PLACE, blockpos, GameEvent.Context.of(player, currentState));
        itemstack.consume(1, player);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    

    @Unique
    private SoundEvent getPlaceSound_mixin(BlockState state, Level world, BlockPos pos, Player entity) {
        var glass = Blocks.GLASS;
        return glass.getSoundType(glass.defaultBlockState(), world, pos, entity).getPlaceSound();
    }
    
    @Unique
    private boolean placeBlock_mixin(BlockPlaceContext pContext, BlockState pState) {
        return pContext.getLevel().setBlock(pContext.getClickedPos(), pState, 11);
    }
    
    
    
}



