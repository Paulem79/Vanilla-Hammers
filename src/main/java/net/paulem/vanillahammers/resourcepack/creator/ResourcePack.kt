package net.paulem.krimson.resourcepack.creator

import net.paulem.vanillahammers.hammers.Hammer
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
    val zipFile = File(dataFolder, "hammer_resource_pack.zip")
    val deleted = zipFile.delete()

    if (!deleted) {
        println("No existing resource pack zip to delete.")
    }

    val tmpDir = dataFolder.resolve("tmp")
    tmpDir.deleteRecursively()
    tmpDir.mkdirs()

    val pack = resourcePack {
        meta {
            description = "§eHammer Resource Pack"
            format = packFormat
            outputDir = tmpDir
        }

        assetResolutionStrategy = ResourceAssetResolutionStrategy(this::class.java)
    }

    for (material in Hammer.HAMMERS.keys()) {
        val hammer = Hammer.HAMMERS.getOrNull(material) ?: continue

        println("Adding hammer $material, $hammer")

        val modelKey = hammer.getModelKey()
        println("Model key: $modelKey")
        if(modelKey == null) continue

        val textureKey = Key(modelKey.namespace, "item/" + modelKey.key)
        println("Creating model for $material, $modelKey, $textureKey")
        createHammerModel(pack, Key(modelKey.namespace, modelKey.key), textureKey)
    }

    pack.save(deleteOld = true)

    pack.createZip(zipFile)
    tmpDir.deleteRecursively()

    return zipFile
}