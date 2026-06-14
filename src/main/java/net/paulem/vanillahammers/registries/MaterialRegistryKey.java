package net.paulem.vanillahammers.registries;

import org.bukkit.Material;
import ovh.paulem.arcana.registry.RegistryKey;

public class MaterialRegistryKey implements RegistryKey<Material> {
    private final Material material;

    public MaterialRegistryKey(Material material) {
        this.material = material;
    }

    @Override
    public Material getKey() {
        return material;
    }
}
