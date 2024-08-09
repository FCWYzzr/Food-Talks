package indi.fcwyzzr.minecraft.f_lib.registry

import indi.fcwyzzr.minecraft.food_talks.toRegistryName
import indi.fcwyzzr.minecraft.food_talks.toResourceLocation
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.registries.DeferredHolder

interface IFRegistry<T>: FRegistry<T> {
    var name: String

    override var location: ResourceLocation
    override var holder: Holder<T>

    fun initSubClass(){
        val sub = this
        sub.name = sub::class.simpleName!!.toRegistryName()
        sub.location = sub.name.toResourceLocation()
        sub.holder = DeferredHolder.create(sub.registryKey, sub.location)
    }
}