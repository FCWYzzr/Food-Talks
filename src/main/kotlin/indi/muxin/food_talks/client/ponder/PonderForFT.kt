package indi.muxin.food_talks.client.ponder

import indi.muxin.food_talks.FoodTalks
import net.createmod.ponder.api.registration.PonderPlugin
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper
import net.minecraft.resources.ResourceLocation

object PonderForFT: PonderPlugin {
    override fun getModId(): String = FoodTalks.MOD_ID

    override fun registerScenes(helper: PonderSceneRegistrationHelper<ResourceLocation>) {
        sandwichCookbook(helper)
        cocktailCookbook(helper)
    }
}