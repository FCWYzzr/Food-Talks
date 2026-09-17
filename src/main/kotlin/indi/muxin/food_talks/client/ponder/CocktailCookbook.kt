package indi.muxin.food_talks.client.ponder

import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.common.block.BottleBlock
import indi.muxin.food_talks.common.block.FTBlocks
import indi.muxin.food_talks.common.item.Cocktail
import indi.muxin.food_talks.common.item.FTItemHolders
import net.createmod.catnip.math.Pointing
import net.createmod.ponder.api.registration.MultiSceneBuilder
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3

fun cocktailCookbook(ps: PonderSceneRegistrationHelper<ResourceLocation>): MultiSceneBuilder = ps.forComponents(
    FTItemHolders.GLASS_BOTTLE.key!!.location())
    .addStoryBoard("kitchen_scene") { scene, util ->
        scene.title("scene.cocktail_assembly", "Cocktail Assembly")
        scene.configureBasePlate(0, 0, 3)
        scene.world().showSection(util.select().everywhere(), Direction.UP)

        scene.overlay().showText(FoodTalks.TPS)
            .text("1")
        scene.idle(5)

        scene.overlay().showControls(
            Vec3(1.5, 3.0, 1.5),
            Pointing.DOWN,
            5)
            .rightClick()
            .withItem(Items.GLASS_BOTTLE.defaultInstance)
        scene.world().setBlock(
            BlockPos(1, 2, 1),
            FTBlocks.BOTTLE_BLOCK.defaultBlockState(), true)
        scene.idle(FoodTalks.TPS)

        scene.overlay().showText(4 * FoodTalks.TPS)
            .text("2")
            .attachKeyFrame()
        scene.idle(5)
        repeat(8){
            scene.overlay().showControls(
                Vec3(1.5, 3.0, 1.5),
                Pointing.DOWN,
                0)
                .rightClick()
                .withItem(Items.POTION.defaultInstance)
            scene.idle(5)
            scene.world().modifyBlock(BlockPos(1, 2, 1), {
                it.setValue(BottleBlock.PROPERTY_FILL_LEVEL, it.getValue(BottleBlock.PROPERTY_FILL_LEVEL) + 1)
            }, false)
        }
        scene.idle(2 * FoodTalks.TPS + 5)

        scene.overlay().showText(3 * FoodTalks.TPS)
            .text("3")
            .attachKeyFrame()
        scene.overlay().showControls(
            Vec3(1.5, 3.0, 1.5),
            Pointing.DOWN,
            2 * FoodTalks.TPS)
            .rightClick()
            .withItem(Items.REDSTONE.defaultInstance)
        scene.idle(3 * FoodTalks.TPS + 5)

        scene.overlay().showText(3 * FoodTalks.TPS)
            .text("4")
        scene.overlay().showControls(
            Vec3(1.5, 3.0, 1.5),
            Pointing.DOWN,
            2 * FoodTalks.TPS)
            .rightClick()
            .withItem(Items.GLOWSTONE_DUST.defaultInstance)
        scene.idle(3 * FoodTalks.TPS + 5)

        scene.overlay().showText(3 * FoodTalks.TPS)
            .text("5")
        scene.overlay().showControls(
            Vec3(1.5, 3.0, 1.5),
            Pointing.DOWN,
            2 * FoodTalks.TPS)
            .rightClick()
            .withItem(Items.HONEY_BOTTLE.defaultInstance)
        scene.idle(3 * FoodTalks.TPS + 5)

        val cocktail = Cocktail.defaultInstance
        scene.overlay().showText(2 * FoodTalks.TPS)
            .text("6")
            .attachKeyFrame()
        scene.idle(5)

        scene.overlay().showControls(
            Vec3(1.5, 3.0, 1.5),
            Pointing.DOWN,
            FoodTalks.TPS / 2)
            .rightClick()
            .whileSneaking()
        scene.idle(FoodTalks.TPS / 2)
        scene.world().setBlock(BlockPos(1, 2, 1), Blocks.AIR.defaultBlockState(), true)
        scene.world().createItemEntity(
            Vec3(1.5, 2.0, 1.5),
            Vec3(0.0, 0.1, 0.0),
            cocktail)
        scene.idle(2 * FoodTalks.TPS)
    }