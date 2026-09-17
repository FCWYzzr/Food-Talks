@file:Suppress("unused")

package indi.muxin.food_talks.common.block

import indi.muxin.food_talks.toResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.RegisterEvent

object FTBlocks {
    @JvmField val BOTTLE_BLOCK  = BottleBlock()
    @JvmField val BOWL_BLOCK    = BowlBlock()
    @JvmField val PLATE_BLOCK   = PlateBlock()
}

object FTBlockHolders {
    private fun toHolder(name: String): Holder<Block> = DeferredHolder
        .create(Registries.BLOCK, name.toResourceLocation())

    @JvmField val BOTTLE_BLOCK  : Holder<Block> = toHolder("BottleBlock")
    @JvmField val BOWL_BLOCK    : Holder<Block> = toHolder("BowlBlock")
    @JvmField val PLATE_BLOCK   : Holder<Block> = toHolder("PlateBlock")
}

@Suppress("TYPE_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "UNCHECKED_CAST")
object FTBlockEntityTypes {
    private fun <T: BlockEntity> entityType(factory: (bp: BlockPos, bs: BlockState) -> T, block: Block): BlockEntityType<T> = BlockEntityType.Builder.of(factory, block).build(null)

    @JvmField val BOTTLE_BLOCK_ENTITY   = entityType(::BottleBlockEntity, FTBlocks.BOTTLE_BLOCK)
    @JvmField val BOWL_BLOCK_ENTITY     = entityType(::BowlBlockEntity, FTBlocks.BOWL_BLOCK)
    @JvmField val PLATE_BLOCK_ENTITY    = entityType(::PlateBlockEntity, FTBlocks.PLATE_BLOCK)
}

object FTBlockEntityTypeHolders {
    private fun toHolder(name: String) = DeferredHolder
        .create(Registries.BLOCK_ENTITY_TYPE, name.toResourceLocation())

    @JvmField val BOTTLE_BLOCK_ENTITY   : Holder<BlockEntityType<*>> = toHolder("BottleBlockEntity")
    @JvmField val BOWL_BLOCK_ENTITY     : Holder<BlockEntityType<*>> = toHolder("BowlBlockEntity")
    @JvmField val PLATE_BLOCK_ENTITY    : Holder<BlockEntityType<*>> = toHolder("PlateBlockEntity")
}

fun registerFTBlocksAndEntities(ev: RegisterEvent) {
    ev.register(Registries.BLOCK){
        it.register(FTBlockHolders.BOTTLE_BLOCK.key!!.location(), FTBlocks.BOTTLE_BLOCK)
        it.register(FTBlockHolders.BOWL_BLOCK  .key!!.location(), FTBlocks.BOWL_BLOCK  )
        it.register(FTBlockHolders.PLATE_BLOCK .key!!.location(), FTBlocks.PLATE_BLOCK )
    }

    ev.register(Registries.BLOCK_ENTITY_TYPE){
        it.register(FTBlockEntityTypeHolders.BOTTLE_BLOCK_ENTITY.key!!.location(), FTBlockEntityTypes.BOTTLE_BLOCK_ENTITY)
        it.register(FTBlockEntityTypeHolders.BOWL_BLOCK_ENTITY  .key!!.location(), FTBlockEntityTypes.BOWL_BLOCK_ENTITY  )
        it.register(FTBlockEntityTypeHolders.PLATE_BLOCK_ENTITY .key!!.location(), FTBlockEntityTypes.PLATE_BLOCK_ENTITY )
    }
}

