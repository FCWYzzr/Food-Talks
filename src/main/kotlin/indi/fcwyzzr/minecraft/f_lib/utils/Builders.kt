package indi.fcwyzzr.minecraft.f_lib.utils

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentContents
import net.minecraft.network.chat.MutableComponent

fun buildDataComponentPatch(action: DataComponentPatch.Builder.() -> Unit): DataComponentPatch {
    return DataComponentPatch.builder()
        .apply(action)
        .build()
}

fun buildComponent(content: ComponentContents, actions: MutableComponent.() -> Unit={}): Component {
    val component = MutableComponent.create(content)
    component.actions()
    return component
}


