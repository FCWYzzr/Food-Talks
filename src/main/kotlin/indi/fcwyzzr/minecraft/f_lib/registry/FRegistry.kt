package indi.fcwyzzr.minecraft.f_lib.registry

import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.registries.RegisterEvent
import net.neoforged.neoforge.registries.RegisterEvent.RegisterHelper

interface FRegistry<registryT> {

    val location: ResourceLocation

    val holder: Holder<registryT>

    val registryKey: ResourceKey<out Registry<registryT>>

    fun registerByHelper(helper: RegisterHelper<registryT>){
        @Suppress("UNCHECKED_CAST")
        helper.register(this.location, (this as registryT)!!)
    }
}

infix fun <BuiltinT> RegisterEvent.doRegister(
    instance: FRegistry<BuiltinT>
){
    register(instance.registryKey){ helper ->
        instance.registerByHelper(helper)
    }
}

