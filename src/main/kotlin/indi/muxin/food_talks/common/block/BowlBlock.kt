package indi.muxin.food_talks.common.block

import com.mojang.serialization.Codec
import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.common.mob_effect.FTMobEffectHolders
import indi.muxin.food_talks.common.mob_effect.MobEffectDetail
import indi.muxin.food_talks.common.registries.FTTags
import indi.muxin.neoforged.utils.buildItemStack
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.food.Foods
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUtils
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.*
import kotlin.jvm.optionals.getOrDefault
import kotlin.jvm.optionals.getOrNull
import kotlin.math.max
import kotlin.math.round

class BowlBlockEntity(pos: BlockPos, blockState: BlockState): BlockEntity(FTBlockEntityTypeHolders.BOWL_BLOCK_ENTITY.value(), pos, blockState) {
    var totalNut = 0
        private set
    var totalSat = 0.0f
        private set
    var contentLevel = 0
        private set
    val effectPool: Map<Holder<MobEffect>, MobEffectDetail>
        field = mutableMapOf()


    enum class PutItemResult {
        FAIL,
        SUCCESS_NO_GROW,
        SUCCESS
    }

    fun clear(){
        totalNut = 0
        totalSat = 0.0f
        contentLevel = 0
        effectPool.clear()
    }

    fun tipBread(breads: ItemStack) = buildItemStack(Items.BREAD) {
        val fillLevel = blockState.getValue(BowlBlock.PROPERTY_FILL_LEVEL)
        val detail: FoodProperties = breads[DataComponents.FOOD] ?: Foods.BREAD

        val consumeNut = totalNut / fillLevel
        val neoNut = detail.nutrition + consumeNut
        totalNut -= consumeNut

        val consumeSat = totalSat / fillLevel
        val neoSat = round((detail.saturation * detail.nutrition + consumeSat) / detail.saturation * 100) / 100
        totalSat -= consumeSat

        val effects = effectPool.map { (effect, detail) ->
            val consumeDuration = detail.duration / fillLevel
            detail.duration -= consumeDuration
            MobEffectInstance(effect, consumeDuration, detail.amplifier)
        }
        effectPool.filterValues { v->  v.duration == 0 }.keys.forEach { k -> effectPool.remove(k) }


        it.set(DataComponents.FOOD, FoodProperties(
            neoNut, neoSat, false,
            detail.eatSeconds / 2, Optional.empty(), listOf()
        ))
        it.set(DataComponents.POTION_CONTENTS, PotionContents(
            Optional.empty(), Optional.empty(), effects
        ))


        if (fillLevel == 1)
            clear()
        setChanged()
    }


    fun takeSip(player: Player) {
        val fillLevel = blockState.getValue(BowlBlock.PROPERTY_FILL_LEVEL)

        val consumeNut = totalNut / fillLevel
        totalNut -= consumeNut

        val consumeSat = totalSat / fillLevel
        totalSat -= consumeSat

        val effects = effectPool.map { (effect, detail) ->
            val consumeDuration = detail.duration / fillLevel
            detail.duration -= consumeDuration
            MobEffectInstance(effect, consumeDuration, detail.amplifier)
        }
        effectPool.filterValues { v->  v.duration == 0 }.keys.forEach { k -> effectPool.remove(k) }

        if (fillLevel == 1)
            clear()
        setChanged()

        player.foodData.eat(consumeNut, consumeSat / consumeNut)
        effects.forEach(player::addEffect)
    }

    fun putItem(itemStack: ItemStack, fillLevel: Int): PutItemResult = when {
        itemStack.`is`(Items.SUGAR) -> {
            putSugar()
            PutItemResult.SUCCESS_NO_GROW
        }

        fillLevel >= BowlBlock.MAX_FILL_LEVEL -> PutItemResult.FAIL

        itemStack.`is`(Items.POTION) -> {
            if (level?.isClientSide != true)
                putPotion(itemStack)
            PutItemResult.SUCCESS
        }

        itemStack.`is`(Items.HONEY_BOTTLE) -> {
            if (level?.isClientSide != true)
                putHoney()
            PutItemResult.SUCCESS
        }
        itemStack.`is`(Items.OMINOUS_BOTTLE) -> {
            if (level?.isClientSide != true)
                putOminousBottle(itemStack)
            PutItemResult.SUCCESS
        }
        itemStack.`is`(FTTags.SOUP) -> {
            if (level?.isClientSide != true)
                putSoupFood(itemStack)
            PutItemResult.SUCCESS
        }

        else -> PutItemResult.FAIL
    }

    private fun putPotion(itemStack: ItemStack) {
        val potionContent = itemStack[DataComponents.POTION_CONTENTS]
        if (potionContent == null || !potionContent.hasEffects())
            return

        ++ contentLevel
        totalSat = max(0f, totalSat - 5)

        potionContent.potion().getOrNull() ?.let { potion ->
            for (effect in potion.value().effects)
                effectPool[effect.effect] = MobEffectDetail(effect.duration, effect.amplifier) merge effectPool[effect.effect]
        }
        for (effect in potionContent.customEffects)
            effectPool[effect.effect] = MobEffectDetail(effect.duration, effect.amplifier) merge effectPool[effect.effect]
        setChanged()
    }

    private fun putSugar() {
        totalSat += 0.5f
        ++ contentLevel
        setChanged()
    }

    private fun putHoney() {
        totalSat += 1.5f
        ++ contentLevel
        effectPool[FTMobEffectHolders.POISON_RESISTANCE] = MobEffectDetail(60 * FoodTalks.TPS, 0) merge effectPool[FTMobEffectHolders.POISON_RESISTANCE]
        setChanged()
    }

    private fun putOminousBottle(itemStack: ItemStack) {
        totalSat = max(0f, totalSat - 5)
        ++ contentLevel
        effectPool[MobEffects.BAD_OMEN] = MobEffectDetail(100 * 60 * FoodTalks.TPS, itemStack[DataComponents.OMINOUS_BOTTLE_AMPLIFIER] ?: 0) merge effectPool[MobEffects.BAD_OMEN]
        setChanged()
    }

    private fun putSoupFood(ingredient: ItemStack) {
        ++ contentLevel

        ingredient.components[DataComponents.FOOD] ?.let {foodProp ->
            totalNut += foodProp.nutrition
            totalSat += foodProp.saturation * foodProp.nutrition
            for (pEffect in foodProp.effects){
                val effect = pEffect.effect()
                val prob = pEffect.probability
                effectPool[effect.effect] =
                    MobEffectDetail((effect.duration * prob).toInt(), effect.amplifier) merge effectPool[effect.effect]
            }
        }

        ingredient.components[DataComponents.SUSPICIOUS_STEW_EFFECTS] ?.let { ssEffect ->
            for (effect in ssEffect.effects)
                effectPool[effect.effect] = MobEffectDetail(effect.duration, 0) merge effectPool[effect.effect]
        }
        setChanged()
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)

        val ops = registries.createSerializationContext(NbtOps.INSTANCE)

        totalNut = Codec.INT.parse(ops, tag.get("total_nut")).resultOrPartial().getOrDefault(0)
        totalSat = Codec.FLOAT.parse(ops, tag.get("total_sat")).resultOrPartial().getOrDefault(0f)
        contentLevel = Codec.INT.parse(ops, tag.get("content_level")).resultOrPartial().getOrDefault(0)
        effectPool.clear()

        tag.getList("effect_pool", 10)
            .asSequence()
            .mapNotNull {
                MobEffectInstance.CODEC.parse(ops, it).resultOrPartial().getOrNull()
            }
            .forEach{
                effectPool[it.effect] = MobEffectDetail(it)
            }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)

        tag.put("total_nut", Codec.INT.encodeStart(
            registries.createSerializationContext(NbtOps.INSTANCE),
            totalNut
        ).getOrThrow())
        tag.put("total_sat", Codec.FLOAT.encodeStart(
            registries.createSerializationContext(NbtOps.INSTANCE),
            totalSat
        ).getOrThrow())
        tag.put("content_level", Codec.INT.encodeStart(
            registries.createSerializationContext(NbtOps.INSTANCE),
            contentLevel
        ).getOrThrow())
        tag.put("effect_pool", ListTag().apply {
            effectPool.asSequence()
                .map { (effect, detail) ->
                    MobEffectInstance.CODEC.encodeStart(
                        registries.createSerializationContext(NbtOps.INSTANCE),
                        MobEffectDetail.instate(effect, detail)
                    ).getOrThrow()
                }.forEach(::add)
        })
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
        ClientboundBlockEntityDataPacket.create(this)

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        val tag = CompoundTag()
        saveAdditional(tag, registries)
        return tag
    }


}


class BowlBlock: Block(Properties.of().apply {
    instabreak()
    explosionResistance(0F)
    sound(SoundType.WOOD)
    noCollission()
    noOcclusion()
    isViewBlocking { _, _, _ -> false }
    isValidSpawn{_, _, _, _-> false}
    pushReaction(PushReaction.DESTROY)
}), EntityBlock {
    companion object{
        const val MAX_FILL_LEVEL = 8
        @JvmField
        val PROPERTY_FILL_LEVEL: IntegerProperty = IntegerProperty.create("fill_level", 0, MAX_FILL_LEVEL)

        private val outlineShape: VoxelShape = box(
            0.0, 0.0, 0.0,
            16.0, 9.0, 16.0
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(PROPERTY_FILL_LEVEL)
    }

    override fun getShape(
        pState: BlockState,
        pLevel: BlockGetter,
        pPos: BlockPos,
        pContext: CollisionContext
    ): VoxelShape {
        return outlineShape
    }

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return outlineShape
    }

    override fun getBlockSupportShape(pState: BlockState, pLevel: BlockGetter, pPos: BlockPos): VoxelShape {
        return outlineShape
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState) = BowlBlockEntity(pos, state)

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        val entity = level.getBlockEntity(pos) as BowlBlockEntity
        val fillLevel: Int = state.getValue(PROPERTY_FILL_LEVEL)
        if (stack.`is`(Items.BREAD) && fillLevel > 0){
            if (!level.isClientSide) {
                val newStack = ItemUtils.createFilledResult(stack, player, entity.tipBread(stack))
                if (newStack !== stack)
                    player.setItemInHand(hand, newStack)
            }
            level.setBlockAndUpdate(pos, state.setValue(PROPERTY_FILL_LEVEL, fillLevel - 1))
            return ItemInteractionResult.SUCCESS
        }
        return when (entity.putItem(stack, fillLevel)){
            BowlBlockEntity.PutItemResult.FAIL -> ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
            BowlBlockEntity.PutItemResult.SUCCESS_NO_GROW -> {
                ItemInteractionResult.SUCCESS
            }
            BowlBlockEntity.PutItemResult.SUCCESS -> {
                level.setBlockAndUpdate(pos, state.setValue(PROPERTY_FILL_LEVEL, fillLevel + 1))
                if (!level.isClientSide) {
                    val remaining = if (stack.`is`(Items.POTION) || stack.`is`(Items.HONEY_BOTTLE))
                        Items.GLASS_BOTTLE.defaultInstance
                    else if (stack.`is`(FTTags.SOUP))
                        Items.BOWL.defaultInstance
                    else
                        ItemStack.EMPTY

                    if (!remaining.isEmpty) {
                        val newStack = ItemUtils.createFilledResult(stack, player, remaining)
                        if (newStack !== stack)
                            player.setItemInHand(hand, newStack)
                    }
                }
                ItemInteractionResult.SUCCESS
            }
        }

    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        val fillLevel: Int = state.getValue(PROPERTY_FILL_LEVEL)
        if (fillLevel == 0)
            return InteractionResult.PASS
        if (level.isClientSide)
            level.playSound(player, pos, SoundEvents.HONEY_DRINK, SoundSource.PLAYERS, 1f, 1f)
        else {
            (level.getBlockEntity(pos) as BowlBlockEntity).takeSip(player)
            level.setBlockAndUpdate(pos, state.setValue(PROPERTY_FILL_LEVEL, fillLevel - 1))
        }
        return InteractionResult.SUCCESS_NO_ITEM_USED
    }
}
