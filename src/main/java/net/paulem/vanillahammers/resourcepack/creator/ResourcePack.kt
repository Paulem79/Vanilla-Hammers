package net.paulem.krimson.resourcepack.creator

import net.paulem.vanillahammers.VanillaHammers
import net.radstevee.packed.core.asset.impl.ResourceAssetResolutionStrategy
import net.radstevee.packed.core.item.definition.BasicItem
import net.radstevee.packed.core.item.definition.ItemDefinition
import net.radstevee.packed.core.key.Key
import net.radstevee.packed.core.pack.PackFormat
import net.radstevee.packed.core.pack.ResourcePack
import net.radstevee.packed.core.pack.ResourcePackBuilder.Companion.resourcePack
import java.io.File

fun createHammerModel(
    pack: ResourcePack,
    model: Key,
    texture: Key,
) {
    pack.addItemModel(model) {
        parent = "minecraft:item/handheld"
        primaryTexture(texture)
    }
    pack.addItemDefinition(ItemDefinition(model, BasicItem(model)))
}

fun main(dataFolder: File, packFormat: PackFormat): File {
    val zipFile = File(dataFolder, "krimson_resource_pack_v${packFormat}.zip")
    val deleted = zipFile.delete()

    if (!deleted) {
        println("No existing resource pack zip to delete.")
    }

    val tmpDir = dataFolder.resolve("tmp")
    tmpDir.deleteRecursively()
    tmpDir.mkdirs()

    val pack = resourcePack {
        meta {
            description = "§eKrimson Resource Pack"
            format = packFormat
            outputDir = tmpDir
        }

        assetResolutionStrategy = ResourceAssetResolutionStrategy(this::class.java)
    }

    val modelPath = VanillaHammers.HAMMER_KEY
    val texturePath = VanillaHammers.HAMMER_TEXTURE_KEY
    createHammerModel(pack, Key(modelPath.namespace, modelPath.key), Key(texturePath.namespace, texturePath.key))

    pack.save(deleteOld = true)

    pack.createZip(zipFile)
    tmpDir.deleteRecursively()

    return zipFile
}