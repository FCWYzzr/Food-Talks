package indi.muxin.food_talks.client.ponder

import indi.muxin.food_talks.FoodTalks
import indi.muxin.food_talks.common.block.PlateBlockEntity
import indi.muxin.food_talks.common.item.Plate
import indi.muxin.food_talks.common.item.Sandwich
import net.createmod.catnip.math.Pointing
import net.createmod.ponder.api.registration.MultiSceneBuilder
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper
import net.minecraft.client.resources.language.I18n
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3

fun sandwichCookbook(ps: PonderSceneRegistrationHelper<ResourceLocation>): MultiSceneBuilder = ps.forComponents(Plate.location)
    .addStoryBoard("plate/sandwich_assembly"){ scene, util ->
        scene.title("scene.sandwich_assembly", "Sandwich Assembly")
        scene.configureBasePlate(0, 0, 2)
        scene.world().showSection(util.select().everywhere(), Direction.UP)

        scene.overlay().showText(2 * FoodTalks.TPS)
            .text("1")
        scene.idle(2 * FoodTalks.TPS + 5)

        val putItem = {item: ItemStack, duration: Int ->
            scene.overlay().showControls(
                Vec3(1.5, 3.0, 1.5),
                Pointing.DOWN,
                0)
                .rightClick()
                .withItem(item)
            scene.world().modifyBlockEntity(
                BlockPos(1, 2, 1), PlateBlockEntity::class.java){
                it.putItem(item)
            }
            scene.idle(duration)
        }
        scene.overlay().showText(2 * FoodTalks.TPS)
            .text("2")
            .attachKeyFrame()
        scene.idle(FoodTalks.TPS / 2)
        putItem(Items.BREAD.defaultInstance, 3 * FoodTalks.TPS / 2)
        scene.overlay().showText(3 * FoodTalks.TPS)
            .text(I18n.get("3"))
        putItem(Items.COOKED_BEEF.defaultInstance, FoodTalks.TPS / 2)
        putItem(Items.COOKED_MUTTON.defaultInstance, FoodTalks.TPS / 2)
        putItem(Items.SWEET_BERRIES.defaultInstance, FoodTalks.TPS / 2)
        putItem(Items.BAKED_POTATO.defaultInstance, FoodTalks.TPS / 2)
        putItem(Items.CARROT.defaultInstance, FoodTalks.TPS / 2)
        scene.idle(FoodTalks.TPS)
        scene.overlay().showText(3 * FoodTalks.TPS / 2)
            .text(I18n.get("4"))
        putItem(Items.BREAD.defaultInstance, 3 * FoodTalks.TPS / 2)

        scene.overlay().showText(3 * FoodTalks.TPS)
            .text(I18n.get("5"))
            .attachKeyFrame()
        scene.idle(FoodTalks.TPS / 2)

        scene.overlay().showControls(
            Vec3(1.5, 3.0, 1.5),
            Pointing.DOWN,
            FoodTalks.TPS / 2)
            .rightClick()
        scene.world().modifyBlockEntity(
            BlockPos(1, 2, 1), PlateBlockEntity::class.java){
            it.clear()
        }
        val sandwichEntity = scene.world().createItemEntity(
            Vec3(1.5, 2.5, 1.5),
            Vec3(0.0, 0.1, 0.0),
            Sandwich.assemblyFromIngredientList(listOf(
                Items.BREAD.defaultInstance,
                Items.COOKED_BEEF.defaultInstance,
                Items.COOKED_MUTTON.defaultInstance,
                Items.SWEET_BERRIES.defaultInstance,
                Items.BAKED_POTATO.defaultInstance,
                Items.CARROT.defaultInstance,
                Items.BREAD.defaultInstance
            ))
        )
        scene.idleSeconds(3)

        scene.world().modifyEntity(sandwichEntity){
            it.kill()
        }

        scene.overlay().showText(5 * FoodTalks.TPS)
            .text(I18n.get("6"))
            .attachKeyFrame()
        scene.idle(FoodTalks.TPS / 2)

        putItem(Items.BREAD.defaultInstance, FoodTalks.TPS / 2)
        putItem(Items.BREAD.defaultInstance, FoodTalks.TPS / 2)
        putItem(Items.BREAD.defaultInstance, FoodTalks.TPS / 2)

        scene.overlay().showControls(
            Vec3(1.5, 3.0, 1.5),
            Pointing.DOWN,
            FoodTalks.TPS)
            .whileSneaking()
            .rightClick()
        scene.idle(FoodTalks.TPS)

        scene.world().modifyBlockEntity(
            BlockPos(1, 2, 1), PlateBlockEntity::class.java){
            it.clear()
        }
        repeat(3) {
            scene.world().createItemEntity(
                Vec3(1.5, 2.5, 1.5),
                Vec3(0.0, 0.1, 0.0),
                Items.BREAD.defaultInstance
            )
        }


        scene.markAsFinished()
    }
