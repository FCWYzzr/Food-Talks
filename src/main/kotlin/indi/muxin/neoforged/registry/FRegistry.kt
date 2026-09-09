package indi.muxin.neoforged.registry

import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.registries.RegisterEvent

interface FRegistry<registryT> {
    val location: ResourceLocation
    val registryKey: ResourceKey<out Registry<registryT>>
    val holder: Holder<registryT>
    @Suppress("UNCHECKED_CAST")
    val instance: registryT get() = this as registryT

    infix fun registerTo(ev: RegisterEvent){
        @Suppress("TYPE_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        ev.register(registryKey) {
            it.register(location, instance)
        }
    }
}
