package indi.fcwyzzr.minecraft.food_talks.common.block.entity

import com.mojang.logging.LogUtils
import indi.fcwyzzr.minecraft.f_lib.registry.FRegistry
import indi.fcwyzzr.minecraft.food_talks.common.block.BottleBlock
import indi.fcwyzzr.minecraft.food_talks.common.item.Cocktail.mergeMobEffectInstance
import indi.fcwyzzr.minecraft.food_talks.toRegistryName
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.ResourceKey
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.RegisterEvent
import org.slf4j.Logger
import kotlin.jvm.optionals.getOrNull

class BottleBlockEntity(
    pos: BlockPos,
    state: BlockState
): BlockEntity(type, pos, state) {
    private var waterLevel = 0
    var upgrade = 0
        private set
    var extend = 0
        private set
    var detoxified = false
        private set

    private val contents = mutableMapOf<Holder<MobEffect>, MobEffectInstance>()

    fun addPotion(effects: Collection<MobEffectInstance>){
        waterLevel ++
        effects
            .asSequence()
            .map { it.effect to it }
            .map { (effect, instance) ->
                effect to mergeMobEffectInstance(
                    instance,
                    contents[effect]
                )
            }
            .forEach{ (effect, instance) ->
                contents[effect] = instance
            }
    }

    fun allowNewCondiment(addon: Int): Boolean {
        val condiment = (if (detoxified) 2 else 0) + upgrade + extend + addon
        return condiment < waterLevel * 2
    }

    fun upgrade(){
        upgrade ++
    }

    fun extend(){
        extend ++
    }

    fun detoxify(): Boolean{
        if (detoxified)
            return false

        detoxified = true
        return true
    }

    fun dumpContents() = contents.values

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        contents.clear()

        detoxified = tag.getBoolean("detoxified")
        upgrade = tag.getInt("upgrade")
        extend = tag.getInt("extend")

        tag.getList("contents", 10)
            .asSequence()
            .map {
                MobEffectInstance.CODEC.parse(
                    registries.createSerializationContext(NbtOps.INSTANCE),
                    it
                ).resultOrPartial { name ->
                    LOGGER.error("Tried to load invalid item: '{}'", name)
                }
            }.mapNotNull {
                it.getOrNull()
            }
            .forEach{
                contents[it.effect] = it
            }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        val contentTag = ListTag().apply {
            contents.asSequence()
                .map {
                    MobEffectInstance.CODEC.encodeStart(
                        registries.createSerializationContext(NbtOps.INSTANCE),
                        it.value
                    ).getOrThrow()
                }
                .forEach(::add)
        }

        tag.putInt("upgrade", upgrade)
        tag.putBoolean("detoxified", detoxified)
        tag.put("contents", contentTag)
        tag.putInt("extend", extend)

    }



    companion object: FRegistry<BlockEntityType<*>> {
        private val LOGGER: Logger = LogUtils.getLogger()

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        val type: BlockEntityType<BottleBlockEntity> = BlockEntityType.Builder.of(
            ::BottleBlockEntity,
            BottleBlock.instance
        ).build(null)

        override val registryKey: ResourceKey<out Registry<BlockEntityType<*>>> = BuiltInRegistries.BLOCK_ENTITY_TYPE.key()
        val name = "Bottle".toRegistryName()
        override val location = name.toResourceLocation()
        override val holder: Holder<BlockEntityType<*>> = DeferredHolder.create(registryKey, location)

        override fun registerByHelper(helper: RegisterEvent.RegisterHelper<BlockEntityType<*>>) {
            helper.register(location, type)
        }


    }
}