package indi.muxin.neoforged.utils

import com.mojang.serialization.Codec
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentContents
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.registries.datamaps.DataMapType

fun buildDataComponentPatch(action: DataComponentPatch.Builder.() -> Unit): DataComponentPatch {
    return DataComponentPatch.builder()
        .apply(action)
        .build()
}

fun buildComponent(content: ComponentContents, actions: MutableComponent.() -> Unit={}): Component {
    return buildMutableComponent(content, actions)
}

fun buildMutableComponent(content: ComponentContents, actions: MutableComponent.() -> Unit={}): MutableComponent {
    val component = MutableComponent.create(content)
    component.actions()
    return component
}

fun <T, R> buildDataMapType(registryKey: ResourceKey<Registry<R>>, id: ResourceLocation, codec: Codec<T>, actions: DataMapType.Builder<T, R>.() -> Unit={}) =
    DataMapType.builder(id, registryKey, codec).apply(actions).build() as DataMapType<R, T>
