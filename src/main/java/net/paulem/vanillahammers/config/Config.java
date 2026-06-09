package net.paulem.vanillahammers.config;

import ovh.paulem.arcana.config.ConfigData;
import ovh.paulem.arcana.config.ConfigEntry;

@ovh.paulem.arcana.config.Config
public class Config implements ConfigData {
    @ConfigEntry
    public boolean specialColors;
}
