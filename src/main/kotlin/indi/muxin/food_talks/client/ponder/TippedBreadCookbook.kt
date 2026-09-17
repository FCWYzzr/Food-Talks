package indi.muxin.food_talks.client.ponder

import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.common.block.BowlBlock
import indi.muxin.food_talks.common.block.FTBlocks
import indi.muxin.food_talks.common.item.FTItemHolders
import indi.muxin.food_talks.common.item.FTItems
import net.createmod.catnip.math.Pointing
import net.createmod.ponder.api.registration.MultiSceneBuilder
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3

fun tippedBreadCookbook(ps: PonderSceneRegistrationHelper<ResourceLocation>): MultiSceneBuilder = ps.forComponents(
    FTItemHolders.BOWL.key!!.location())
    .addStoryBoard("kitchen_scene"){ scene, util ->
        scene.title("scene.bread_tipping", "Bread Tipping")
        scene.configureBasePlate(0, 0, 3)
        scene.world().showSection(util.select().everywhere(), Direction.UP)

        scene.overlay().showText(4 * FoodTalks.TPS)
            .text("1")
        scene.idle(4 * FoodTalks.TPS + 5)

        scene.overlay().showControls(
            Vec3(1.5, 3.0, 1.5),
            Pointing.DOWN,
            5)
            .rightClick()
            .withItem(FTItems.BOWL.defaultInstance)
        scene.world().setBlock(
            BlockPos(1, 2, 1),
            FTBlocks.BOWL_BLOCK.defaultBlockState(), true)
        scene.idle(FoodTalks.TPS)
        scene.rotateCameraY(60.0f)

        scene.overlay().showText(15 * FoodTalks.TPS)
            .text("2")
        scene.overlay().showControls(
            Vec3(1.5, 3.0, 1.5),
            Pointing.DOWN,
            3 * FoodTalks.TPS - 2)
            .rightClick()
            .withItem(Items.RABBIT_STEW.defaultInstance)
        scene.world().modifyBlock(BlockPos(1, 2, 1), {
            it.setValue(BowlBlock.PROPERTY_FILL_LEVEL, 1)
        }, false)
        scene.idle(3 * FoodTalks.TPS)
        scene.overlay().showControls(
            Vec3(1.5, 3.0, 1.5),
            Pointing.DOWN,
            3 * FoodTalks.TPS - 2)
            .rightClick()
            .withItem(Items.POTION.defaultInstance)
        scene.world().modifyBlock(BlockPos(1, 2, 1), {
            it.setValue(BowlBlock.PROPERTY_FILL_LEVEL, 2)
        }, false)
        scene.idle(3 * FoodTalks.TPS)
        scene.overlay().showControls(
            Vec3(1.5, 3.0, 1.5),
            Pointing.DOWN,
            3 * FoodTalks.TPS - 2)
            .rightClick()
            .withItem(Items.OMINOUS_BOTTLE.defaultInstance)
        scene.world().modifyBlock(BlockPos(1, 2, 1), {
            it.setValue(BowlBlock.PROPERTY_FILL_LEVEL, 3)
        }, false)
        scene.idle(3 * FoodTalks.TPS)
        scene.overlay().showControls(
            Vec3(1.5, 3.0, 1.5),
            Pointing.DOWN,
            3 * FoodTalks.TPS - 2)
            .rightClick()
            .withItem(Items.HONEY_BOTTLE.defaultInstance)
        scene.world().modifyBlock(BlockPos(1, 2, 1), {
            it.setValue(BowlBlock.PROPERTY_FILL_LEVEL, 4)
        }, false)
        scene.idle(3 * FoodTalks.TPS)
        scene.overlay().showControls(
            Vec3(1.5, 3.0, 1.5),
            Pointing.DOWN,
            3 * FoodTalks.TPS - 2)
            .rightClick()
            .withItem(Items.SUGAR.defaultInstance)
        scene.idle(3 * FoodTalks.TPS)

        scene.overlay().showText(FoodTalks.TPS)
            .text("3")
            .attachKeyFrame()
        scene.idle(FoodTalks.TPS + 5)
        scene.overlay().showControls(
            Vec3(1.5, 3.0, 1.5),
            Pointing.DOWN,
            5)
            .rightClick()
        scene.world().modifyBlock(BlockPos(1, 2, 1), {
            it.setValue(BowlBlock.PROPERTY_FILL_LEVEL, 3)
        }, false)
        scene.idle(FoodTalks.TPS)

        scene.overlay().showText(FoodTalks.TPS)
            .text("4")
            .attachKeyFrame()
        scene.idle(2 * FoodTalks.TPS + 5)
        scene.overlay().showControls(
            Vec3(1.5, 3.0, 1.5),
            Pointing.DOWN,
            5)
            .rightClick()
            .withItem(Items.BREAD.defaultInstance)
        scene.world().modifyBlock(BlockPos(1, 2, 1), {
            it.setValue(BowlBlock.PROPERTY_FILL_LEVEL, 2)
        }, false)
        scene.world().createItemEntity(
            Vec3(1.5, 3.0, 1.5),
            Vec3(0.0, 0.05, 0.0),
            Items.BREAD.defaultInstance)
        scene.idle(2 * FoodTalks.TPS)
    }
