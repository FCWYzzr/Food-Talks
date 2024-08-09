package indi.fcwyzzr.minecraft.f_lib.utils

import com.mojang.datafixers.kinds.App
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentContents
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.food.FoodProperties

fun buildDataComponentPatch(action: DataComponentPatch.Builder.() -> Unit): DataComponentPatch = DataComponentPatch
    .builder()
    .apply(action)
    .build()

fun buildComponent(content: ComponentContents, actions: MutableComponent.() -> Unit={}): Component {
    val component = MutableComponent.create(content)
    component.actions()
    return component
}

